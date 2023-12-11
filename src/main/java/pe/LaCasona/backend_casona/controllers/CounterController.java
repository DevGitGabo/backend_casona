package pe.LaCasona.backend_casona.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pe.LaCasona.backend_casona.models.DTO.PedidoDTO;
import pe.LaCasona.backend_casona.models.DTO.PedidoResponseDTO;
import pe.LaCasona.backend_casona.models.DTO.Reporte.ReporteDTO;
import pe.LaCasona.backend_casona.models.DTO.Reporte.ReporteResponseDTO;
import pe.LaCasona.backend_casona.services.OrderService;
import pe.LaCasona.backend_casona.services.ReporteService;

import java.util.List;

@RestController
@RequestMapping("/counter")
@CrossOrigin("*")
public class CounterController {
    @Autowired
    private ReporteService reporteService;
    @PostMapping("/generateReporte")
    public ReporteResponseDTO generateReporte(@RequestBody ReporteDTO body) {
        return reporteService.generateReporte(body);
    }

    @GetMapping("/getAllHistoriales")
    public List<ReporteResponseDTO> getAllHistoriales() {
        return reporteService.getAllHistoriales();
    }
}
