package pt.ulisboa.ewp.node.api.host.forward.ewp.controller.iias;

import eu.erasmuswithoutpaper.api.iias.v3.endpoints.IiasGetResponseV3;
import eu.erasmuswithoutpaper.api.iias.v3.endpoints.IiasIndexResponseV3;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.AbstractForwardEwpApiController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.ForwardEwpApi;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.ForwardEwpApiInterInstitutionalAgreementsApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.InterInstitutionalAgreementsGetRequestDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.InterInstitutionalAgreementsIndexRequestDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.iias.EwpInterInstitutionalAgreementsV3Client;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;

@RestController
@ForwardEwpApi(apiLocalName = EwpApiConstants.API_INTERINSTITUTIONAL_AGREEMENTS_NAME)
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "iias/v3")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_WITH_PREFIX})
public class ForwardEwpApiInterInstitutionalAgreementsV3Controller
    extends AbstractForwardEwpApiController {

  private final EwpInterInstitutionalAgreementsV3Client client;

  public ForwardEwpApiInterInstitutionalAgreementsV3Controller(
      RegistryClient registryClient, EwpInterInstitutionalAgreementsV3Client client) {
    super(registryClient);
    this.client = client;
  }

  @GetMapping(value = "/specification", produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<
          ForwardEwpApiResponseWithData<
              ForwardEwpApiInterInstitutionalAgreementsApiSpecificationResponseDTO>>
      getApiSpecification(@NotEmpty @RequestParam(value = "hei_id") String heiId) {
    return ResponseEntity.ok(
        ForwardEwpApiResponseUtils.createResponseWithMessagesAndData(
            client.getApiSpecification(heiId)));
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE,
      value = "/index")
  public ResponseEntity<ForwardEwpApiResponseWithData<IiasIndexResponseV3>> findAllByHeiId(
      @Valid InterInstitutionalAgreementsIndexRequestDto requestDto)
      throws EwpClientErrorException {
    EwpSuccessOperationResult<IiasIndexResponseV3> response =
        client.findAllByHeiIds(
            requestDto.getHeiId(),
            requestDto.getPartnerHeiId(),
            requestDto.getReceivingAcademicYearIds(),
            requestDto.getModifiedSince());
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(response);
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE,
      value = "/get")
  public ResponseEntity<ForwardEwpApiResponseWithData<IiasGetResponseV3>> findByHeiIdAndIiaIds(
      @Valid InterInstitutionalAgreementsGetRequestDto requestDto)
      throws EwpClientErrorException {
    EwpSuccessOperationResult<IiasGetResponseV3> response;
    List<String> iiaIds = requestDto.getIiaIds();
    List<String> iiaCodes = requestDto.getIiaCodes();
    if (!iiaIds.isEmpty()) {
      response =
          client.findByHeiIdAndIiaIds(requestDto.getHeiId(), iiaIds, requestDto.getSendPdf());
    } else {
      response =
          client.findByHeiIdAndIiaCodes(requestDto.getHeiId(), iiaCodes, requestDto.getSendPdf());
    }
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(response);
  }
}
