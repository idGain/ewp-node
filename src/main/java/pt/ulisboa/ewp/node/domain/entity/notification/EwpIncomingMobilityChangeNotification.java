package pt.ulisboa.ewp.node.domain.entity.notification;

import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("IMOBILITY")
public class EwpIncomingMobilityChangeNotification extends EwpChangeNotification {

  private String sendingHeiId;
  private String receivingHeiId;
  private String outgoingMobilityId;

  public EwpIncomingMobilityChangeNotification() {
  }

  public EwpIncomingMobilityChangeNotification(String sendingHeiId,
      String receivingHeiId, String outgoingMobilityId) {
    super();
    this.sendingHeiId = sendingHeiId;
    this.receivingHeiId = receivingHeiId;
    this.outgoingMobilityId = outgoingMobilityId;
  }

  public EwpIncomingMobilityChangeNotification(int attemptNumber,
      ZonedDateTime scheduledDateTime,
      Status status, String sendingHeiId,
      String receivingHeiId, String outgoingMobilityId) {
    super(attemptNumber, scheduledDateTime, status);
    this.sendingHeiId = sendingHeiId;
    this.receivingHeiId = receivingHeiId;
    this.outgoingMobilityId = outgoingMobilityId;
  }

  @Column(name = "sending_hei_id")
  public String getSendingHeiId() {
    return sendingHeiId;
  }

  public void setSendingHeiId(String sendingHeiId) {
    this.sendingHeiId = sendingHeiId;
  }

  @Column(name = "receiving_hei_id")
  public String getReceivingHeiId() {
    return receivingHeiId;
  }

  public void setReceivingHeiId(String receivingHeiId) {
    this.receivingHeiId = receivingHeiId;
  }

  @Column(name = "outgoing_mobility_id")
  public String getOutgoingMobilityId() {
    return outgoingMobilityId;
  }

  public void setOutgoingMobilityId(String outgoingMobilityId) {
    this.outgoingMobilityId = outgoingMobilityId;
  }

  @Override
  public boolean canBeMergedInto(EwpChangeNotification o) {
    if (!(o instanceof EwpIncomingMobilityChangeNotification)) {
      return false;
    }

    EwpIncomingMobilityChangeNotification otherChangeNotification = (EwpIncomingMobilityChangeNotification) o;
    return sendingHeiId.equals(otherChangeNotification.sendingHeiId) &&
        receivingHeiId.equals(otherChangeNotification.receivingHeiId) && outgoingMobilityId.equals(
        otherChangeNotification.outgoingMobilityId);
  }

  @Override
  public String toString() {
    return "EwpIncomingMobilityChangeNotification{" +
        "super='" + super.toString() + '\'' +
        ", sendingHeiId='" + sendingHeiId + '\'' +
        ", receivingHeiId='" + receivingHeiId + '\'' +
        ", outgoingMobilityId='" + outgoingMobilityId + '\'' +
        '}';
  }
}
