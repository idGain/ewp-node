package pt.ulisboa.ewp.node.domain.entity.api.ewp;

import eu.erasmuswithoutpaper.api.iias.v3.IiasV3;
import eu.erasmuswithoutpaper.api.iias.v4.IiasV4;
import eu.erasmuswithoutpaper.api.iias.v6.IiasV6;
import java.math.BigInteger;
import java.util.Collection;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationConfiguration;

public class EwpInterInstitutionalAgreementApiConfiguration extends EwpApiConfiguration {

  public static final String API_NAME = "InterInstitutional Agreements API";

  private String indexUrl;
  private String getUrl;
  private BigInteger maxIiaIds;
  private BigInteger maxIiaCodes;
  private boolean sendsNotifications;

  public EwpInterInstitutionalAgreementApiConfiguration(
      String indexUrl,
      String getUrl,
      Collection<EwpClientAuthenticationConfiguration> supportedClientAuthenticationMethods,
      Collection<EwpServerAuthenticationConfiguration> supportedServerAuthenticationMethods,
      BigInteger maxIiaIds,
      BigInteger maxIiaCodes,
      boolean sendsNotifications) {
    super(supportedClientAuthenticationMethods, supportedServerAuthenticationMethods);
    this.indexUrl = indexUrl;
    this.getUrl = getUrl;
    this.maxIiaIds = maxIiaIds;
    this.maxIiaCodes = maxIiaCodes;
    this.sendsNotifications = sendsNotifications;
  }

  public String getIndexUrl() {
    return indexUrl;
  }

  public void setIndexUrl(String indexUrl) {
    this.indexUrl = indexUrl;
  }

  public String getGetUrl() {
    return getUrl;
  }

  public void setGetUrl(String getUrl) {
    this.getUrl = getUrl;
  }

  public BigInteger getMaxIiaIds() {
    return maxIiaIds;
  }

  public void setMaxIiaIds(BigInteger maxIiaIds) {
    this.maxIiaIds = maxIiaIds;
  }

  public BigInteger getMaxIiaCodes() {
    return maxIiaCodes;
  }

  public void setMaxIiaCodes(BigInteger maxIiaCodes) {
    this.maxIiaCodes = maxIiaCodes;
  }

  public boolean isSendsNotifications() {
    return sendsNotifications;
  }

  public void setSendsNotifications(boolean sendsNotifications) {
    this.sendsNotifications = sendsNotifications;
  }

  public static EwpInterInstitutionalAgreementApiConfiguration create(IiasV3 apiElement) {
    return new EwpInterInstitutionalAgreementApiConfiguration(
        apiElement.getIndexUrl(),
        apiElement.getGetUrl(),
        EwpApiUtils.getSupportedClientAuthenticationMethods(apiElement.getHttpSecurity()),
        EwpApiUtils.getSupportedServerAuthenticationMethods(apiElement.getHttpSecurity()),
        apiElement.getMaxIiaIds(),
        apiElement.getMaxIiaCodes(),
        apiElement.getSendsNotifications() != null);
  }

  public static EwpInterInstitutionalAgreementApiConfiguration create(IiasV4 apiElement) {
    return new EwpInterInstitutionalAgreementApiConfiguration(
        apiElement.getIndexUrl(),
        apiElement.getGetUrl(),
        EwpApiUtils.getSupportedClientAuthenticationMethods(apiElement.getHttpSecurity()),
        EwpApiUtils.getSupportedServerAuthenticationMethods(apiElement.getHttpSecurity()),
        apiElement.getMaxIiaIds(),
        apiElement.getMaxIiaCodes(),
        apiElement.getSendsNotifications() != null);
  }

  public static EwpInterInstitutionalAgreementApiConfiguration create(IiasV6 apiElement) {
    return new EwpInterInstitutionalAgreementApiConfiguration(
        apiElement.getIndexUrl(),
        apiElement.getGetUrl(),
        EwpApiUtils.getSupportedClientAuthenticationMethods(apiElement.getHttpSecurity()),
        EwpApiUtils.getSupportedServerAuthenticationMethods(apiElement.getHttpSecurity()),
        apiElement.getMaxIiaIds(),
        apiElement.getMaxIiaCodes(),
        true);
  }

  @Override
  public String toString() {
    return "EwpInterinstitutionalAgreementApiConfiguration{"
        + "indexUrl='"
        + indexUrl
        + '\''
        + ", getUrl='"
        + getUrl
        + '\''
        + ", maxIiaIds="
        + maxIiaIds
        + ", maxIiaCodes="
        + maxIiaCodes
        + ", sendsNotifications="
        + sendsNotifications
        + '}';
  }
}
