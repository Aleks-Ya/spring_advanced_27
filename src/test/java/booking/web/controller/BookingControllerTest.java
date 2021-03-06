package booking.web.controller;

import booking.BaseWebTest;
import booking.domain.Booking;
import booking.domain.Event;
import booking.domain.Ticket;
import booking.domain.User;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;

import static booking.util.ResourceUtil.resourceToString;
import static booking.web.controller.RootControllerTest.NAVIGATOR;
import static java.lang.String.format;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = BookingController.class)
public class BookingControllerTest extends BaseWebTest {

    @Test
    public void getBookedTickets() throws Exception {
        Booking booking1 = to.bookTicketToParty();
        Ticket ticket1 = booking1.getTicket();
        User user1 = booking1.getUser();
        Booking booking2 = to.bookTicketToHackathon();
        Ticket ticket2 = booking2.getTicket();
        User user2 = booking2.getUser();

        mvc.perform(get(BookingController.SHOW_ALL_TICKETS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().string(format(LoginControllerTest.ANONYMOUS_HEADER +
                                NAVIGATOR +
                                "<h1>Booked tickets</h1>\n" +
                                "<p>Booking</p>\n" +
                                "<p>Id: %d</p>\n" +
                                "<p>User: %s</p>\n" +
                                "<p>Ticket</p>\n" +
                                "<p>Id: %d</p>\n" +
                                "<p>Event: New Year Party</p>\n" +
                                "<p>Date: 2018-12-31T23:00</p>\n" +
                                "<p>Seats: 100,101</p>\n" +
                                "<p>Price: 400</p><hr/>\n" +
                                "<p>Booking</p>\n" +
                                "<p>Id: %d</p>\n" +
                                "<p>User: %s</p>\n" +
                                "<p>Ticket</p>\n" +
                                "<p>Id: %d</p>\n" +
                                "<p>Event: Java Hackathon</p>\n" +
                                "<p>Date: 2018-03-13T09:00</p>\n" +
                                "<p>Seats: 100,101</p>\n" +
                                "<p>Price: 200</p><hr/>\n",
                        booking1.getId(), user1.getName(), ticket1.getId(),
                        booking2.getId(), user2.getName(), ticket2.getId()
                )));
    }

    @Test
    public void getTicketById() throws Exception {
        Ticket bookedTicket = to.bookTicketToParty().getTicket();
        mvc.perform(get(BookingController.ROOT_ENDPOINT + "/id/" + bookedTicket.getId())
        )
                .andExpect(status().isOk())
                .andExpect(content().string(format(LoginControllerTest.ANONYMOUS_HEADER +
                                "<h1>Ticket</h1>\n" +
                                "<p>Id: %s</p>\n" +
                                "<p>Event: %s</p>\n" +
                                "<p>Date: 2018-12-31T23:00</p>\n" +
                                "<p>Seats: 100,101</p>\n" +
                                "<p>Price: 400</p>",
                        bookedTicket.getId(), bookedTicket.getEvent().getName())));
    }

    @Test
    public void bookTicket() throws Exception {
        User user = to.createJohnWithAccount();
        Event event = to.createParty();

        assertThat(accountService.getByUserId(user.getId()).getAmount(),
                closeTo(BigDecimal.valueOf(10_000), BigDecimal.ZERO));

        mvc.perform(post(BookingController.ROOT_ENDPOINT)
                .param("userId", String.valueOf(user.getId()))
                .param("eventId", String.valueOf(event.getId()))
                .param("seats", "1,2,3")
                .param("price", "100")
        )
                .andExpect(status().isCreated())
                .andExpect(content().string(Matchers.matchesPattern(format(".*\n" + NAVIGATOR +
                                "<h1>The ticket is booked</h1>\n" +
                                "<p>Id: \\d+</p>\n" +
                                "<p>Event: %s</p>\n" +
                                "<p>Date: 2018-12-31T23:00</p>\n" +
                                "<p>Seats: 1,2,3</p>\n" +
                                "<p>Price: 100</p>",
                        event.getName()))));

        assertThat(accountService.getByUserId(user.getId()).getAmount(),
                closeTo(BigDecimal.valueOf(9700), BigDecimal.ZERO));
    }

    @Test
    public void newBooking() throws Exception {
        String body = resourceToString("BookingControllerTest_newBooking.html", BookingController.class);
        User user = to.createCurrentUser();
        mvc.perform(get(BookingController.NEW_BOOKING_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().string(format(body, user.getName(), user.getEmail(), user.getId())));
    }

    @Test
    public void getTicketPrice() throws Exception {
        User user = to.createJohn();
        Event event = to.createParty();

        mvc.perform(get(BookingController.PRICE_ENDPOINT)
                .param("eventId", String.valueOf(event.getId()))
                .param("userId", String.valueOf(user.getId()))
                .param("seats", "1,2,3")
        )
                .andExpect(status().isOk())
                .andExpect(content().string(LoginControllerTest.ANONYMOUS_HEADER +
                        "<h1>Ticket price</h1>\n" +
                        "1,440\n"));
    }

    @Test
    public void getTicketsForEvent() throws Exception {
        Booking booking = to.bookTicketToParty();
        Ticket ticket = booking.getTicket();
        Event event = booking.getTicket().getEvent();
        mvc.perform(get(BookingController.SHOW_TICKETS_BY_EVENT_ENDPOINT)
                .param("eventId", String.valueOf(event.getId()))
        )
                .andExpect(status().isOk())
                .andExpect(content().string(format(LoginControllerTest.ANONYMOUS_HEADER +
                                "<h1>Tickets for event</h1>\n" +
                                "<p>Event: %s</p>\n" +
                                "<p>Ticket</p>\n" +
                                "<p>Id: %d</p>\n" +
                                "<p>Event: %s</p>\n" +
                                "<p>Date: 2018-12-31T23:00</p>\n" +
                                "<p>Seats: 100,101</p>\n" +
                                "<p>Price: 400</p><hr/>\n",
                        event.getName(),
                        ticket.getId(),
                        event.getName()
                )));
    }
}