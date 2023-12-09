package pe.LaCasona.backend_casona.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.LaCasona.backend_casona.services.OrderChefServices;

@RestController
@RequestMapping("/chef")
@CrossOrigin("*")
public class ChefController {
    @Autowired OrderChefServices orderChefServices;
    @GetMapping("/")
    public String helloChefController() {
        return "Chef access level";
    }
}
