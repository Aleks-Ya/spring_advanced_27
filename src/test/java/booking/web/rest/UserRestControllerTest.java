package booking.web.rest;

import booking.BaseWebTest;
import booking.domain.User;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = UserRestController.class)
public class UserRestControllerTest extends BaseWebTest {

    @Test
    public void getById() throws Exception {
        User expUser = to.createJohn();
        MvcResult mvcResult = mvc.perform(get(UserRestController.ENDPOINT + "/" + expUser.getId()))
                .andExpect(status().isOk()).andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        User actUser = objectMapper.readValue(content, User.class);

        expUser.setPassword(null);
        assertThat(actUser, equalTo(expUser));
    }

    @Test
    public void getById_NotFound() throws Exception {
        mvc.perform(get(UserRestController.ENDPOINT + "/123"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().json("{error:'User is not found by id 123'}"));
    }

    @Test
    public void getAll() throws Exception {
        User expUser1 = to.createJohn();
        User expUser2 = to.createBookingManager();
        MvcResult mvcResult = mvc.perform(get(UserRestController.ENDPOINT))
                .andExpect(status().isOk()).andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        List<User> actUsers = objectMapper.readValue(content, new TypeReference<List<User>>() {
        });

        expUser1.setPassword(null);
        expUser2.setPassword(null);
        assertThat(actUsers, containsInAnyOrder(expUser1, expUser2));
    }
}