package pe.LaCasona.backend_casona.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/waiter")
@CrossOrigin("*")
public class WaiterController {

    @GetMapping("/")
    public String helloWaiterController() {
        return "Waiter access level";
    }
}
