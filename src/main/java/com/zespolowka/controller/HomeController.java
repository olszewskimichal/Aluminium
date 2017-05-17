package com.zespolowka.controller;

import java.time.LocalDate;

import com.zespolowka.service.TestService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@Slf4j
public class HomeController {

	private final TestService testService;

	@Autowired
	public HomeController(TestService testService) {
		this.testService = testService;
	}


	@RequestMapping(value = "/")
	public String homePage(Model model) {
		log.info("nazwa metody = homePage");
		model.addAttribute("archiveTest", testService.getTestByEndDateBefore(LocalDate.now()));
		model.addAttribute("activeTest", testService.getTestByEndDateAfter(LocalDate.now().minusDays(1)));
		return "index";
	}

}
