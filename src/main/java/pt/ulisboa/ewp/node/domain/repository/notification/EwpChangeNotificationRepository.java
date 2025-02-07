package pt.ulisboa.ewp.node.domain.repository.notification;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification_;
import pt.ulisboa.ewp.node.domain.repository.AbstractRepository;
import pt.ulisboa.ewp.node.exception.domain.DomainException;
import pt.ulisboa.ewp.node.utils.i18n.MessageResolver;

@Repository
@Transactional
public class EwpChangeNotificationRepository
    extends AbstractRepository<EwpChangeNotification> {

  @Autowired
  @Lazy
  private MessageResolver messages;

  protected EwpChangeNotificationRepository(SessionFactory sessionFactory) {
    super(EwpChangeNotification.class, sessionFactory);
  }

  @Override
  @Transactional
  public boolean persist(EwpChangeNotification entity) {
    if (!super.persist(entity)) {
      return false;
    }

    mergeOldChangeNotifications(entity);

    return true;
  }

  public Optional<EwpChangeNotification> findById(long id) {
    return runInSession(
        session -> {
          CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
          CriteriaQuery<EwpChangeNotification> query = criteriaBuilder.createQuery(
              EwpChangeNotification.class);
          Root<EwpChangeNotification> selection = query.from(EwpChangeNotification.class);
          return session
              .createQuery(
                  query.where(criteriaBuilder.equal(selection.get(EwpChangeNotification_.ID), id)))
              .stream()
              .findFirst();
        });
  }

  public Collection<EwpChangeNotification> findAllPending() {
    return super.findAll().stream().filter(EwpChangeNotification::isPending)
        .collect(Collectors.toSet());
  }

  private void mergeOldChangeNotifications(EwpChangeNotification entity) {
    if (entity.isPending()) {
      findAll().stream()
          .filter(i -> i != entity && i.canBeMergedInto(entity))
          .forEach(i -> {
            i.markAsMerged();
            this.persist(i);
          });
    }
  }

  @Override
  protected boolean checkDomainConstraints(EwpChangeNotification entity)
      throws DomainException {
    if (entity.getScheduledDateTime() == null) {
      throw new DomainException(
          messages.get("error.ewp.change.notification.scheduleDateTime.must.be.defined"));
    }

    if (entity.getStatus() == null) {
      throw new DomainException(
          messages.get("error.ewp.change.notification.status.must.be.defined"));
    }

    return true;
  }
}
