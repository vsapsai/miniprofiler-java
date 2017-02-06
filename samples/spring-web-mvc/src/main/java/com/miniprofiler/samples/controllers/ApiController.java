package com.miniprofiler.samples.controllers;

import com.miniprofiler.samples.CalculationResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Class to test and demonstrate MiniProfiler work when returning JSON instead of HTML.
 */
@Controller
@RequestMapping(value = "/api")
public class ApiController {
    @RequestMapping(value = "/sum-n-difference")
    @ResponseBody
    public CalculationResponse sumAndSubtractNumbers(@RequestParam("x") int x,
                                                     @RequestParam("y") int y) {
        try {
            Thread.sleep(60);
        } catch (InterruptedException e) {
            // Do nothing.
        }
        return new CalculationResponse(x + y, x - y);
    }
}
