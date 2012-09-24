package se.cygni.webapp.controllers.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import se.cygni.texasholdem.table.GamePlan;

import java.util.Locale;

@Controller
public class HouseRulesController {

    @Autowired
    GamePlan gamePlan;

    @RequestMapping(value = "/rules", method = RequestMethod.GET)
    public String home(Locale locale, Model model) {

        model.addAttribute("gamePlan", gamePlan);

        return "rules";
    }
}
