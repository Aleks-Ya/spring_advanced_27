package booking.service.aspects;

import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

@EnableAspectJAutoProxy
@Import({DiscountAspect.class, LuckyWinnerAspect.class})
public class AspectConfig {
}
