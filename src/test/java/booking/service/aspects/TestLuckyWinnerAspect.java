package booking.service.aspects;

import booking.domain.config.PropertySourceConfig;
import booking.domain.config.TestAspectsConfig;
import booking.domain.config.db.DataSourceConfig;
import booking.domain.config.db.DbSessionFactoryConfig;
import booking.domain.daos.mocks.BookingDAOBookingMock;
import booking.domain.daos.mocks.DBAuditoriumDAOMock;
import booking.domain.daos.mocks.EventDAOMock;
import booking.domain.daos.mocks.UserDAOMock;
import booking.domain.models.Ticket;
import booking.domain.models.User;
import booking.service.BookingService;
import booking.service.aspects.mocks.LuckyWinnerAspectMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static junit.framework.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: Dmytro_Babichev
 * Date: 13/2/16
 * Time: 7:20 PM
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {PropertySourceConfig.class, DataSourceConfig.class,
        DbSessionFactoryConfig.class, TestAspectsConfig.class})
@Transactional
public class TestLuckyWinnerAspect {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingDAOBookingMock bookingDAOBookingMock;

    @Autowired
    private EventDAOMock eventDAOMock;

    @Autowired
    private UserDAOMock userDAOMock;

    @Autowired
    private LuckyWinnerAspectMock luckyWinnerAspectMock;

    @Autowired
    private DBAuditoriumDAOMock auditoriumDAOMock;

    @Before
    public void init() {
        LuckyWinnerAspectMock.cleanup();
        auditoriumDAOMock.init();
        userDAOMock.init();
        eventDAOMock.init();
        bookingDAOBookingMock.init();
    }

    @After
    public void cleanup() {
        LuckyWinnerAspectMock.cleanup();
        auditoriumDAOMock.cleanup();
        userDAOMock.cleanup();
        eventDAOMock.cleanup();
        bookingDAOBookingMock.cleanup();
    }

    @Test
    public void testCalculateDiscount() {
        User user = (User) applicationContext.getBean("testUser1");
        User discountUser = new User(user.getId(), user.getEmail(), user.getName(), LocalDate.now(), null, null);
        Ticket ticket1 = (Ticket) applicationContext.getBean("testTicket1");
        bookingService.bookTicket(discountUser,
                new Ticket(ticket1.getEvent(), ticket1.getDateTime(), Arrays.asList(5, 6), user, ticket1.getPrice()));
        bookingService.bookTicket(discountUser,
                new Ticket(ticket1.getEvent(), ticket1.getDateTime(), Arrays.asList(7, 8), user, ticket1.getPrice()));
        bookingService.bookTicket(discountUser,
                new Ticket(ticket1.getEvent(), ticket1.getDateTime(), Arrays.asList(9, 10), user, ticket1.getPrice()));
        bookingService.bookTicket(discountUser,
                new Ticket(ticket1.getEvent(), ticket1.getDateTime(), Arrays.asList(11, 12), user, ticket1.getPrice()));

        assertEquals(Collections.singletonList(user.getEmail()), LuckyWinnerAspectMock.getLuckyUsers());
    }
}