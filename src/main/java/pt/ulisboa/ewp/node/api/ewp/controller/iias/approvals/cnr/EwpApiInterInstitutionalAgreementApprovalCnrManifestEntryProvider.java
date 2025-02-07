package pt.ulisboa.ewp.node.api.ewp.controller.iias.approvals.cnr;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.iias.approval.cnr.v1.IiaApprovalCnrV1;
import java.util.Collection;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.approval.cnr.InterInstitutionalAgreementApprovalCnrV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpManifestEntryProvider;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiInterInstitutionalAgreementApprovalCnrManifestEntryProvider
    extends EwpManifestEntryProvider {

  public EwpApiInterInstitutionalAgreementApprovalCnrManifestEntryProvider(
      HostPluginManager hostPluginManager) {
    super(hostPluginManager);

    super.registerHostProviderToManifestEntryConverter(
        InterInstitutionalAgreementApprovalCnrV1HostProvider.class,
        this::getManifestEntryForV1);
  }

  public ManifestApiEntryBaseV1 getManifestEntryForV1(String heiId, String baseUrl,
      Collection<InterInstitutionalAgreementApprovalCnrV1HostProvider> hostProviders) {
    IiaApprovalCnrV1 manifestEntry = new IiaApprovalCnrV1();
    manifestEntry.setVersion(hostProviders.iterator().next().getVersion());
    manifestEntry.setAdminNotes(null);
    manifestEntry.setUrl(
        baseUrl + EwpApiInterInstitutionalAgreementApprovalCnrV1Controller.BASE_PATH);
    manifestEntry.setHttpSecurity(getDefaultHttpSecurityOptions());
    return manifestEntry;
  }
}
