package pe.LaCasona.backend_casona.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chef")
@CrossOrigin("*")
public class ChefController {

    @GetMapping("/")
    public String helloChefController() {
        return "Chef access level";
    }
}
