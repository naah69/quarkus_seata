/*
 *  Copyright 1999-2019 Seata.io Group.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.quarkiverse.seata.interceptor;

import static io.seata.common.DefaultValues.DEFAULT_DISABLE_GLOBAL_TRANSACTION;
import static io.seata.common.DefaultValues.DEFAULT_GLOBAL_TRANSACTION_TIMEOUT;
import static io.seata.common.DefaultValues.DEFAULT_TM_DEGRADE_CHECK;
import static io.seata.common.DefaultValues.DEFAULT_TM_DEGRADE_CHECK_ALLOW_TIMES;
import static io.seata.common.DefaultValues.DEFAULT_TM_DEGRADE_CHECK_PERIOD;
import static io.seata.common.DefaultValues.TM_INTERCEPTOR_ORDER;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

import io.quarkiverse.seata.annotation.GlobalLock;
import io.quarkiverse.seata.annotation.GlobalTransactional;
import io.quarkiverse.seata.event.DegradeCheckEvent;
import io.quarkiverse.seata.transaction.AspectTransactional;
import io.quarkiverse.seata.utils.ClassUtils;
import io.quarkus.arc.Priority;
import io.seata.common.exception.ShouldNeverHappenException;
import io.seata.common.thread.NamedThreadFactory;
import io.seata.common.util.StringUtils;
import io.seata.config.ConfigurationCache;
import io.seata.config.ConfigurationChangeEvent;
import io.seata.config.ConfigurationChangeListener;
import io.seata.config.ConfigurationFactory;
import io.seata.core.constants.ConfigurationKeys;
import io.seata.core.event.EventBus;
import io.seata.core.event.GuavaEventBus;
import io.seata.core.model.GlobalLockConfig;
import io.seata.rm.GlobalLockExecutor;
import io.seata.rm.GlobalLockTemplate;
import io.seata.tm.TransactionManagerHolder;
import io.seata.tm.api.DefaultFailureHandlerImpl;
import io.seata.tm.api.FailureHandler;
import io.seata.tm.api.TransactionalExecutor;
import io.seata.tm.api.TransactionalTemplate;
import io.seata.tm.api.transaction.NoRollbackRule;
import io.seata.tm.api.transaction.RollbackRule;
import io.seata.tm.api.transaction.TransactionInfo;

/**
 * The type Global transactional interceptor.
 *
 * @author slievrly
 */
