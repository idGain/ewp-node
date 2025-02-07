package pt.ulisboa.ewp.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import pt.ulisboa.ewp.node.config.bootstrap.BootstrapProperties;
import pt.ulisboa.ewp.node.config.registry.RegistryProperties;
import pt.ulisboa.ewp.node.config.security.SecurityProperties;
import pt.ulisboa.ewp.node.domain.utils.DatabaseProperties;
import pt.ulisboa.ewp.node.service.bootstrap.BootstrapService;
import pt.ulisboa.ewp.node.service.bootstrap.KeystoreBootstrapService;
import pt.ulisboa.ewp.node.service.ewp.mapping.sync.EwpMappingSyncService;
import pt.ulisboa.ewp.node.service.ewp.notification.EwpNotificationSenderDaemon;
import pt.ulisboa.ewp.node.service.http.log.ewp.EwpHttpCommunicationLogService;
import pt.ulisboa.ewp.node.utils.bean.ParamNameProcessor;
import pt.ulisboa.ewp.node.utils.http.converter.xml.EwpNamespacePrefixMapper;
import pt.ulisboa.ewp.node.utils.http.converter.xml.Jaxb2HttpMessageConverter;

@SpringBootApplication(scanBasePackages = {"pt.ulisboa.ewp.node"})
@EnableConfigurationProperties(
    value = {
        DatabaseProperties.class,
        BootstrapProperties.class,
        RegistryProperties.class,
        SecurityProperties.class
    })
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
@EnableCaching(proxyTargetClass = true)
@Import(ValidationAutoConfiguration.class)
public class EwpNodeApplication {

  @Autowired
  private BootstrapService bootstrapService;
  @Autowired
  private KeystoreBootstrapService keystoreBootstrapService;
  @Autowired
  private ThreadPoolTaskScheduler taskScheduler;
  @Autowired
  private EwpNotificationSenderDaemon ewpNotificationSenderDaemon;

  @Autowired
  private Collection<EwpMappingSyncService> mappingSyncServices;

  @Autowired
  private EwpHttpCommunicationLogService ewpHttpCommunicationLogService;

  public static void main(String[] args) {
    SpringApplication.run(EwpNodeApplication.class);
  }

  @PostConstruct
  private void init() {
    bootstrapService.bootstrap();
    keystoreBootstrapService.bootstrap();
    this.initSchedules();
  }

  private void initSchedules() {
    taskScheduler.schedule(ewpNotificationSenderDaemon,
        new PeriodicTrigger(EwpNotificationSenderDaemon.TASK_INTERVAL_IN_MILLISECONDS,
            TimeUnit.MILLISECONDS));

    for (EwpMappingSyncService mappingSyncService : mappingSyncServices) {
      taskScheduler.schedule(mappingSyncService,
          new PeriodicTrigger(mappingSyncService.getTaskIntervalInMilliseconds(),
              TimeUnit.MILLISECONDS));
    }
  }

  /**
   * Returns a message source for internationalization (i18n).
   */
  @Bean
  public MessageSource messageSource() {
    ReloadableResourceBundleMessageSource messageSource =
        new ReloadableResourceBundleMessageSource();

    messageSource.setBasename("classpath:messages");
    messageSource.setDefaultEncoding("UTF-8");
    return messageSource;
  }

  @Bean
  public Marshaller marshaller() throws JAXBException {
    return jaxb2Marshaller().getJaxbContext().createMarshaller();
  }

  /**
   * Inject a (un)marshalling HTTP message converter that is aware of the different packages to scan
   * when marshalling/unmarshalling.
   */
  @Bean
  public Jaxb2HttpMessageConverter marshallingHttpMessageConverter() {
    Jaxb2HttpMessageConverter result = new Jaxb2HttpMessageConverter();
    result.setPackagesToScan("eu.erasmuswithoutpaper.api", "pt.ulisboa.ewp.node");
    result.setSupportJaxbElementClass(true);
    result.setNamespacePrefixMapper(new EwpNamespacePrefixMapper());
    return result;
  }

  @Bean
  public Jaxb2Marshaller jaxb2Marshaller() {
    Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    marshaller.setPackagesToScan("eu.erasmuswithoutpaper.api", "pt.ulisboa.ewp.node");
    marshaller.setSupportJaxbElementClass(true);

    Map<String, Object> jaxbProperties = new HashMap<>();
    jaxbProperties.put(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    marshaller.setMarshallerProperties(jaxbProperties);
    return marshaller;
  }

  /**
   * Custom {@link BeanPostProcessor} for adding {@link ParamNameProcessor} into the first of {@link
   * RequestMappingHandlerAdapter#argumentResolvers}.
   *
   * @return BeanPostProcessor
   */
  @Bean
  public BeanPostProcessor beanPostProcessor() {
    return new BeanPostProcessor() {

      @Override
      public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
      }

      @Override
      public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof RequestMappingHandlerAdapter) {
          RequestMappingHandlerAdapter adapter = (RequestMappingHandlerAdapter) bean;
          List<HandlerMethodArgumentResolver> argumentResolvers =
              new ArrayList<>(adapter.getArgumentResolvers());
          argumentResolvers.add(0, paramNameProcessor());
          adapter.setArgumentResolvers(argumentResolvers);
        }
        return bean;
      }
    };
  }

  /**
   * A processor for {@link pt.ulisboa.ewp.node.utils.bean.ParamName} annotations. Reference:
   * https://stackoverflow.com/questions/8986593/how-to-customize-parameter-names-when-binding-spring-mvc-command-objects
   */
  @Bean
  public ParamNameProcessor paramNameProcessor() {
    return new ParamNameProcessor();
  }

  @Bean
  public CommonsRequestLoggingFilter requestLoggingFilter() {
    CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
    loggingFilter.setIncludeClientInfo(false);
    loggingFilter.setIncludeQueryString(true);
    loggingFilter.setIncludeHeaders(false);
    loggingFilter.setIncludePayload(false);
    return loggingFilter;
  }

  @Bean
  public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
    ThreadPoolTaskScheduler threadPoolTaskScheduler
        = new ThreadPoolTaskScheduler();
    threadPoolTaskScheduler.setPoolSize(5);
    threadPoolTaskScheduler.setThreadNamePrefix(
        "ThreadPoolTaskScheduler");
    return threadPoolTaskScheduler;
  }
}
