package it.aulab.progetto_finale_java.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import ch.qos.logback.core.model.Model;

@Controller
public class ExceptionHandlingController {
    // rotta per la gestione e cattura degli errori
    @GetMapping("/error/{number}")
    public String accessDenied(@PathVariable int number, Model model){
        if(number == 403){
            return "redirect:/?notAuthorized";
        }
        return "redirect:/";
    }
}
