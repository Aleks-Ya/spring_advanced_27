package booking.repository.config;

import booking.domain.Auditorium;
import booking.repository.AuditoriumDAO;
import booking.repository.mocks.DBAuditoriumDAOMock;
import booking.service.AuditoriumService;
import booking.service.impl.AuditoriumServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: Dmytro_Babichev
 * Date: 2/12/2016
 * Time: 1:36 PM
 */
@Configuration
@Deprecated
public class TestAuditoriumConfig {

    @Bean
    public Auditorium testHall1() {
        return new Auditorium(1, "Test auditorium", 15, Arrays.asList(1, 2, 3, 4, 5));
    }

    @Bean
    public Auditorium testHall2() {
        return new Auditorium(2, "Test auditorium 2", 8, Collections.singletonList(1));
    }

    @Bean
    public AuditoriumDAO auditoriumDAOMock() {
        return new DBAuditoriumDAOMock(Arrays.asList(testHall1(), testHall2()));
    }

    @Bean
    public AuditoriumService auditoriumServiceImpl() {
        return new AuditoriumServiceImpl(auditoriumDAOMock());
    }
}
