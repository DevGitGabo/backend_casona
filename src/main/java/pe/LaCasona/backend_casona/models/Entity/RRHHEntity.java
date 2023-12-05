package pe.LaCasona.backend_casona.models.Entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "RRHH")
public class RRHHEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_RRHH")
    private Integer idRRHH;

    @Column(name = "primer_nombre")
    private String primerNombre;

    private String cargo;

    @Column(name = "fecha_contrato")
    private Timestamp fechaContrato;

    private String email;
    private Integer telefono;

    @ManyToOne
    @JoinColumn(name = "id_costos_fijos")
    private CostosFijosEntity costosFijos;

    // Getters and setters

}
