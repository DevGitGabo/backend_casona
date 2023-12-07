package pe.LaCasona.backend_casona.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pe.LaCasona.backend_casona.models.DTO.PedidoDTO;
import pe.LaCasona.backend_casona.models.DTO.PedidoResponseDTO;
import pe.LaCasona.backend_casona.models.DTO.RegisterDTO;
import pe.LaCasona.backend_casona.models.DTO.RegisterResponseDTO;
import pe.LaCasona.backend_casona.services.OrderService;
import pe.LaCasona.backend_casona.services.UserService;

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
}
