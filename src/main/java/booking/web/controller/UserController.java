package booking.web.controller;

import booking.domain.User;
import booking.service.UserService;
import booking.web.security.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@Controller
@SuppressWarnings("unused")
public class UserController {
    public static final String ROOT_ENDPOINT = "/user";
    public static final String REGISTER_ENDPOINT = ROOT_ENDPOINT + "/register";
    public static final String SHOW_ALL_USERS_ENDPOINT = ROOT_ENDPOINT + "/all";

    static final String USER_ATTR = "user";
    private static final String USERS_ATTR = "users";

    private static final String USER_FTL = "user/user";
    private static final String USERS_FTL = "user/users";
    private static final String USER_REGISTERED_FTL = "user/user_registered";

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(
            @Autowired UserService userService,
            @Autowired(required = false) PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping(path = REGISTER_ENDPOINT, method = RequestMethod.POST, consumes = APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    String register(@RequestBody MultiValueMap<String, String> formParams,
                    @ModelAttribute(ControllerConfig.MODEL_ATTR) ModelMap model) {
        String name = formParams.getFirst("name");
        String email = formParams.getFirst("email");
        String birthday = formParams.getFirst("birthday");
        String rawPassword = formParams.getFirst("password");
        String encodedPassword = encodePassword(passwordEncoder, rawPassword);
        User newUser = new User(email, name, LocalDate.parse(birthday), encodedPassword, Roles.REGISTERED_USER);
        User user = userService.register(newUser);
        model.addAttribute(USER_ATTR, user);
        return USER_REGISTERED_FTL;
    }

    static String encodePassword(PasswordEncoder passwordEncoder, String rawPassword) {
        String encodedPassword;
        if (passwordEncoder != null) {
            encodedPassword = passwordEncoder.encode(rawPassword);//TODO use JdbcUserDetailsManager#createUser
        } else {
            encodedPassword = rawPassword;
        }
        return encodedPassword;
    }

    @RequestMapping(value = ROOT_ENDPOINT + "/id/{userId}", method = RequestMethod.GET)
    String getById(@PathVariable Long userId, @ModelAttribute(ControllerConfig.MODEL_ATTR) ModelMap model) {
        User user = userService.getById(userId);
        model.addAttribute(USER_ATTR, user);
        return USER_FTL;
    }

    @RequestMapping(path = SHOW_ALL_USERS_ENDPOINT, method = RequestMethod.GET)
    String getAll(@ModelAttribute(ControllerConfig.MODEL_ATTR) ModelMap model) {
        List<User> users = userService.getAll();
        model.addAttribute(USERS_ATTR, users);
        return USERS_FTL;
    }
}
