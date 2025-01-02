/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.annotation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.parsing.FailFastProblemReporter;
import org.springframework.beans.factory.parsing.PassThroughSourceExtractor;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.beans.factory.parsing.SourceExtractor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ApplicationStartupAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ConfigurationClassEnhancer.EnhancedConfiguration;
import org.springframework.core.NativeDetector;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.core.metrics.StartupStep;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.*;

/**
 * {@link BeanFactoryPostProcessor} used for bootstrapping processing of
 * {@link Configuration @Configuration} classes.
 *
 * <p>Registered by default when using {@code <context:annotation-config/>} or
 * {@code <context:component-scan/>}. Otherwise, may be declared manually as
 * with any other {@link BeanFactoryPostProcessor}.
 *
 * <p>This post processor is priority-ordered as it is important that any
 * {@link Bean @Bean} methods declared in {@code @Configuration} classes have
 * their corresponding bean definitions registered before any other
 * {@code BeanFactoryPostProcessor} executes.
 *
 * 这段描述指出了 ConfigurationClassPostProcessor 在 Spring 框架中的作用及其重要性。
 * ConfigurationClassPostProcessor 是一个实现了 BeanFactoryPostProcessor 接口的类，
 * 它用于处理 @Configuration 类，并确保在 Spring 应用上下文初始化期间正确地处理这些配置类。下面是对其功能的详细说明：
 *
 * 主要作用
 * 处理 @Configuration 类：
 * ConfigurationClassPostProcessor 用于处理 @Configuration 类，这些类通过 @Bean 方法声明了 Bean 的定义。
 * 它会解析这些类，并将它们转换成相应的 BeanDefinition，然后注册到 BeanFactory 中。
 * 注册默认处理器：
 * 当你在 Spring 配置中使用 <context:annotation-config/> 或 <context:component-scan/> 标签时，
 * 默认情况下会注册 ConfigurationClassPostProcessor。这意味着框架会自动检测并处理 @Configuration 类。
 * 手动注册：
 * 如果你不使用上述标签，也可以像其他 BeanFactoryPostProcessor 实现一样，手动声明 ConfigurationClassPostProcessor。
 * 优先级顺序：
 * ConfigurationClassPostProcessor 在 BeanFactoryPostProcessor 中具有较高的优先级（通常是最高的）。
 * 这是因为它需要在其他 BeanFactoryPostProcessor 执行之前完成 @Configuration 类的处理，以确保 @Bean 方法声明的 Bean 能够被正确地注册。
 * 工作流程
 * 解析 @Configuration 类：
 * 当 Spring 应用上下文启动时，ConfigurationClassPostProcessor 会扫描所有的 @Configuration 类，并解析它们的 @Bean 方法。
 * 转换为 BeanDefinition：
 * 解析完 @Configuration 类后，ConfigurationClassPostProcessor 会将每个 @Bean 方法转换成一个 BeanDefinition。
 * 注册 BeanDefinition：
 * 将生成的 BeanDefinition 注册到 BeanFactory 中。这样，在后续的应用程序运行过程中，这些 Bean 就能够被正确地实例化和管理。
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Phillip Webb
 * @author Sam Brannen
 * @since 3.0
 */
