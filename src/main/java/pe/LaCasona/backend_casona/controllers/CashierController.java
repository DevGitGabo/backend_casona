package pe.LaCasona.backend_casona.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pe.LaCasona.backend_casona.models.DTO.CEResponseDTO;
import pe.LaCasona.backend_casona.models.DTO.CambioStatusDTO;
import pe.LaCasona.backend_casona.models.DTO.Cashier.BoletaDTO;
import pe.LaCasona.backend_casona.models.DTO.Cashier.FacturaDTO;
import pe.LaCasona.backend_casona.models.DTO.Cashier.FacturaOrders;
import pe.LaCasona.backend_casona.models.DTO.OrdenResponseDTO;
import pe.LaCasona.backend_casona.services.OrderService;

import java.util.List;

@RestController
@RequestMapping("/cashier")
@CrossOrigin("*")
public class CashierController {
    @Autowired
    private OrderService orderService;
    @PutMapping("update/{id}")
    public CEResponseDTO updateUser(@PathVariable int id, @RequestBody CambioStatusDTO newStatus) {
        return orderService.updateStatusCashier(id, newStatus);
    }
    @GetMapping("/getAll")
    public List<FacturaOrders> getAllEntregadosForDay() {
        return orderService.getAllEntregadosForDay();
    }
    @GetMapping("factura/{id}")
    public FacturaDTO generateFactura(@PathVariable int id) {
        return orderService.generateFactura(id);
    }
    @GetMapping("boleta/{id}")
    public BoletaDTO generateBoleta(@PathVariable int id) {
        return orderService.generateBoleta(id);
    }
}
