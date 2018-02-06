package booking.web.controller;

import booking.domain.Event;
import booking.domain.Ticket;
import booking.domain.User;
import booking.service.BookingService;
import booking.service.EventService;
import booking.service.TicketService;
import booking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@SuppressWarnings("unused")
public class BookingController {
    public static final String ROOT_ENDPOINT = "/booking";
    public static final String TICKETS_ENDPOINT = ROOT_ENDPOINT + "/tickets";
    public static final String PRICE_ENDPOINT = ROOT_ENDPOINT + "/price";
    public static final String NEW_BOOKING_ENDPOINT = ROOT_ENDPOINT + "/new_booking";

    private static final String TICKETS_ATTR = "tickets";
    private static final String BOOKINGS_ATTR = "bookings";
    private static final String TICKET_ATTR = "ticket";
    private static final String EVENT_ATTR = "event";
    private static final String EVENTS_ATTR = "events";
    private static final String TICKET_PRICE_ATTR = "price";

    private static final String BOOKINGS_FTL = "booking/bookings";
    private static final String TICKET_FTL = "booking/ticket";
    private static final String TICKET_PRICE_FTL = "booking/ticket_price";
    private static final String TICKET_FOR_EVENT_FTL = "booking/ticket_for_event";
    private static final String BOOKED_TICKET_FTL = "booking/booked_ticket";
    private static final String NEW_BOOKING_FTL = "booking/new_booking";

    private final BookingService bookingService;
    private final UserService userService;
    private final EventService eventService;
    private final TicketService ticketService;

    @Autowired
    public BookingController(BookingService bookingService, UserService userService, EventService eventService,
                             TicketService ticketService) {
        this.bookingService = bookingService;
        this.userService = userService;
        this.eventService = eventService;
        this.ticketService = ticketService;
    }

    @RequestMapping(method = RequestMethod.GET)
    String getBookedTickets(@ModelAttribute(ControllerConfig.MODEL_ATTR) ModelMap model) {
        model.addAttribute(BOOKINGS_ATTR, bookingService.getAll());
        return BOOKINGS_FTL;
    }

    @RequestMapping(path = PRICE_ENDPOINT, method = RequestMethod.GET)
    String getTicketPrice(
            @RequestParam String eventName,
            @RequestParam String auditoriumName,
            @RequestParam Long userId,
            @RequestParam String localDateTime,
            @RequestParam String seats,
            @ModelAttribute(ControllerConfig.MODEL_ATTR) ModelMap model) {
        User user = userService.getById(userId);
        LocalDateTime date = LocalDateTime.parse(localDateTime);
        List<Integer> seatsList = SeatHelper.parseSeatsString(seats);
        double price = bookingService.getTicketPrice(eventName, String.valueOf(auditoriumName), date, seatsList, user);
        model.addAttribute(TICKET_PRICE_ATTR, price);
        return TICKET_PRICE_FTL;
    }

    @RequestMapping(path = TICKETS_ENDPOINT, method = RequestMethod.GET)
    String getTicketsForEvent(
            @RequestParam String eventId,
            @ModelAttribute(ControllerConfig.MODEL_ATTR) ModelMap model) {
        long eventIdLong = Long.parseLong(eventId);
        Event event = eventService.getById(eventIdLong);
        List<Ticket> tickets = bookingService.getTicketsForEvent(eventIdLong);
        model.addAttribute(TICKETS_ATTR, tickets);
        model.addAttribute(EVENT_ATTR, event);
        return TICKET_FOR_EVENT_FTL;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    String bookTicket(@RequestParam Long userId,
                      @RequestParam Long eventId,
                      @RequestParam(required = false) String localDateTime,
                      @RequestParam String seats,
                      @RequestParam(required = false) Double price,
                      @ModelAttribute(ControllerConfig.MODEL_ATTR) ModelMap model) {
        Ticket bookedTicket1 = bookingService.bookTicket(userId, eventId, seats, localDateTime, price).getTicket();

        User user = userService.getById(userId);
        Event event = eventService.getById(eventId);
        List<Integer> seatsList = Stream.of(seats.split(",")).map(Integer::valueOf).collect(Collectors.toList());
        LocalDateTime date = localDateTime != null ? LocalDateTime.parse(localDateTime) : event.getDateTime();
        Double priceValue = price != null ? price : event.getBasePrice();
        Ticket ticket = ticketService.create(new Ticket(event, date, seatsList, priceValue));

        Ticket bookedTicket = bookingService.bookTicket(user.getId(), ticket).getTicket();

        model.addAttribute(TICKET_ATTR, bookedTicket);
        return BOOKED_TICKET_FTL;
    }

    @RequestMapping(path = ROOT_ENDPOINT + "/id/{ticketId}", method = RequestMethod.GET)
    String getTicketById(@PathVariable Long ticketId, @ModelAttribute(ControllerConfig.MODEL_ATTR) ModelMap model) {
        Ticket ticket = ticketService.getTicketById(ticketId);
        model.addAttribute(TICKET_ATTR, ticket);
        return TICKET_FTL;
    }

    @RequestMapping(path = NEW_BOOKING_ENDPOINT, method = RequestMethod.GET)
    String bookNewTicket(@ModelAttribute(ControllerConfig.MODEL_ATTR) ModelMap model) {
        List<Event> events = eventService.getAll();
        model.addAttribute(EVENTS_ATTR, events);
        return NEW_BOOKING_FTL;
    }
}
