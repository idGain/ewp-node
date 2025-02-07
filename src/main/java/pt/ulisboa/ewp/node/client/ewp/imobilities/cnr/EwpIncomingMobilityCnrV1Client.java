package pt.ulisboa.ewp.node.client.ewp.imobilities.cnr;

import eu.erasmuswithoutpaper.api.imobilities.cnr.v1.ImobilityCnrResponseV1;
import java.util.List;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.EwpApiClient;
import pt.ulisboa.ewp.node.client.ewp.EwpClient;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestFormDataBody;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpIncomingMobilityCnrApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.EwpApiVersionSpecification;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.IncomingMobilityCnr;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpIncomingMobilityCnrV1Client
    extends EwpApiClient<EwpIncomingMobilityCnrApiConfiguration> {

  public EwpIncomingMobilityCnrV1Client(RegistryClient registryClient,
      EwpClient ewpClient) {
    super(registryClient, ewpClient);
  }

  public EwpSuccessOperationResult<ImobilityCnrResponseV1> sendChangeNotification(
      String sendingHeiId, String receivingHeiId, List<String> outgoingMobilityIds)
      throws EwpClientErrorException {
    EwpIncomingMobilityCnrApiConfiguration api = getApiConfigurationForHeiId(
        sendingHeiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.RECEIVING_HEI_ID, receivingHeiId);
    bodyParams.param(EwpApiParamConstants.OMOBILITY_ID, outgoingMobilityIds);

    EwpRequest request = EwpRequest.createPost(api, api.getUrl(),
        new EwpRequestFormDataBody(bodyParams));
    return ewpClient.executeAndLog(request, ImobilityCnrResponseV1.class);
  }

  @Override
  public EwpApiVersionSpecification<?, EwpIncomingMobilityCnrApiConfiguration>
  getApiVersionSpecification() {
    return IncomingMobilityCnr.V1;
  }
}
