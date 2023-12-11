package pe.LaCasona.backend_casona.models.Entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import pe.LaCasona.backend_casona.models.DTO.Reporte.ReporteDTO;


import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "historial_reportes")
@Getter
@Setter
public class HistorialReporteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "fecha_creacion")
    private Date fechaCreacion;

    @Lob
    @Column(name = "detalle_reporte", columnDefinition = "TEXT")
    private String detalleReporte;
}
