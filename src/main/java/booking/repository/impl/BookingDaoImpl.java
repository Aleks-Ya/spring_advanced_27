package booking.repository.impl;

import booking.domain.Booking;
import booking.domain.Ticket;
import booking.domain.User;
import booking.repository.BookingDao;
import booking.repository.TicketDao;
import booking.repository.UserDao;
import booking.service.UserService;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static booking.exception.BookingExceptionFactory.notFoundById;


public class BookingDaoImpl extends AbstractDao implements BookingDao {

    private final UserDao userDao;

    @Autowired
    public BookingDaoImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Booking create(long userId, Ticket ticket) {
        User user = userDao.getById(userId).orElseThrow(() -> notFoundById(Booking.class, userId));

        TicketDao.validateTicket(ticket);
        UserService.validateUser(user);

        Booking booking = new Booking(user, ticket);
        getCurrentSession().save(booking);
        return booking;
    }

    @Override
    public Optional<Booking> getById(long bookingId) {
        Query query = getCurrentSession().createQuery("from Booking b where b.id = :bookingId");
        query.setParameter("bookingId", bookingId);
        return Optional.ofNullable((Booking) query.uniqueResult());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Booking> getAll() {
        return ((List<Booking>) createBlankCriteria(Booking.class).list());
    }

    @Override
    public void delete(User user, Ticket ticket) {
        Query query = getCurrentSession().createQuery(
                "delete from Booking b where b.user = :user and b.ticket = :ticket");
        query.setParameter("user", user);
        query.setParameter("ticket", ticket);
        query.executeUpdate();
        getCurrentSession().delete(ticket);
    }

    @Override
    public void delete(long bookingId) {
        Query query = getCurrentSession().createQuery(
                "delete from Booking b where b.id = :bookingId");
        query.setParameter("bookingId", bookingId);
        query.executeUpdate();
    }

    @Override
    public long countTickets(long userId) {
        Query query = getCurrentSession()
                .createQuery("select count(*) from Booking b where b.user.id = :userId");
        query.setParameter("userId", userId);
        return (Long) query.uniqueResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Ticket> getTicketsForEvent(long eventId) {
        Query query = getCurrentSession()
                .createQuery("select b.ticket from Booking b where b.ticket.event.id = :eventId");
        query.setParameter("eventId", eventId);
        return ((List<Ticket>) query.list());
    }
}
