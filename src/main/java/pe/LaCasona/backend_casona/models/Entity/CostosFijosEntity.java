package pe.LaCasona.backend_casona.models.Entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "costos_fijos")
public class CostosFijosEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_costos_fijos")
    private Integer idCostosFijos;

    private String descripcion;
    private BigDecimal monto;

    @Column(name = "fecha_vencimiento")
    private Timestamp fechaVencimiento;

    // Getters and setters

}
