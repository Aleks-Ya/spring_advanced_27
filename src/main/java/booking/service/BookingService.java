package booking.service;

import booking.domain.Booking;
import booking.domain.Ticket;
import booking.domain.User;

import java.util.List;

/**
 * @author Aleksey Yablokov
 */
public interface BookingService {

    Booking create(User user, Ticket ticket);

    List<Booking> getAll();

    void delete(long bookingId);
}
