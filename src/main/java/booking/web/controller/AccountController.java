package booking.web.controller;

import booking.domain.Account;
import booking.exception.BookingExceptionFactory;
import booking.service.AccountService;
import booking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.math.BigDecimal;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@Controller
@SuppressWarnings("unused")
public class AccountController {
    public static final String ROOT_ENDPOINT = "/account";
    public static final String REFILLING_ENDPOINT = ROOT_ENDPOINT + "/refilling";
    public static final String REFILLED_ENDPOINT = ROOT_ENDPOINT + "/refilled";

    private static final String USER_ATTR = "user";
    private static final String USERS_ATTR = "users";
    private static final String AMOUNT_BEFORE_ATTR = "amountBefore";
    private static final String AMOUNT_AFTER_ATTR = "amountAfter";
    private static final String AMOUNT_ATTR = "amount";

    private static final String REFILLING_FTL = "account/refilling";
    private static final String REFILLED_FTL = "account/refilled";

    private final AccountService accountService;
    private final UserService userService;

    @Autowired
    public AccountController(AccountService accountService, UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

    @RequestMapping(path = ROOT_ENDPOINT, method = RequestMethod.POST, consumes = APPLICATION_FORM_URLENCODED_VALUE)
    String refill(@RequestBody MultiValueMap<String, String> formParams,
                  @ModelAttribute(ControllerConfig.MODEL_ATTR) ModelMap model) {
        long userId = Long.parseLong(formParams.getFirst("userId"));
        Account account = accountService.getByUserId(userId);
        BigDecimal amountBefore = account.getAmount();
        String amount = formParams.getFirst("amount");

        BigDecimal refillAmount;
        try {
            refillAmount = BigDecimal.valueOf(Double.parseDouble(amount));
        } catch (Exception e) {
            throw BookingExceptionFactory.incorrect("Amount", amount);
        }

        Account refilledAccount = accountService.refill(account, refillAmount);

        model.addAttribute(USER_ATTR, refilledAccount.getUser());
        model.addAttribute(AMOUNT_BEFORE_ATTR, amountBefore);
        model.addAttribute(AMOUNT_AFTER_ATTR, refilledAccount.getAmount());
        model.addAttribute(AMOUNT_ATTR, refillAmount);
        return REFILLED_FTL;
    }

    @RequestMapping(path = REFILLING_ENDPOINT, method = RequestMethod.GET)
    String getRefillingPage(@ModelAttribute(ControllerConfig.MODEL_ATTR) ModelMap model) {
        model.addAttribute(USERS_ATTR, userService.getAll());
        return REFILLING_FTL;
    }
}
