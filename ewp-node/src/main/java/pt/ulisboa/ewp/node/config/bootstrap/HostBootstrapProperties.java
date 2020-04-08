package pt.ulisboa.ewp.node.config.bootstrap;

import java.util.List;

public class HostBootstrapProperties {

  private String code;
  private String description;
  private String adminEmail;
  private String adminNotes;
  private HostForwardEwpApiBootstrapProperties forwardEwpApi;
  private HostNotificationApiBootstrapProperties notificationApi;
  private List<HostCoveredHeiBootstrapProperties> coveredHeis;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getAdminEmail() {
    return adminEmail;
  }

  public void setAdminEmail(String adminEmail) {
    this.adminEmail = adminEmail;
  }

  public String getAdminNotes() {
    return adminNotes;
  }

  public void setAdminNotes(String adminNotes) {
    this.adminNotes = adminNotes;
  }

  public HostForwardEwpApiBootstrapProperties getForwardEwpApi() {
    return forwardEwpApi;
  }

  public void setForwardEwpApi(HostForwardEwpApiBootstrapProperties forwardEwpApi) {
    this.forwardEwpApi = forwardEwpApi;
  }

  public HostNotificationApiBootstrapProperties getNotificationApi() {
    return notificationApi;
  }

  public void setNotificationApi(HostNotificationApiBootstrapProperties notificationApi) {
    this.notificationApi = notificationApi;
  }

  public List<HostCoveredHeiBootstrapProperties> getCoveredHeis() {
    return coveredHeis;
  }

  public void setCoveredHeis(List<HostCoveredHeiBootstrapProperties> coveredHeis) {
    this.coveredHeis = coveredHeis;
  }
}
