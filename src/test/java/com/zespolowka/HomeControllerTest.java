package com.zespolowka;

import com.zespolowka.controller.HomeController;
import com.zespolowka.service.SolutionTestService;
import com.zespolowka.service.TestService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class HomeControllerTest {
    @Mock
    SolutionTestService solutionTestService;
    private MockMvc mvc;
    @Mock
    private TestService testService;

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.standaloneSetup(new HomeController(testService)).build();
    }

    @Test
    public void shoud_show_main_page() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }
}
