package dev.boenkkk.simulator_outstation_dnp3.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/dashboard")
@Slf4j
public class DashboardController {

    @GetMapping(value = "")
    public ModelAndView index(ModelMap modelMap) {
        return new ModelAndView("app/dashboard", modelMap);
    }
}
