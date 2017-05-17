package com.zespolowka.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.ArrayList;

import com.zespolowka.service.TestService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.ui.Model;

public class HomeControllerTest {

	private HomeController controller;
	@Mock
	private TestService testService;

	@Mock
	private Model model;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		controller = new HomeController(testService);
	}

	@Test
	public void shouldShowHomePage() throws Exception {
		assertThat(controller.homePage(model)).isEqualTo("index");
		verify(model).addAttribute("archiveTest", new ArrayList<>());
		verify(model).addAttribute("activeTest", new ArrayList<>());
		verifyNoMoreInteractions(model);
	}

}