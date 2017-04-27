package com.zespolowka;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerTest {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mvc;

    @Before
    public void setUp() throws Exception {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .build();
    }

    @Test
    public void shoud_show_userDetail_page() throws Exception {
        mvc.perform(get("/user/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("userDetail"));
    }

   /* @Test
    public void should_process_edit_user() throws Exception {
        mvc.perform(post("/user/edit/1")
                .param("name", "adam")
                .param("lastName", "malysz")
                .param("email", "aaaaa@o2.pl")
                .param("passHash,","zaq1@WSX")
                .param("confirmPassword,","zaq1@WSX"))
                .andExpect(redirectedUrl("/user/1"));
    }*/

    @Test
    public void should_failed_edit_user() throws Exception {
        mvc.perform(post("/user/edit/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("userEdit"))
                .andExpect(model().errorCount(1));
    }
}
