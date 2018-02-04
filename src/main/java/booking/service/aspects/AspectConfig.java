package booking.service.aspects;

import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * @author Aleksey Yablokov
 */
@EnableAspectJAutoProxy
@Import({CounterAspect.class, DiscountAspect.class, LuckyWinnerAspect.class})
public class AspectConfig {
}
