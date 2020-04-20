package pt.ulisboa.ewp.node.utils.ewp;

import static org.joox.JOOX.$;

import eu.erasmuswithoutpaper.api.client.auth.methods.cliauth.httpsig.CliauthHttpsig;
import eu.erasmuswithoutpaper.api.client.auth.methods.cliauth.none.CliauthAnonymous;
import eu.erasmuswithoutpaper.api.client.auth.methods.cliauth.tlscert.CliauthTlscert;
import eu.erasmuswithoutpaper.api.client.auth.methods.srvauth.httpsig.SrvauthHttpsig;
import eu.erasmuswithoutpaper.api.client.auth.methods.srvauth.tlscert.SrvauthTlscert;
import eu.erasmuswithoutpaper.api.courses.Courses;
import eu.erasmuswithoutpaper.api.courses.replication.SimpleCourseReplication;
import eu.erasmuswithoutpaper.api.institutions.Institutions;
import eu.erasmuswithoutpaper.api.ounits.OrganizationalUnits;
import eu.erasmuswithoutpaper.api.specs.sec.intro.HttpSecurityOptions;
import eu.erasmuswithoutpaper.registryclient.ApiSearchConditions;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.w3c.dom.Element;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.client.ewp.utils.EwpClientConstants;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpCourseApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpInstitutionApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpOrganizationalUnitApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpSimpleCourseReplicationApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationAnonymousConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationHttpSignatureConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationTlsCertificateConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationHttpSignatureConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationTlsCertificateConfiguration;

public class EwpApiUtils {

  private EwpApiUtils() {}

  public static Optional<EwpInstitutionApiConfiguration> getInstitutionApiConfiguration(
      RegistryClient registryClient, String heiId) {
    Optional<Institutions> apiElementOptional =
        getApiElement(
            registryClient,
            heiId,
            EwpClientConstants.API_INSTITUTIONS_LOCAL_NAME,
            Institutions.class);
    if (!apiElementOptional.isPresent()) {
      return Optional.empty();
    }
    Institutions apiElement = apiElementOptional.get();

    return Optional.of(
        new EwpInstitutionApiConfiguration(
            apiElement.getUrl(),
            getSupportedClientAuthenticationMethods(apiElement.getHttpSecurity()),
            getSupportedServerAuthenticationMethods(apiElement.getHttpSecurity()),
            apiElement.getMaxHeiIds()));
  }

  public static Optional<EwpOrganizationalUnitApiConfiguration>
      getOrganizationalUnitApiConfiguration(RegistryClient registryClient, String heiId) {
    Optional<OrganizationalUnits> apiElementOptional =
        getApiElement(
            registryClient,
            heiId,
            EwpClientConstants.API_ORGANIZATIONAL_UNITS_NAME,
            OrganizationalUnits.class);
    if (!apiElementOptional.isPresent()) {
      return Optional.empty();
    }
    OrganizationalUnits apiElement = apiElementOptional.get();

    return Optional.of(
        new EwpOrganizationalUnitApiConfiguration(
            apiElement.getUrl(),
            getSupportedClientAuthenticationMethods(apiElement.getHttpSecurity()),
            getSupportedServerAuthenticationMethods(apiElement.getHttpSecurity()),
            apiElement.getMaxOunitIds(),
            apiElement.getMaxOunitCodes()));
  }

  public static Optional<EwpCourseApiConfiguration> getCourseApiConfiguration(
      RegistryClient registryClient, String heiId) {
    Optional<Courses> apiElementOptional =
        getApiElement(registryClient, heiId, EwpClientConstants.API_COURSES_NAME, Courses.class);
    if (!apiElementOptional.isPresent()) {
      return Optional.empty();
    }
    Courses apiElement = apiElementOptional.get();

    return Optional.of(
        new EwpCourseApiConfiguration(
            apiElement.getUrl(),
            getSupportedClientAuthenticationMethods(apiElement.getHttpSecurity()),
            getSupportedServerAuthenticationMethods(apiElement.getHttpSecurity()),
            apiElement.getMaxLosIds(),
            apiElement.getMaxLosCodes()));
  }

  public static Optional<EwpSimpleCourseReplicationApiConfiguration>
      getSimpleCourseReplicationApiConfiguration(RegistryClient registryClient, String heiId) {
    Optional<SimpleCourseReplication> apiElementOptional =
        getApiElement(
            registryClient,
            heiId,
            EwpClientConstants.API_SIMPLE_COURSE_REPLICATION_NAME,
            SimpleCourseReplication.class);
    if (!apiElementOptional.isPresent()) {
      return Optional.empty();
    }
    SimpleCourseReplication apiElement = apiElementOptional.get();

    return Optional.of(
        new EwpSimpleCourseReplicationApiConfiguration(
            apiElement.getUrl(),
            getSupportedClientAuthenticationMethods(apiElement.getHttpSecurity()),
            getSupportedServerAuthenticationMethods(apiElement.getHttpSecurity()),
            apiElement.isSupportsModifiedSince()));
  }

