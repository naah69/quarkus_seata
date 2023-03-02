package io.quarkiverse.seata.config;

import static io.quarkiverse.seata.config.StarterConstants.PROPERTY_BEAN_INSTANCE_MAP;
import static io.quarkiverse.seata.config.StarterConstants.PROPERTY_BEAN_MAP;
import static io.quarkiverse.seata.config.StarterConstants.SEATA_PREFIX;
import static io.quarkiverse.seata.config.StarterConstants.SERVICE_PREFIX;
import static io.quarkiverse.seata.config.StarterConstants.SPECIAL_KEY_GROUPLIST;
import static io.quarkiverse.seata.config.StarterConstants.SPECIAL_KEY_SERVICE;
import static io.quarkiverse.seata.config.StarterConstants.SPECIAL_KEY_VGROUP_MAPPING;
import static io.seata.common.util.StringFormatUtils.DOT;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.seata.common.exception.ShouldNeverHappenException;
import io.seata.config.Configuration;
import io.seata.config.ExtConfigurationProvider;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

public class SeataConfigProvider implements ExtConfigurationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeataConfigProvider.class);

    private static final String INTERCEPT_METHOD_PREFIX = "get";

    private static final Function optionalMap = v -> {
        if (v instanceof Optional) {
            return ((Optional) v).orElse(null);
        }
        return v;
    };

    @Override
    public Configuration provide(Configuration originalConfiguration) {
        DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<Configuration> intercept = new ByteBuddy()
                .subclass(Configuration.class)
                .method(ElementMatchers.any())
                .intercept(InvocationHandlerAdapter.of((proxy, method, args) -> {
                    if (method.getName().startsWith(INTERCEPT_METHOD_PREFIX) && args.length > 0) {
                        Object result;
                        String rawDataId = (String) args[0];
                        result = originalConfiguration.getConfigFromSys(rawDataId);
                        if (null == result) {
                            if (args.length == 1) {
                                result = get(convertDataId(rawDataId));
                            } else {
                                result = get(convertDataId(rawDataId), args[1]);
                            }
                        }
                        if (result != null) {
                            // If the return type is String,need to convert the object to string
                            if (method.getReturnType().equals(String.class)) {
                                return String.valueOf(result);
                            }
                            return result;
                        }
                    }
                    return method.invoke(originalConfiguration, args);
                }));
        try {
            return intercept.make().load(Configuration.class.getClassLoader())
                    .getLoaded().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Object get(String dataId, Object defaultValue) throws IllegalAccessException, InstantiationException {
        Object result = get(dataId);
        if (result == null) {
            return defaultValue;
        }
        return result;
    }

    private Object get(String dataId) throws IllegalAccessException {
        String propertyPrefix = getPropertyPrefix(dataId);
        String propertySuffix = getPropertySuffix(dataId);
        Class<?> propertyClass = PROPERTY_BEAN_MAP.get(propertyPrefix);
        Object valueObject = null;
        if (propertyClass != null) {
            valueObject = getFieldValue(
                    Objects.requireNonNull(PROPERTY_BEAN_INSTANCE_MAP.computeIfAbsent(propertyPrefix, k -> {
                        try {
                            return propertyClass.newInstance();
                        } catch (InstantiationException | IllegalAccessException e) {
                            LOGGER.error("PropertyClass for prefix: [" + propertyPrefix
                                    + "] should not be null. error :" + e.getMessage(), e);
                        }
                        return null;
                    })), propertySuffix, dataId);
        } else {
            throw new ShouldNeverHappenException("PropertyClass for prefix: [" + propertyPrefix + "] should not be null.");
        }

        return valueObject;
    }

    /**
     * get field value
     *
     * @param object
     * @param fieldName
     * @param dataId
     *
     * @return java.lang.Object
     *
     * @author xingfudeshi@gmail.com
     */
    private Object getFieldValue(Object object, String fieldName, String dataId) throws IllegalAccessException {
        Optional<Field> fieldOptional = Stream.of(object.getClass().getDeclaredFields())
                .filter(f -> f.getName().equalsIgnoreCase(fieldName)).findAny();
        if (fieldOptional.isPresent()) {
            Field field = fieldOptional.get();
            if (Objects.equals(field.getType(), Map.class)) {
                return getConfig(dataId, null, String.class);
            }
            Object defaultValue = field.get(object);
            return getConfig(dataId, defaultValue, field.getType());
        }
        return null;
    }

    /**
     * convert data id
     *
     * @param rawDataId
     *
     * @return dataId
     */
    private String convertDataId(String rawDataId) {
        if (rawDataId.endsWith(SPECIAL_KEY_GROUPLIST)) {
            String suffix = StringUtils.removeStart(StringUtils.removeEnd(rawDataId, DOT + SPECIAL_KEY_GROUPLIST),
                    SPECIAL_KEY_SERVICE + DOT);
            // change the format of default.grouplist to grouplist.default
            return SERVICE_PREFIX + DOT + SPECIAL_KEY_GROUPLIST + DOT + suffix;
        }
        return SEATA_PREFIX + DOT + rawDataId;
    }

    /**
     * Get property prefix
     *
     * @param dataId
     *
     * @return propertyPrefix
     */
    private String getPropertyPrefix(String dataId) {
        if (dataId.contains(SPECIAL_KEY_VGROUP_MAPPING)) {
            return SERVICE_PREFIX;
        }
        if (dataId.contains(SPECIAL_KEY_GROUPLIST)) {
            return SERVICE_PREFIX;
        }
        return StringUtils.substringBeforeLast(dataId, String.valueOf(DOT));
    }

    /**
     * Get property suffix
     *
     * @param dataId
     *
     * @return propertySuffix
     */
    private String getPropertySuffix(String dataId) {
        if (dataId.contains(SPECIAL_KEY_VGROUP_MAPPING)) {
            return SPECIAL_KEY_VGROUP_MAPPING;
        }
        if (dataId.contains(SPECIAL_KEY_GROUPLIST)) {
            return SPECIAL_KEY_GROUPLIST;
        }
        return StringUtils.substringAfterLast(dataId, String.valueOf(DOT));
    }

    /**
     * get spring config
     *
     * @param dataId data id
     * @param defaultValue default value
     * @param type type
     *
     * @return object
     */
    private Object getConfig(String dataId, Object defaultValue, Class<?> type) {

        Optional value = ConfigProvider.getConfig().getOptionalValue(dataId, type).map(optionalMap);
        if (value.isEmpty()) {
            value = ConfigProvider.getConfig()
                    .getOptionalValue(io.seata.common.util.StringUtils.hump2Line(dataId), type)
                    .map(optionalMap);
        }
        if (value.isEmpty() && defaultValue != null) {
            return ConfigProvider.getConfig()
                    .getConverter(type)
                    .map(c -> c.convert(defaultValue.toString()))
                    .orElse(null);
        }
        return value.orElse(null);
    }

}
