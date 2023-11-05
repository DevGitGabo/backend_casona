package pe.LaCasona.backend_casona.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cashier")
@CrossOrigin("*")
public class CashierController {

    @GetMapping("/")
    public String helloCashierController() {
        return "Cashier access level";
    }

}
