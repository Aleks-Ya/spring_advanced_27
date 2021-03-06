package booking.web.controller;

import booking.domain.Event;
import booking.domain.Ticket;
import booking.service.BookingService;
import booking.service.EventService;
import booking.service.TicketService;
import booking.service.UserService;
import booking.web.pdf.BookingPdfHttpMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@SuppressWarnings("unused")
public class BookingController {
    public static final String ROOT_ENDPOINT = "/booking";
    public static final String SHOW_TICKETS_BY_EVENT_ENDPOINT = ROOT_ENDPOINT + "/tickets";
    public static final String SHOW_ALL_TICKETS_ENDPOINT = ROOT_ENDPOINT + "/all";
    public static final String PRICE_ENDPOINT = ROOT_ENDPOINT + "/price";
    public static final String NEW_BOOKING_ENDPOINT = ROOT_ENDPOINT + "/new_booking";
    public static final String SHOW_ALL_TICKETS_PDF_ENDPOINT = ROOT_ENDPOINT + "/all_pdf";

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

    @RequestMapping(path = SHOW_ALL_TICKETS_ENDPOINT, method = GET)
    String getBookedTickets(@ModelAttribute(ControllerConfig.MODEL_ATTR) ModelMap model) {
        model.addAttribute(BOOKINGS_ATTR, bookingService.getAll());
        return BOOKINGS_FTL;
    }

    @ResponseBody
    @RequestMapping(path = SHOW_ALL_TICKETS_PDF_ENDPOINT, method = GET, produces = APPLICATION_PDF_VALUE)
    BookingPdfHttpMessageConverter.BookingList  getBookedTicketsAsPdf() {
        return new BookingPdfHttpMessageConverter.BookingList(bookingService.getAll());
    }

    @RequestMapping(path = PRICE_ENDPOINT, method = GET)
    String getTicketPrice(
            @RequestParam String eventId,
            @RequestParam Long userId,
            @RequestParam String seats,
            @ModelAttribute(ControllerConfig.MODEL_ATTR) ModelMap model) {
        userService.getById(userId);
        List<Integer> seatsList = SeatHelper.parseSeatsString(seats);
        long eventIdLong = Long.parseLong(eventId);
        double price = bookingService.getTicketPrice(eventIdLong, seatsList, userId);
        model.addAttribute(TICKET_PRICE_ATTR, price);
        return TICKET_PRICE_FTL;
    }

    @RequestMapping(path = SHOW_TICKETS_BY_EVENT_ENDPOINT, method = GET)
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
                      @RequestParam String seats,
                      @RequestParam(required = false) Double price,
                      @ModelAttribute(ControllerConfig.MODEL_ATTR) ModelMap model) {
        Ticket bookedTicket = bookingService.bookTicket(userId, eventId, seats, price).getTicket();
        model.addAttribute(TICKET_ATTR, bookedTicket);
        return BOOKED_TICKET_FTL;
    }

    @RequestMapping(path = ROOT_ENDPOINT + "/id/{ticketId}", method = GET)
    String getTicketById(@PathVariable Long ticketId, @ModelAttribute(ControllerConfig.MODEL_ATTR) ModelMap model) {
        Ticket ticket = ticketService.getTicketById(ticketId);
        model.addAttribute(TICKET_ATTR, ticket);
        return TICKET_FTL;
    }

    @RequestMapping(path = NEW_BOOKING_ENDPOINT, method = GET)
    String bookNewTicket(@ModelAttribute(ControllerConfig.MODEL_ATTR) ModelMap model) {
        List<Event> events = eventService.getAll();
        model.addAttribute(EVENTS_ATTR, events);
        return NEW_BOOKING_FTL;
    }
}
