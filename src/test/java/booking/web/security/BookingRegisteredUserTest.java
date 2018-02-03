package booking.web.security;

import booking.BaseWebSecurityTest;
import booking.domain.*;
import booking.web.controller.BookingController;
import booking.web.controller.LoginController;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static java.lang.String.format;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Check that an user (has REGISTERED_USER role) has access to all operations (exclude "get booked tickets for event").
 *
 * @author Aleksey Yablokov
 */
@ContextConfiguration(classes = {BookingController.class, LoginController.class})
public class BookingRegisteredUserTest extends BaseWebSecurityTest {

    @Test
    public void getBookedTickets() throws Exception {
        User user = testObjects.createJohn();
        MockHttpSession session = authenticateSession(user);
        mvc.perform(get(BookingController.ENDPOINT).session(session))
                .andExpect(status().isOk());
    }

    @Test
    public void getTicketById() throws Exception {
        User user = testObjects.createJohn();
        MockHttpSession session = authenticateSession(user);
        Ticket ticket = testObjects.createTicketToParty();
        mvc.perform(get(BookingController.ENDPOINT + "/id/" + ticket.getId()).session(session))
                .andExpect(status().isOk());
    }

    @Test
    public void bookTicket() throws Exception {
        User user = testObjects.createJohn();
        MockHttpSession session = authenticateSession(user);
        Event event = testObjects.createParty();
        mvc.perform(post(BookingController.ENDPOINT).session(session)
                .param("userId", String.valueOf(user.getId()))
                .param("eventId", String.valueOf(event.getId()))
                .param("localDateTime", "2007-12-03T10:15:30")
                .param("seats", "1,2,3")
                .param("price", "100.5")
        )
                .andExpect(status().isCreated());
    }

    @Test
    public void getTicketPrice() throws Exception {
        User user = testObjects.createJohn();

        MockHttpSession session = authenticateSession(user);

        Booking booking = testObjects.bookTicketToParty();
        Event event = booking.getTicket().getEvent();
        Auditorium auditorium = event.getAuditorium();
        LocalDateTime date = event.getDateTime();

        mvc.perform(get(BookingController.ENDPOINT + "/price").session(session)
                .param("eventName", event.getName())
                .param("auditoriumName", auditorium.getName())
                .param("userId", String.valueOf(user.getId()))
                .param("localDateTime", date.toString())
                .param("seats", "1,2,3")
        )
                .andExpect(status().isOk());
    }

    /**
     * User that possesses only {@link Roles#REGISTERED_USER} has no access to this endpoint
     * and must be redirected to The Access Denied page.
     */
    @Test
    public void getTicketsForEvent() throws Exception {
        User user = testObjects.createJohn();
        MockHttpSession session = authenticateSession(user);

        Booking booking = testObjects.bookTicketToParty();
        Event event = booking.getTicket().getEvent();

        MvcResult mvcResult = mvc.perform(get(BookingController.ENDPOINT + "/tickets")
                .session(session)
                .param("eventId", String.valueOf(event.getId())))
                .andExpect(status().isForbidden())
                .andReturn();

        String forwardedUrl = mvcResult.getResponse().getForwardedUrl();
        assertThat(forwardedUrl, endsWith(LoginController.ACCESS_DENIED_ENDPOINT));

        mvc.perform(get(forwardedUrl).session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(format(
                        "<p>Name: %s, E-mail: %s, Roles: REGISTERED_USER (<a href='/logout'>logout</a>)</p>\n" +
                                "<h1>Access denied</h1>\n",
                        user.getName(),
                        user.getEmail()
                )));
    }
}