package io.quarkiverse.seata.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ReflectionUtils
 *
 * @author nayan
 * @date 2022/7/28 3:03 PM
 */
public class ReflectionUtils {
    private static final Cache<Class<?>, Method[]> declaredMethodsCache = CacheBuilder.newBuilder().build();
    private static final Method[] EMPTY_METHOD_ARRAY = new Method[0];



    public static Method findMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
        assertNotNull(clazz, "Class must not be null");
        assertNotNull(name, "Method name must not be null");
        Class<?> searchType = clazz;
        while (searchType != null) {
            Method[] methods = (searchType.isInterface() ? searchType.getMethods() :
                    getDeclaredMethods(searchType, false));
            for (Method method : methods) {
                if (name.equals(method.getName()) && (paramTypes == null || hasSameParams(method, paramTypes))) {
                    return method;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    private static Method[] getDeclaredMethods(Class<?> clazz, boolean defensive) {
        assertNotNull(clazz, "Class must not be null");
        try {
            Method[] result = declaredMethodsCache.get(clazz, () -> {
                Method[] result1 = null;
                Method[] declaredMethods = clazz.getDeclaredMethods();
                List<Method> defaultMethods = findConcreteMethodsOnInterfaces(clazz);
                if (defaultMethods != null) {
                    result1 = new Method[declaredMethods.length + defaultMethods.size()];
                    System.arraycopy(declaredMethods, 0, result1, 0, declaredMethods.length);
                    int index = declaredMethods.length;
                    for (Method defaultMethod : defaultMethods) {
                        result1[index] = defaultMethod;
                        index++;
                    }
                } else {
                    result1 = declaredMethods;
                }
                return  (result1.length == 0 ? EMPTY_METHOD_ARRAY : result1);
            });
            return (result.length == 0 || !defensive) ? result : result.clone();
        } catch (Throwable e) {
            throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() +
                    "] from ClassLoader [" + clazz.getClassLoader() + "]", e);
        }
    }



    private static boolean hasSameParams(Method method, Class<?>[] paramTypes) {
        return (paramTypes.length == method.getParameterCount() &&
                Arrays.equals(paramTypes, method.getParameterTypes()));
    }


    private static List<Method> findConcreteMethodsOnInterfaces(Class<?> clazz) {
        List<Method> result = null;
        for (Class<?> ifc : clazz.getInterfaces()) {
            for (Method ifcMethod : ifc.getMethods()) {
                if (!Modifier.isAbstract(ifcMethod.getModifiers())) {
                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add(ifcMethod);
                }
            }
        }
        return result;
    }

    public static void assertNotNull(Object clazz, String message) {
        if (clazz == null) {
            throw new IllegalArgumentException(message);
        }
    }

}
