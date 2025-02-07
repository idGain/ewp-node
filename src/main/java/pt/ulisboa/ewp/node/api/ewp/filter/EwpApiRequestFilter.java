package pt.ulisboa.ewp.node.api.ewp.filter;

import java.io.IOException;
import java.time.ZonedDateTime;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.wrapper.EwpApiHttpRequestWrapper;
import pt.ulisboa.ewp.node.service.http.log.ewp.EwpHttpCommunicationLogService;

/**
 * Filter that wraps EWP API requests around an EwpHttpRequestWrapper, necessary for other filters
 * related to EWP. Also, it passes, further to the filter chain, a response object that caches
 * content.
 */
@Configuration
@Order(Integer.MIN_VALUE)
public class EwpApiRequestFilter extends OncePerRequestFilter {

  private final EwpHttpCommunicationLogService ewpCommunicationLogService;

  public EwpApiRequestFilter(EwpHttpCommunicationLogService ewpCommunicationLogService) {
    this.ewpCommunicationLogService = ewpCommunicationLogService;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return !request.getRequestURI().startsWith(EwpApiConstants.API_BASE_URI)
        && !request.getRequestURI().startsWith(EwpApiConstants.REST_BASE_URI);
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    ZonedDateTime startProcessingDateTime = ZonedDateTime.now();
    EwpApiHttpRequestWrapper ewpRequest = new EwpApiHttpRequestWrapper(request);
    ContentCachingResponseWrapper contentCachingResponseWrapper =
        new ContentCachingResponseWrapper(response);
    filterChain.doFilter(ewpRequest, contentCachingResponseWrapper);

    ZonedDateTime endProcessingDateTime = ZonedDateTime.now();
    ewpCommunicationLogService.logCommunicationFromEwpNode(
        ewpRequest,
        contentCachingResponseWrapper,
        startProcessingDateTime,
        endProcessingDateTime,
        "");

    contentCachingResponseWrapper.copyBodyToResponse();
  }
}
