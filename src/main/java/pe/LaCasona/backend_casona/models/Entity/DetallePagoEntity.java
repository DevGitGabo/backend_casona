package pe.LaCasona.backend_casona.models.Entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "detalle_pago")
public class DetallePagoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_pago")
    private Integer idDetallePago;

    private BigDecimal IGV;

    @Column(name = "fecha_de_emision")
    private BigDecimal fechaDeEmision;

    @Column(name = "fecha_de_vencimiento")
    private Timestamp fechaDeVencimiento;

    @Column(name = "estado_factura")
    private String estadoFactura;

    @Column(name = "pago_a_plazo")
    private Boolean pagoAPlazo;

    private BigDecimal total;

    // Getters and setters

}
