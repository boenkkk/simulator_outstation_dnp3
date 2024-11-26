package dev.boenkkk.simulator_outstation_dnp3.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
@Slf4j
public class DashboardController {

    @GetMapping(value = "/dashboard")
    public ModelAndView dashboard(ModelMap modelMap) {
        return new ModelAndView("dashboard", modelMap);
    }
}
