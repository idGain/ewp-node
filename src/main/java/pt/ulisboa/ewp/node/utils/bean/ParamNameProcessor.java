package pt.ulisboa.ewp.node.utils.bean;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

/** Method processor that supports {@link ParamName} parameters renaming */
public class ParamNameProcessor extends ServletModelAttributeMethodProcessor {

  @Autowired private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

  private static final Map<Class<?>, Map<String, String>> PARAM_MAPPINGS_CACHE =
      new ConcurrentHashMap<>(256);

  public ParamNameProcessor() {
    super(false);
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return !BeanUtils.isSimpleProperty(parameter.getParameterType())
        && Arrays.stream(parameter.getParameterType().getDeclaredFields())
            .anyMatch(field -> field.getAnnotation(ParamName.class) != null);
  }

  @Override
  protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest nativeWebRequest) {
    Object target = binder.getTarget();
    if (target != null) {
      Map<String, String> paramMappings = this.getParamMappings(target.getClass());
      ParamNameDataBinder paramNameDataBinder =
          new ParamNameDataBinder(target, binder.getObjectName(), paramMappings);
      Objects.requireNonNull(requestMappingHandlerAdapter.getWebBindingInitializer())
          .initBinder(paramNameDataBinder);
      super.bindRequestParameters(paramNameDataBinder, nativeWebRequest);
    } else {
      throw new IllegalStateException("Missing binder's target");
    }
  }

  /**
   * Get param mappings. Cache param mappings in memory.
   *
   * @return {@link Map}
   */
  private Map<String, String> getParamMappings(Class<?> targetClass) {
    if (PARAM_MAPPINGS_CACHE.containsKey(targetClass)) {
      return PARAM_MAPPINGS_CACHE.get(targetClass);
    }
    Field[] fields = targetClass.getDeclaredFields();
    Map<String, String> paramMappings = new HashMap<>(32);
    for (Field field : fields) {
      ParamName paramName = field.getAnnotation(ParamName.class);
      if (paramName != null && !paramName.value().isEmpty()) {
        paramMappings.put(paramName.value(), field.getName());
      }
    }
    PARAM_MAPPINGS_CACHE.put(targetClass, paramMappings);
    return paramMappings;
  }
}
