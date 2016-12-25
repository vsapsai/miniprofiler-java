package com.miniprofiler.samples.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/")
public class HomeController {

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView index() {
        return new ModelAndView("index");
    }
}
