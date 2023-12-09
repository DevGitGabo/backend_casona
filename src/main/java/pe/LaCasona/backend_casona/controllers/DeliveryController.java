package pe.LaCasona.backend_casona.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pe.LaCasona.backend_casona.models.DTO.PedidoDTO;
import pe.LaCasona.backend_casona.models.DTO.PedidoResponseDTO;
import pe.LaCasona.backend_casona.models.DTO.PedidosDTO;
import pe.LaCasona.backend_casona.services.OrderService;

import java.util.List;

@RestController
@RequestMapping("/delivery")
@CrossOrigin("*")
public class DeliveryController {
    @Autowired
    private OrderService orderService;
    @PostMapping("/order")
    public PedidoResponseDTO registerOrder(@RequestBody PedidoDTO body) {
        return orderService.registerOrder(body);
    }
    @GetMapping("/GetAll")
    public List<PedidosDTO> getAllOrders() {
        return orderService.getAllOrders();
    }
}
