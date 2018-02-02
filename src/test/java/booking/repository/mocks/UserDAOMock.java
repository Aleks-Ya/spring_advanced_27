package booking.repository.mocks;

import booking.domain.User;
import booking.repository.impl.UserDAOImpl;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Dmytro_Babichev
 * Date: 06/2/16
 * Time: 2:41 PM
 */
@Deprecated
public class UserDAOMock extends UserDAOImpl {

    private final List<User> users;

    public UserDAOMock(List<User> users) {
        this.users = users;
    }

    public void init() {
        cleanup();
        users.forEach(this :: create);
    }

    public void cleanup() {
        getAll().forEach(this :: delete);
    }
}
