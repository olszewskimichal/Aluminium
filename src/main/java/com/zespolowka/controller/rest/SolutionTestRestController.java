package com.zespolowka.controller.rest;

import com.zespolowka.entity.solutionTest.SolutionTest;
import com.zespolowka.service.SolutionTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/getTime/solutionTest")
public class SolutionTestRestController {
    @Autowired
    private SolutionTestService solutionTestService;

    @CrossOrigin
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Integer getTotalPages(@PathVariable("id") Long id) {
        SolutionTest solutionTest = solutionTestService.getSolutionTestById(id);
        return solutionTest.secondsToEnd().intValue();
    }

}
