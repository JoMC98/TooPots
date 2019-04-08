package es.uji.ei1027.toopots.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomepageController {

    @RequestMapping("/")
    public String homepage(Model model) {
        return "index";
    }
}
