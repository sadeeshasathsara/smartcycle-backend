package com.smartcycle.smartcycleapplication;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeApplication {
    @RequestMapping("/")
    public String hello(){
        return "index.html";
    }

}
