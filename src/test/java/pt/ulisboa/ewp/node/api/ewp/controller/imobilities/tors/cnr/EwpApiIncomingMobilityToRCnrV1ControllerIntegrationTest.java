package pt.ulisboa.ewp.node.api.ewp.controller.imobilities.tors.cnr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.erasmuswithoutpaper.api.imobilities.tors.cnr.v1.ImobilityTorCnrResponseV1;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.imobilities.tors.cnr.IncomingMobilityToRCnrV1HostProvider;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.imobilities.tors.cnr.MockIncomingMobilityToRCnrV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.AbstractEwpControllerIntegrationTest;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.utils.XmlUtils;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

class EwpApiIncomingMobilityToRCnrV1ControllerIntegrationTest extends
    AbstractEwpControllerIntegrationTest {

  @Autowired
  private HostPluginManager hostPluginManager;

  @Autowired
  private RegistryClient registryClient;

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  void testIncomingMobilityToRCnr_TwoAdmissibleHostProviders_BothHostProvidersInvoked(
      HttpMethod method) throws Exception {
    String receivingHeiId = "test";
    String omobilityId = "om1";

    MockIncomingMobilityToRCnrV1HostProvider mockProvider1 = Mockito.spy(
        new MockIncomingMobilityToRCnrV1HostProvider());
    MockIncomingMobilityToRCnrV1HostProvider mockProvider2 = Mockito.spy(
        new MockIncomingMobilityToRCnrV1HostProvider());

    doReturn(Arrays.asList(mockProvider1, mockProvider2)).when(hostPluginManager)
        .getAllProvidersOfType(IncomingMobilityToRCnrV1HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.RECEIVING_HEI_ID, receivingHeiId);
    queryParams.param(EwpApiParamConstants.OMOBILITY_ID, omobilityId);

    String responseXml =
        executeRequest(registryClient, method,
            EwpApiConstants.API_BASE_URI
                + EwpApiIncomingMobilityToRCnrV1Controller.BASE_PATH,
            queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    ImobilityTorCnrResponseV1 response = XmlUtils.unmarshall(responseXml,
        ImobilityTorCnrResponseV1.class);

    assertThat(response).isNotNull();

    verify(mockProvider1, times(1)).onChangeNotification(receivingHeiId, List.of(omobilityId));
    verify(mockProvider2, times(1)).onChangeNotification(receivingHeiId, List.of(omobilityId));
  }

}