@GlobalTransactional
@Interceptor
@Priority(TM_INTERCEPTOR_ORDER)
public class GlobalTransactionalInterceptor implements ConfigurationChangeListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalTransactionalInterceptor.class);
    private static final FailureHandler DEFAULT_FAIL_HANDLER = new DefaultFailureHandlerImpl();

    private final TransactionalTemplate transactionalTemplate = new TransactionalTemplate();
    private final GlobalLockTemplate globalLockTemplate = new GlobalLockTemplate();

    public FailureHandler failureHandler;

    private volatile boolean disable;
    protected AspectTransactional aspectTransactional;
    private static int degradeCheckPeriod;
    private static volatile boolean degradeCheck;
    private static int degradeCheckAllowTimes;
    private static volatile Integer degradeNum = 0;
    private static volatile Integer reachNum = 0;
    private static final EventBus EVENT_BUS = new GuavaEventBus("degradeCheckEventBus", true);
    private static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1,
            new NamedThreadFactory("degradeCheckWorker", 1, true));

    //region DEFAULT_GLOBAL_TRANSACTION_TIMEOUT

    private static int defaultGlobalTransactionTimeout = 0;

    private void initDefaultGlobalTransactionTimeout() {
        if (GlobalTransactionalInterceptor.defaultGlobalTransactionTimeout <= 0) {
            int defaultGlobalTransactionTimeout;
            try {
                defaultGlobalTransactionTimeout = ConfigurationFactory.getInstance().getInt(
                        ConfigurationKeys.DEFAULT_GLOBAL_TRANSACTION_TIMEOUT, DEFAULT_GLOBAL_TRANSACTION_TIMEOUT);
            } catch (Exception e) {
                LOGGER.error("Illegal global transaction timeout value: " + e.getMessage());
                defaultGlobalTransactionTimeout = DEFAULT_GLOBAL_TRANSACTION_TIMEOUT;
            }
            if (defaultGlobalTransactionTimeout <= 0) {
                LOGGER.warn("Global transaction timeout value '{}' is illegal, and has been reset to the default value '{}'",
                        defaultGlobalTransactionTimeout, DEFAULT_GLOBAL_TRANSACTION_TIMEOUT);
                defaultGlobalTransactionTimeout = DEFAULT_GLOBAL_TRANSACTION_TIMEOUT;
            }
            GlobalTransactionalInterceptor.defaultGlobalTransactionTimeout = defaultGlobalTransactionTimeout;
        }
    }

    //endregion

    /**
     * Instantiates a new Global transactional interceptor.
     *
     */
    public GlobalTransactionalInterceptor() {
        Instance<FailureHandler> failureHandlerInstance = CDI.current().select(FailureHandler.class);
        if (failureHandlerInstance.isResolvable()) {
            this.failureHandler = failureHandlerInstance.get();
        } else if (failureHandlerInstance.isAmbiguous()) {
            this.failureHandler = failureHandlerInstance.stream().findFirst().get();
        } else {
            this.failureHandler = DEFAULT_FAIL_HANDLER;
        }
        this.disable = ConfigurationFactory.getInstance().getBoolean(ConfigurationKeys.DISABLE_GLOBAL_TRANSACTION,
                DEFAULT_DISABLE_GLOBAL_TRANSACTION);
        //todo transcation order
        //        this.order =
        //                ConfigurationFactory.getInstance().getInt(ConfigurationKeys.TM_INTERCEPTOR_ORDER, TM_INTERCEPTOR_ORDER);
        degradeCheck = ConfigurationFactory.getInstance().getBoolean(ConfigurationKeys.CLIENT_DEGRADE_CHECK,
                DEFAULT_TM_DEGRADE_CHECK);
        if (degradeCheck) {
            ConfigurationCache.addConfigListener(ConfigurationKeys.CLIENT_DEGRADE_CHECK, this);
            degradeCheckPeriod = ConfigurationFactory.getInstance()
                    .getInt(ConfigurationKeys.CLIENT_DEGRADE_CHECK_PERIOD, DEFAULT_TM_DEGRADE_CHECK_PERIOD);
            degradeCheckAllowTimes = ConfigurationFactory.getInstance()
                    .getInt(ConfigurationKeys.CLIENT_DEGRADE_CHECK_ALLOW_TIMES, DEFAULT_TM_DEGRADE_CHECK_ALLOW_TIMES);
            EVENT_BUS.register(this);
            if (degradeCheckPeriod > 0 && degradeCheckAllowTimes > 0) {
                startDegradeCheck();
            }
        }
        this.initDefaultGlobalTransactionTimeout();
    }

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Throwable {
        Class<?> targetClass = context.getTarget().getClass();
        Method specificMethod = ClassUtils.getMostSpecificMethod(context.getMethod(), targetClass);
        if (specificMethod != null && !specificMethod.getDeclaringClass().equals(Object.class)) {
            //todo 测试
            //            final Method method = BridgeMethodResolver.findBridgedMethod(specificMethod);
            final Method method = specificMethod;
            final GlobalTransactional globalTransactionalAnnotation = getAnnotation(method, targetClass,
                    GlobalTransactional.class);
            final GlobalLock globalLockAnnotation = getAnnotation(method, targetClass, GlobalLock.class);
            boolean localDisable = disable || (degradeCheck && degradeNum >= degradeCheckAllowTimes);
            if (!localDisable) {
                if (globalTransactionalAnnotation != null || this.aspectTransactional != null) {
                    AspectTransactional transactional;
                    if (globalTransactionalAnnotation != null) {
                        transactional = new AspectTransactional(globalTransactionalAnnotation.timeoutMills(),
                                globalTransactionalAnnotation.name(), globalTransactionalAnnotation.rollbackFor(),
                                globalTransactionalAnnotation.noRollbackForClassName(),
                                globalTransactionalAnnotation.noRollbackFor(),
                                globalTransactionalAnnotation.noRollbackForClassName(),
                                globalTransactionalAnnotation.propagation(),
                                globalTransactionalAnnotation.lockRetryInterval(),
                                globalTransactionalAnnotation.lockRetryTimes());
                    } else {
                        transactional = this.aspectTransactional;
                    }
                    return handleGlobalTransaction(context, transactional);
                } else if (globalLockAnnotation != null) {
                    return handleGlobalLock(context, globalLockAnnotation);
                }
            }
        }
        return context.proceed();
    }

    private Object handleGlobalLock(final InvocationContext context, final GlobalLock globalLockAnno) throws Throwable {
        return globalLockTemplate.execute(new GlobalLockExecutor() {
            @Override
            public Object execute() throws Throwable {
                return context.proceed();
            }

            @Override
            public GlobalLockConfig getGlobalLockConfig() {
                GlobalLockConfig config = new GlobalLockConfig();
                config.setLockRetryInterval(globalLockAnno.lockRetryInterval());
                config.setLockRetryTimes(globalLockAnno.lockRetryTimes());
                return config;
            }
        });
    }

    Object handleGlobalTransaction(final InvocationContext context,
            final AspectTransactional aspectTransactional) throws Throwable {
        boolean succeed = true;
        try {
            return transactionalTemplate.execute(new TransactionalExecutor() {
                @Override
                public Object execute() throws Throwable {
                    return context.proceed();
                }

                public String name() {
                    String name = aspectTransactional.getName();
                    if (!StringUtils.isNullOrEmpty(name)) {
                        return name;
                    }
                    return formatMethod(context.getMethod());
                }

                @Override
                public TransactionInfo getTransactionInfo() {
                    // reset the value of timeout
                    int timeout = aspectTransactional.getTimeoutMills();
                    if (timeout <= 0 || timeout == DEFAULT_GLOBAL_TRANSACTION_TIMEOUT) {
                        timeout = defaultGlobalTransactionTimeout;
                    }

                    TransactionInfo transactionInfo = new TransactionInfo();
                    transactionInfo.setTimeOut(timeout);
                    transactionInfo.setName(name());
                    transactionInfo.setPropagation(aspectTransactional.getPropagation());
                    transactionInfo.setLockRetryInterval(aspectTransactional.getLockRetryInterval());
                    transactionInfo.setLockRetryTimes(aspectTransactional.getLockRetryTimes());
                    Set<RollbackRule> rollbackRules = new LinkedHashSet<>();
                    for (Class<?> rbRule : aspectTransactional.getRollbackFor()) {
                        rollbackRules.add(new RollbackRule(rbRule));
                    }
                    for (String rbRule : aspectTransactional.getRollbackForClassName()) {
                        rollbackRules.add(new RollbackRule(rbRule));
                    }
                    for (Class<?> rbRule : aspectTransactional.getNoRollbackFor()) {
                        rollbackRules.add(new NoRollbackRule(rbRule));
                    }
                    for (String rbRule : aspectTransactional.getNoRollbackForClassName()) {
                        rollbackRules.add(new NoRollbackRule(rbRule));
                    }
                    transactionInfo.setRollbackRules(rollbackRules);
                    return transactionInfo;
                }
            });
        } catch (TransactionalExecutor.ExecutionException e) {
            TransactionalExecutor.Code code = e.getCode();
            switch (code) {
                case RollbackDone:
                    throw e.getOriginalException();
                case BeginFailure:
                    succeed = false;
                    failureHandler.onBeginFailure(e.getTransaction(), e.getCause());
                    throw e.getCause();
                case CommitFailure:
                    succeed = false;
                    failureHandler.onCommitFailure(e.getTransaction(), e.getCause());
                    throw e.getCause();
                case RollbackFailure:
                    failureHandler.onRollbackFailure(e.getTransaction(), e.getOriginalException());
                    throw e.getOriginalException();
                case RollbackRetrying:
                    failureHandler.onRollbackRetrying(e.getTransaction(), e.getOriginalException());
                    throw e.getOriginalException();
                default:
                    throw new ShouldNeverHappenException(String.format("Unknown TransactionalExecutor.Code: %s", code));
            }
        } finally {
            if (degradeCheck) {
                EVENT_BUS.post(new DegradeCheckEvent(succeed));
            }
        }
    }

    public <T extends Annotation> T getAnnotation(Method method, Class<?> targetClass, Class<T> annotationClass) {
        return Optional.ofNullable(method).map(m -> m.getAnnotation(annotationClass))
                .orElse(Optional.ofNullable(targetClass).map(t -> t.getAnnotation(annotationClass)).orElse(null));
    }

    private String formatMethod(Method method) {
        StringBuilder sb = new StringBuilder(method.getName()).append("(");

        Class<?>[] params = method.getParameterTypes();
        int in = 0;
        for (Class<?> clazz : params) {
            sb.append(clazz.getName());
            if (++in < params.length) {
                sb.append(", ");
            }
        }
        return sb.append(")").toString();
    }

    @Override
    public void onChangeEvent(ConfigurationChangeEvent event) {
        if (ConfigurationKeys.DISABLE_GLOBAL_TRANSACTION.equals(event.getDataId())) {
            LOGGER.info("{} config changed, old value:{}, new value:{}", ConfigurationKeys.DISABLE_GLOBAL_TRANSACTION,
                    disable, event.getNewValue());
            disable = Boolean.parseBoolean(event.getNewValue().trim());
        } else if (ConfigurationKeys.CLIENT_DEGRADE_CHECK.equals(event.getDataId())) {
            degradeCheck = Boolean.parseBoolean(event.getNewValue());
            if (!degradeCheck) {
                degradeNum = 0;
            }
        }
    }

    /**
     * auto upgrade service detection
     */
    private static void startDegradeCheck() {
        executor.scheduleAtFixedRate(() -> {
            if (degradeCheck) {
                try {
                    String xid = TransactionManagerHolder.get().begin(null, null, "degradeCheck", 60000);
                    TransactionManagerHolder.get().commit(xid);
                    EVENT_BUS.post(new DegradeCheckEvent(true));
                } catch (Exception e) {
                    EVENT_BUS.post(new DegradeCheckEvent(false));
                }
            }
        }, degradeCheckPeriod, degradeCheckPeriod, TimeUnit.MILLISECONDS);
    }

    @Subscribe
    public static void onDegradeCheck(DegradeCheckEvent event) {
        if (event.isRequestSuccess()) {
            if (degradeNum >= degradeCheckAllowTimes) {
                reachNum++;
                if (reachNum >= degradeCheckAllowTimes) {
                    reachNum = 0;
                    degradeNum = 0;
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("the current global transaction has been restored");
                    }
                }
            } else if (degradeNum != 0) {
                degradeNum = 0;
            }
        } else {
            if (degradeNum < degradeCheckAllowTimes) {
                degradeNum++;
                if (degradeNum >= degradeCheckAllowTimes) {
                    if (LOGGER.isWarnEnabled()) {
                        LOGGER.warn("the current global transaction has been automatically downgraded");
                    }
                }
            } else if (reachNum != 0) {
                reachNum = 0;
            }
        }
    }

}
