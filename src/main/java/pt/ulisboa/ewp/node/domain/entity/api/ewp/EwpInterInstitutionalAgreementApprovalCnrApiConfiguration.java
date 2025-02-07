package pt.ulisboa.ewp.node.domain.entity.api.ewp;

import eu.erasmuswithoutpaper.api.iias.approval.cnr.v1.IiaApprovalCnrV1;
import java.util.Collection;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationConfiguration;

public class EwpInterInstitutionalAgreementApprovalCnrApiConfiguration extends EwpApiConfiguration {

  public static final String API_NAME = "InterInstitutional Agreement Approval CNR API";

  private final String url;

  public EwpInterInstitutionalAgreementApprovalCnrApiConfiguration(
      String url,
      Collection<EwpClientAuthenticationConfiguration> supportedClientAuthenticationMethods,
      Collection<EwpServerAuthenticationConfiguration> supportedServerAuthenticationMethods) {
    super(supportedClientAuthenticationMethods, supportedServerAuthenticationMethods);
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  public static EwpInterInstitutionalAgreementApprovalCnrApiConfiguration create(
      IiaApprovalCnrV1 apiElement) {
    return new EwpInterInstitutionalAgreementApprovalCnrApiConfiguration(
        apiElement.getUrl(),
        EwpApiUtils.getSupportedClientAuthenticationMethods(apiElement.getHttpSecurity()),
        EwpApiUtils.getSupportedServerAuthenticationMethods(apiElement.getHttpSecurity()));
  }

  @Override
  public String toString() {
    return "EwpInterInstitutionalAgreementApprovalCnrApiConfiguration{" +
        "url='" + url + '\'' +
        '}';
  }
}