  private static <T> Optional<T> getApiElement(
      RegistryClient registryClient, String heiId, String apiLocalName, Class<T> elementClassType) {
    ApiSearchConditions apiSearchConditions =
        new ApiSearchConditions().setRequiredHei(heiId).setApiClassRequired(null, apiLocalName);
    Element apiElement = registryClient.findApi(apiSearchConditions);
    if (apiElement == null) {
      return Optional.empty();
    }
    return Optional.of(
        $(registryClient.findApi(apiSearchConditions)).unmarshalOne(elementClassType));
  }

  public static Collection<EwpClientAuthenticationConfiguration>
      getSupportedClientAuthenticationMethods(HttpSecurityOptions httpSecurityOptions) {
    Collection<EwpClientAuthenticationConfiguration> result = new HashSet<>();
    if (httpSecurityOptions != null) {
      List<Object> clientAuthMethods = httpSecurityOptions.getClientAuthMethods().getAny();
      for (Object object : clientAuthMethods) {
        if (object instanceof CliauthHttpsig) {
          result.add(new EwpClientAuthenticationHttpSignatureConfiguration());
        } else if (object instanceof CliauthAnonymous) {
          result.add(new EwpClientAuthenticationAnonymousConfiguration());
        } else if (object instanceof CliauthTlscert) {
          CliauthTlscert clientAuthTlsCert = (CliauthTlscert) object;
          result.add(
              new EwpClientAuthenticationTlsCertificateConfiguration(
                  clientAuthTlsCert.isAllowsSelfSigned()));
        } else {
          throw new IllegalArgumentException(
              "Unknown client authentication method: " + object.getClass().getCanonicalName());
        }
      }
    } else {
      // Default authentication methods according to EWP documentation
      result.add(new EwpClientAuthenticationTlsCertificateConfiguration(true));
    }
    return result;
  }

  public static Collection<EwpServerAuthenticationConfiguration>
      getSupportedServerAuthenticationMethods(HttpSecurityOptions httpSecurityOptions) {
    Collection<EwpServerAuthenticationConfiguration> result = new HashSet<>();
    if (httpSecurityOptions != null) {
      List<Object> serverAuthMethods = httpSecurityOptions.getServerAuthMethods().getAny();
      for (Object object : serverAuthMethods) {
        if (object instanceof SrvauthHttpsig) {
          result.add(new EwpServerAuthenticationHttpSignatureConfiguration());
        } else if (object instanceof SrvauthTlscert) {
          result.add(new EwpServerAuthenticationTlsCertificateConfiguration());
        } else {
          throw new IllegalArgumentException(
              "Unknown server authentication method: " + object.getClass().getCanonicalName());
        }
      }
    } else {
      // Default authentication methods according to EWP documentation
      result.add(new EwpServerAuthenticationTlsCertificateConfiguration());
    }
    return result;
  }

  /**
   * Returns the "best" supported API authentication method using a predefined list of
   * authentication methods order.
   *
   * @return
   */
  public static EwpAuthenticationMethod getBestSupportedApiAuthenticationMethod(
      EwpApiConfiguration api) {
    for (EwpAuthenticationMethod authenticationMethod :
        EwpClientConstants.AUTHENTICATION_METHODS_BY_PREFERENTIAL_ORDER) {
      if (EwpApiUtils.doesApiSupportAuthenticationMethod(
          api.getSupportedClientAuthenticationMethods(),
          api.getSupportedServerAuthenticationMethods(),
          authenticationMethod)) {
        return authenticationMethod;
      }
    }

    throw new IllegalStateException(
        "Failed to find an admissible authentication method for API: " + api.getUrl());
  }

  public static boolean doesApiSupportAuthenticationMethod(
      Collection<EwpClientAuthenticationConfiguration> clientAuthenticationConfigurations,
      Collection<EwpServerAuthenticationConfiguration> serverAuthenticationConfigurations,
      EwpAuthenticationMethod authenticationMethod) {
    return doesClientSupportAuthenticationMethod(
            clientAuthenticationConfigurations, authenticationMethod)
        && doesServerSupportAuthenticationMethod(
            serverAuthenticationConfigurations, authenticationMethod);
  }

  public static boolean doesServerSupportAuthenticationMethod(
      Collection<EwpServerAuthenticationConfiguration> serverAuthenticationConfigurations,
      EwpAuthenticationMethod authenticationMethod) {
    return serverAuthenticationConfigurations.stream()
        .anyMatch(
            c -> {
              switch (authenticationMethod) {
                case HTTP_SIGNATURE:
                  return c.isHttpSignature();

                case TLS:
                  return c.isTlsCertificate();

                default:
                  return false;
              }
            });
  }

  public static boolean doesClientSupportAuthenticationMethod(
      Collection<EwpClientAuthenticationConfiguration> clientAuthenticationConfigurations,
      EwpAuthenticationMethod authenticationMethod) {
    return clientAuthenticationConfigurations.stream()
        .anyMatch(
            c -> {
              switch (authenticationMethod) {
                case HTTP_SIGNATURE:
                  return c.isHttpSignature();

                case TLS:
                  return c.isTlsCertificate();

                case ANONYMOUS:
                  return c.isAnonymous();

                default:
                  return false;
              }
            });
  }
}
