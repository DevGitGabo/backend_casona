package pe.LaCasona.backend_casona.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pe.LaCasona.backend_casona.models.DTO.*;
import pe.LaCasona.backend_casona.services.OrderService;

import java.util.List;

@RestController
@RequestMapping("/chef")
@CrossOrigin("*")
public class ChefController {
    @Autowired private OrderService orderService;
    @GetMapping("/getAll")
    public List<OrdenResponseDTO> getAllOrdersForDay() {
        return orderService.getAllOrdersForDay();
    }
    @PutMapping("update/{id}")
    public CEResponseDTO updateUser(@PathVariable int id, @RequestBody CambioStatusDTO newStatus) {
        return orderService.updateStatus(id, newStatus);
    }
}
