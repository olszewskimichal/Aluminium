package com.zespolowka.integration;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.zespolowka.builders.UserBuilder;
import com.zespolowka.entity.user.User;
import com.zespolowka.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LoginIntegrationTest {
	@Autowired
	private WebApplicationContext context;

	@Autowired
	private UserRepository userRepository;

	private MockMvc mvc;


	@Before
	public void setUp() throws Exception {
		mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
	}

	@Test
	public void shouldLoginWithCorrectLoginAndPasswordAndActiveUser() throws Exception {
		//given
		User user = new UserBuilder("name", "lastName").withEmail("emailTest@o2.pl").build();
		user.unblock();
		user.enable();
		userRepository.save(user);
		String userLogin = "emailTest@o2.pl";
		String password = "zaq1@WSX";
		//when
		RequestBuilder requestBuilder = post("/login").param("username", userLogin).param("password", password);
		//then
		mvc.perform(requestBuilder).andDo(print()).andExpect(redirectedUrl("/")).andExpect(authenticated().withUsername("emailTest@o2.pl"));
	}

	@Test
	public void shouldFailedLoginWithNotActiveUser() throws Exception {
		//given
		User user = new UserBuilder("name", "lastName").withEmail("emailTest2@o2.pl").build();
		userRepository.save(user);
		String userLogin = "emailTest2@o2.pl";
		String password = "zaq1@WSX";
		//when
		RequestBuilder requestBuilder = post("/login").param("username", userLogin).param("password", password);
		//then
		mvc.perform(requestBuilder).andDo(print()).andExpect(redirectedUrl("/login-error")).andExpect(unauthenticated());
	}

	@Test
	public void shouldFailLoginWithNotExistingUserAndRedirect() throws Exception {
		//given
		String userLogin = "admin";
		String password = "incorrectPassword";
		//when
		RequestBuilder requestBuilder = formLogin("/login").user("username", userLogin).password("password", password);
		//then
		mvc.perform(requestBuilder).andDo(print()).andExpect(redirectedUrl("/login-error")).andExpect(unauthenticated());
	}

	@Test
	public void shouldReturnErrorMessage() throws Exception {
		//when
		mvc.perform(get("/login-error")).andDo(print())
				//then
				.andExpect(model().attribute("loginError", true)).andExpect(status().isOk()).andExpect(content().string(allOf(containsString("<div class=\"alert alert-danger\" role=\"alert\">")))).andExpect(view().name("login"));
	}
}
