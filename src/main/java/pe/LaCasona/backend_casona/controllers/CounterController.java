package pe.LaCasona.backend_casona.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pe.LaCasona.backend_casona.models.DTO.PedidoDTO;
import pe.LaCasona.backend_casona.models.DTO.PedidoResponseDTO;
import pe.LaCasona.backend_casona.models.DTO.Reporte.ReporteDTO;
import pe.LaCasona.backend_casona.models.DTO.Reporte.ReporteResponseDTO;
import pe.LaCasona.backend_casona.services.OrderService;

@RestController
@RequestMapping("/counter")
@CrossOrigin("*")
public class CounterController {
    @Autowired
    private OrderService orderService;
    @PostMapping("/generateReporte")
    public ReporteResponseDTO generateReporte(@RequestBody ReporteDTO body) {
        return orderService.generateReporte(body);
    }
}
