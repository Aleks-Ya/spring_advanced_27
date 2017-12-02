package booking;

import booking.repository.RepositoryConfig;
import booking.service.ServiceConfig;
import booking.web.WebConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Aleksey Yablokov
 */
@Configuration
@Import({RepositoryConfig.class, ServiceConfig.class, WebConfig.class})
public class ApplicationConfig {
}
