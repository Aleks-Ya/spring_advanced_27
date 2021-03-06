package booking.web.rest;

import booking.domain.Booking;
import booking.domain.Ticket;
import booking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static booking.web.rest.RestConfig.REST_ROOT_ENDPOINT;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@SuppressWarnings("unused")
class BookingRestController {
    public static final String ENDPOINT = REST_ROOT_ENDPOINT + "/booking";
    private static final String TICKETS_FOR_EVENT_ENDPOINT = ENDPOINT + "/tickets_for_event";

    private final BookingService bookingService;

    @Autowired
    public BookingRestController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @RequestMapping(path = ENDPOINT + "/{bookingId}", method = GET)
    Booking getById(@PathVariable Long bookingId) {
        return bookingService.getById(bookingId);
    }

    @RequestMapping(path = ENDPOINT, method = GET)
    List<Booking> getAll() {
        return bookingService.getAll();
    }

    @RequestMapping(path = TICKETS_FOR_EVENT_ENDPOINT, method = GET)
    List<Ticket> getTicketsForEvent(long eventId) {
        return bookingService.getTicketsForEvent(eventId);
    }

    @RequestMapping(path = ENDPOINT, method = POST)
    Booking book(@RequestParam Long userId,
                 @RequestParam Long eventId,
                 @RequestParam String seats,
                 @RequestParam(required = false) Double price) {
        return bookingService.bookTicket(userId, eventId, seats, price);
    }
}