public class ConfigurationClassPostProcessor implements BeanDefinitionRegistryPostProcessor,
        PriorityOrdered, ResourceLoaderAware, ApplicationStartupAware, BeanClassLoaderAware, EnvironmentAware {

    /**
     * A {@code BeanNameGenerator} using fully qualified class names as default bean names.
     * <p>This default for configuration-level import purposes may be overridden through
     * {@link #setBeanNameGenerator}. Note that the default for component scanning purposes
     * is a plain {@link AnnotationBeanNameGenerator#INSTANCE}, unless overridden through
     * {@link #setBeanNameGenerator} with a unified user-level bean name generator.
     *
     * @see #setBeanNameGenerator
     * @since 5.2
     */
    public static final AnnotationBeanNameGenerator IMPORT_BEAN_NAME_GENERATOR =
            FullyQualifiedAnnotationBeanNameGenerator.INSTANCE;

    private static final String IMPORT_REGISTRY_BEAN_NAME =
            ConfigurationClassPostProcessor.class.getName() + ".importRegistry";


    private final Log logger = LogFactory.getLog(getClass());

    private SourceExtractor sourceExtractor = new PassThroughSourceExtractor();

    private ProblemReporter problemReporter = new FailFastProblemReporter();

    @Nullable
    private Environment environment;

    private ResourceLoader resourceLoader = new DefaultResourceLoader();

    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory();

    private boolean setMetadataReaderFactoryCalled = false;

    private final Set<Integer> registriesPostProcessed = new HashSet<>();
    /**
     * TODO 干啥用的
     */
    private final Set<Integer> factoriesPostProcessed = new HashSet<>();

    @Nullable
    private ConfigurationClassBeanDefinitionReader reader;

    private boolean localBeanNameGeneratorSet = false;

    /* Using short class names as default bean names by default. */
    private BeanNameGenerator componentScanBeanNameGenerator = AnnotationBeanNameGenerator.INSTANCE;

    /* Using fully qualified class names as default bean names by default. */
    private BeanNameGenerator importBeanNameGenerator = IMPORT_BEAN_NAME_GENERATOR;

    private ApplicationStartup applicationStartup = ApplicationStartup.DEFAULT;


    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;  // within PriorityOrdered
    }

    /**
     * Set the {@link SourceExtractor} to use for generated bean definitions
     * that correspond to {@link Bean} factory methods.
     */
    public void setSourceExtractor(@Nullable SourceExtractor sourceExtractor) {
        this.sourceExtractor = (sourceExtractor != null ? sourceExtractor : new PassThroughSourceExtractor());
    }

    /**
     * Set the {@link ProblemReporter} to use.
     * <p>Used to register any problems detected with {@link Configuration} or {@link Bean}
     * declarations. For instance, an @Bean method marked as {@code final} is illegal
     * and would be reported as a problem. Defaults to {@link FailFastProblemReporter}.
     */
    public void setProblemReporter(@Nullable ProblemReporter problemReporter) {
        this.problemReporter = (problemReporter != null ? problemReporter : new FailFastProblemReporter());
    }

    /**
     * Set the {@link MetadataReaderFactory} to use.
     * <p>Default is a {@link CachingMetadataReaderFactory} for the specified
     * {@linkplain #setBeanClassLoader bean class loader}.
     */
    public void setMetadataReaderFactory(MetadataReaderFactory metadataReaderFactory) {
        Assert.notNull(metadataReaderFactory, "MetadataReaderFactory must not be null");
        this.metadataReaderFactory = metadataReaderFactory;
        this.setMetadataReaderFactoryCalled = true;
    }

    /**
     * Set the {@link BeanNameGenerator} to be used when triggering component scanning
     * from {@link Configuration} classes and when registering {@link Import}'ed
     * configuration classes. The default is a standard {@link AnnotationBeanNameGenerator}
     * for scanned components (compatible with the default in {@link ClassPathBeanDefinitionScanner})
     * and a variant thereof for imported configuration classes (using unique fully-qualified
     * class names instead of standard component overriding).
     * <p>Note that this strategy does <em>not</em> apply to {@link Bean} methods.
     * <p>This setter is typically only appropriate when configuring the post-processor as a
     * standalone bean definition in XML, e.g. not using the dedicated {@code AnnotationConfig*}
     * application contexts or the {@code <context:annotation-config>} element. Any bean name
     * generator specified against the application context will take precedence over any set here.
     *
     * @see AnnotationConfigApplicationContext#setBeanNameGenerator(BeanNameGenerator)
     * @see AnnotationConfigUtils#CONFIGURATION_BEAN_NAME_GENERATOR
     * @since 3.1.1
     */
    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        Assert.notNull(beanNameGenerator, "BeanNameGenerator must not be null");
        this.localBeanNameGeneratorSet = true;
        this.componentScanBeanNameGenerator = beanNameGenerator;
        this.importBeanNameGenerator = beanNameGenerator;
    }

    @Override
    public void setEnvironment(Environment environment) {
        Assert.notNull(environment, "Environment must not be null");
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        Assert.notNull(resourceLoader, "ResourceLoader must not be null");
        this.resourceLoader = resourceLoader;
        if (!this.setMetadataReaderFactoryCalled) {
            this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
        }
    }

    @Override
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
        if (!this.setMetadataReaderFactoryCalled) {
            this.metadataReaderFactory = new CachingMetadataReaderFactory(beanClassLoader);
        }
    }

    @Override
    public void setApplicationStartup(ApplicationStartup applicationStartup) {
        this.applicationStartup = applicationStartup;
    }

    /**
     * Derive further bean definitions from the configuration classes in the registry.
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        int registryId = System.identityHashCode(registry);
        if (this.registriesPostProcessed.contains(registryId)) {
            throw new IllegalStateException(
                    "postProcessBeanDefinitionRegistry already called on this post-processor against " + registry);
        }
        if (this.factoriesPostProcessed.contains(registryId)) {
            throw new IllegalStateException(
                    "postProcessBeanFactory already called on this post-processor against " + registry);
        }
        this.registriesPostProcessed.add(registryId);

        processConfigBeanDefinitions(registry);
    }

    /**
     * Prepare the Configuration classes for servicing bean requests at runtime
     * by replacing them with CGLIB-enhanced subclasses.
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        int factoryId = System.identityHashCode(beanFactory);
        if (this.factoriesPostProcessed.contains(factoryId)) {
            throw new IllegalStateException(
                    "postProcessBeanFactory already called on this post-processor against " + beanFactory);
        }
        this.factoriesPostProcessed.add(factoryId);
        if (!this.registriesPostProcessed.contains(factoryId)) {
            // BeanDefinitionRegistryPostProcessor hook apparently not supported...
            // Simply call processConfigurationClasses lazily at this point then.
            processConfigBeanDefinitions((BeanDefinitionRegistry) beanFactory);
        }

        enhanceConfigurationClasses(beanFactory);
        beanFactory.addBeanPostProcessor(new ImportAwareBeanPostProcessor(beanFactory));
    }

    /**
     * Build and validate a configuration model based on the registry of
     * {@link Configuration} classes.
     * @param registry DefaultListableBeanFactory
     */
    public void processConfigBeanDefinitions(BeanDefinitionRegistry registry) {
        List<BeanDefinitionHolder> configCandidates = new ArrayList<>();
        String[] candidateNames = registry.getBeanDefinitionNames();

        for (String beanName : candidateNames) {
            BeanDefinition beanDef = registry.getBeanDefinition(beanName);
            if (beanDef.getAttribute(ConfigurationClassUtils.CONFIGURATION_CLASS_ATTRIBUTE) != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Bean definition has already been processed as a configuration class: " + beanDef);
                }
            } else if (ConfigurationClassUtils.checkConfigurationClassCandidate(beanDef, this.metadataReaderFactory)) {
                configCandidates.add(new BeanDefinitionHolder(beanDef, beanName));
            }
        }

        // Return immediately if no @Configuration classes were found
        if (configCandidates.isEmpty()) {
            return;
        }

        // TODO Sort by previously determined @Order value, if applicable 排序
        configCandidates.sort((bd1, bd2) -> {
            // 顺序
            int i1 = ConfigurationClassUtils.getOrder(bd1.getBeanDefinition());
            int i2 = ConfigurationClassUtils.getOrder(bd2.getBeanDefinition());
            return Integer.compare(i1, i2);
        });

        // Detect any custom bean name generation strategy supplied through the enclosing application context
        SingletonBeanRegistry sbr = null;
        if (registry instanceof SingletonBeanRegistry) {
            sbr = (SingletonBeanRegistry) registry;
            if (!this.localBeanNameGeneratorSet) {
                BeanNameGenerator generator = (BeanNameGenerator) sbr.getSingleton(
                        AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR);
                if (generator != null) {
                    this.componentScanBeanNameGenerator = generator;
                    this.importBeanNameGenerator = generator;
                }
            }
        }

        if (this.environment == null) {
            this.environment = new StandardEnvironment();
        }

        // Parse each @Configuration class
        ConfigurationClassParser parser = new ConfigurationClassParser(
                this.metadataReaderFactory, this.problemReporter, this.environment,
                this.resourceLoader, this.componentScanBeanNameGenerator, registry);

        Set<BeanDefinitionHolder> candidates = new LinkedHashSet<>(configCandidates);
        Set<ConfigurationClass> alreadyParsed = new HashSet<>(configCandidates.size());
        do {
            StartupStep processConfig = this.applicationStartup.start("spring.context.config-classes.parse");
            // TODO ConfigurationClassParser解析ComponentScan包扫描
            parser.parse(candidates);
            parser.validate();

            Set<ConfigurationClass> configClasses = new LinkedHashSet<>(parser.getConfigurationClasses());
            configClasses.removeAll(alreadyParsed);

            // Read the model and create bean definitions based on its content
            if (this.reader == null) {
                // TODO ConfigurationClassBeanDefinitionReader
                this.reader = new ConfigurationClassBeanDefinitionReader(
                        registry, this.sourceExtractor, this.resourceLoader, this.environment,
                        this.importBeanNameGenerator, parser.getImportRegistry());
            }
            // TODO 解析Configuration修饰的配置类
            this.reader.loadBeanDefinitions(configClasses);
            alreadyParsed.addAll(configClasses);
            processConfig.tag("classCount", () -> String.valueOf(configClasses.size())).end();

            candidates.clear();
            if (registry.getBeanDefinitionCount() > candidateNames.length) {
                String[] newCandidateNames = registry.getBeanDefinitionNames();
                Set<String> oldCandidateNames = new HashSet<>(Arrays.asList(candidateNames));
                Set<String> alreadyParsedClasses = new HashSet<>();
                for (ConfigurationClass configurationClass : alreadyParsed) {
                    alreadyParsedClasses.add(configurationClass.getMetadata().getClassName());
                }
                for (String candidateName : newCandidateNames) {
                    if (!oldCandidateNames.contains(candidateName)) {
                        BeanDefinition bd = registry.getBeanDefinition(candidateName);
                        if (ConfigurationClassUtils.checkConfigurationClassCandidate(bd, this.metadataReaderFactory) &&
                                !alreadyParsedClasses.contains(bd.getBeanClassName())) {
                            candidates.add(new BeanDefinitionHolder(bd, candidateName));
                        }
                    }
                }
                candidateNames = newCandidateNames;
            }
        }
        while (!candidates.isEmpty());

        // Register the ImportRegistry as a bean in order to support ImportAware @Configuration classes
        if (sbr != null && !sbr.containsSingleton(IMPORT_REGISTRY_BEAN_NAME)) {
            sbr.registerSingleton(IMPORT_REGISTRY_BEAN_NAME, parser.getImportRegistry());
        }

        if (this.metadataReaderFactory instanceof CachingMetadataReaderFactory) {
            // Clear cache in externally provided MetadataReaderFactory; this is a no-op
            // for a shared cache since it'll be cleared by the ApplicationContext.
            ((CachingMetadataReaderFactory) this.metadataReaderFactory).clearCache();
        }
    }

    /**
     * Post-processes a BeanFactory in search of Configuration class BeanDefinitions;
     * any candidates are then enhanced by a {@link ConfigurationClassEnhancer}.
     * Candidate status is determined by BeanDefinition attribute metadata.
     *
     * @see ConfigurationClassEnhancer
     */
    public void enhanceConfigurationClasses(ConfigurableListableBeanFactory beanFactory) {
        StartupStep enhanceConfigClasses = this.applicationStartup.start("spring.context.config-classes.enhance");
        Map<String, AbstractBeanDefinition> configBeanDefs = new LinkedHashMap<>();
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDef = beanFactory.getBeanDefinition(beanName);
            Object configClassAttr = beanDef.getAttribute(ConfigurationClassUtils.CONFIGURATION_CLASS_ATTRIBUTE);
            AnnotationMetadata annotationMetadata = null;
            MethodMetadata methodMetadata = null;
            if (beanDef instanceof AnnotatedBeanDefinition) {
                AnnotatedBeanDefinition annotatedBeanDefinition = (AnnotatedBeanDefinition) beanDef;
                annotationMetadata = annotatedBeanDefinition.getMetadata();
                methodMetadata = annotatedBeanDefinition.getFactoryMethodMetadata();
            }
            if ((configClassAttr != null || methodMetadata != null) && beanDef instanceof AbstractBeanDefinition) {
                // Configuration class (full or lite) or a configuration-derived @Bean method
                // -> eagerly resolve bean class at this point, unless it's a 'lite' configuration
                // or component class without @Bean methods.
                AbstractBeanDefinition abd = (AbstractBeanDefinition) beanDef;
                if (!abd.hasBeanClass()) {
                    boolean liteConfigurationCandidateWithoutBeanMethods =
                            (ConfigurationClassUtils.CONFIGURATION_CLASS_LITE.equals(configClassAttr) &&
                                    annotationMetadata != null && !ConfigurationClassUtils.hasBeanMethods(annotationMetadata));
                    if (!liteConfigurationCandidateWithoutBeanMethods) {
                        try {
                            abd.resolveBeanClass(this.beanClassLoader);
                        } catch (Throwable ex) {
                            throw new IllegalStateException(
                                    "Cannot load configuration class: " + beanDef.getBeanClassName(), ex);
                        }
                    }
                }
            }
            if (ConfigurationClassUtils.CONFIGURATION_CLASS_FULL.equals(configClassAttr)) {
                if (!(beanDef instanceof AbstractBeanDefinition)) {
                    throw new BeanDefinitionStoreException("Cannot enhance @Configuration bean definition '" +
                            beanName + "' since it is not stored in an AbstractBeanDefinition subclass");
                } else if (logger.isInfoEnabled() && beanFactory.containsSingleton(beanName)) {
                    logger.info("Cannot enhance @Configuration bean definition '" + beanName +
                            "' since its singleton instance has been created too early. The typical cause " +
                            "is a non-static @Bean method with a BeanDefinitionRegistryPostProcessor " +
                            "return type: Consider declaring such methods as 'static'.");
                }
                configBeanDefs.put(beanName, (AbstractBeanDefinition) beanDef);
            }
        }
        if (configBeanDefs.isEmpty() || NativeDetector.inNativeImage()) {
            // nothing to enhance -> return immediately
            enhanceConfigClasses.end();
            return;
        }

        ConfigurationClassEnhancer enhancer = new ConfigurationClassEnhancer();
        for (Map.Entry<String, AbstractBeanDefinition> entry : configBeanDefs.entrySet()) {
            AbstractBeanDefinition beanDef = entry.getValue();
            // If a @Configuration class gets proxied, always proxy the target class
            beanDef.setAttribute(AutoProxyUtils.PRESERVE_TARGET_CLASS_ATTRIBUTE, Boolean.TRUE);
            // Set enhanced subclass of the user-specified bean class
            Class<?> configClass = beanDef.getBeanClass();
            Class<?> enhancedClass = enhancer.enhance(configClass, this.beanClassLoader);
            if (configClass != enhancedClass) {
                if (logger.isTraceEnabled()) {
                    logger.trace(String.format("Replacing bean definition '%s' existing class '%s' with " +
                            "enhanced class '%s'", entry.getKey(), configClass.getName(), enhancedClass.getName()));
                }
                beanDef.setBeanClass(enhancedClass);
            }
        }
        enhanceConfigClasses.tag("classCount", () -> String.valueOf(configBeanDefs.keySet().size())).end();
    }

    /**
     * org.springframework.context.annotation.ConfigurationClassPostProcessor.ImportAwareBeanPostProcessor
     * 是 Spring 框架内部处理 @Configuration 类的一个关键组件。它是一个实现了 BeanPostProcessor 接口的类，
     * 主要用于处理 @Import 注解引入的 Configuration 类，并负责将它们注册到 Spring 容器中。下面详细解释其作用和工作流程。
     *
     * 主要作用
     * 处理 @Import 注解：
     * ImportAwareBeanPostProcessor 会处理 @Import 注解，该注解可以用来导入其他的 Configuration 类。
     * 通过这种方式，可以在不显式声明的情况下，将多个配置类引入到当前的配置类中。
     * 注册 Configuration 类：
     * 当处理带有 @Import 注解的类时，ImportAwareBeanPostProcessor 会识别出这些类，并将它们注册到 Spring 容器中。
     * 这意味着，即使某些配置类没有直接被声明为 Bean，它们也会被自动注册。
     * 处理条件注册：
     * ImportAwareBeanPostProcessor 还支持条件注册，即通过 @Conditional 注解来决定某些 Bean 是否应该被注册。这使得可以根据环境条件动态地注册 Bean。
     * 工作流程
     * 解析 @Import 注解：
     * 当 Spring 容器启动时，会扫描所有的配置类，并识别出带有 @Import 注解的类。ImportAwareBeanPostProcessor 会解析这些注解，
     * 并将指定的 Configuration 类添加到待处理的列表中。
     * 注册 Configuration 类：
     * ImportAwareBeanPostProcessor 会遍历所有的 Configuration 类，并通过 @Bean 方法注册它们声明的 Bean 到 Spring 容器中。
     * 同时也会处理嵌套的 @Import 注解，递归地导入其他配置类。
     * 处理条件注册：
     * 如果 Configuration 类中的 @Bean 方法使用了 @Conditional 注解，则 ImportAwareBeanPostProcessor 会根据条件决定是否注册这些 Bean。
     */
    private static class ImportAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

        private final BeanFactory beanFactory;

        public ImportAwareBeanPostProcessor(BeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }

        @Override
        public PropertyValues postProcessProperties(@Nullable PropertyValues pvs, Object bean, String beanName) {
            // Inject the BeanFactory before AutowiredAnnotationBeanPostProcessor's
            // postProcessProperties method attempts to autowire other configuration beans.
            if (bean instanceof EnhancedConfiguration) {
                ((EnhancedConfiguration) bean).setBeanFactory(this.beanFactory);
            }
            return pvs;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) {
            if (bean instanceof ImportAware) {
                ImportRegistry ir = this.beanFactory.getBean(IMPORT_REGISTRY_BEAN_NAME, ImportRegistry.class);
                AnnotationMetadata importingClass = ir.getImportingClassFor(ClassUtils.getUserClass(bean).getName());
                if (importingClass != null) {
                    ((ImportAware) bean).setImportMetadata(importingClass);
                }
            }
            return bean;
        }
    }

}
