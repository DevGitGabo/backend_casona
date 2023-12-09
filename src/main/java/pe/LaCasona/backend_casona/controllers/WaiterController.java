package pe.LaCasona.backend_casona.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.LaCasona.backend_casona.models.DTO.*;
import pe.LaCasona.backend_casona.models.Entity.OrdenEntity;
import pe.LaCasona.backend_casona.services.OrderService;
import pe.LaCasona.backend_casona.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/waiter")
@CrossOrigin("*")
public class WaiterController {
    @Autowired
    private OrderService orderService;
    @PostMapping("/order")
    public PedidoResponseDTO registerOrder(@RequestBody PedidoDTO body) {
        return orderService.registerOrder(body);
    }
    @GetMapping("/getAll")
    public List<PedidosDTO> getAllOrders() {
        return orderService.getAllOrders();
    }
}
