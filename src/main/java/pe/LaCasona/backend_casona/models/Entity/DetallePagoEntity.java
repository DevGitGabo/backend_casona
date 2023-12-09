package pe.LaCasona.backend_casona.models.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "detalle_pago")
@Setter
@Getter
public class DetallePagoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_pago")
    private Integer idDetallePago;
    private BigDecimal IGV;
    @Column(name = "fecha_de_emision")
    private Date fechaDeEmision;

    @Column(name = "fecha_de_vencimiento")
    private Date fechaDeVencimiento;
    @Column(name = "estado_factura")
    private String estadoFactura;
    @Column(name = "pago_a_plazo")
    private Boolean pagoAPlazo;
    private BigDecimal total;
    @ManyToOne
    @JoinColumn(name = "id_metodo_pago")
    private MetodoPagoEntity metodoPago;
    @OneToOne(mappedBy = "detallePago", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private OrdenEntity orden;

    @PreUpdate
    public void actualizarEstado() {
        if (fechaDeVencimiento != null && new Date().after(fechaDeVencimiento)) {
            estadoFactura = "VENCIDO";
        }
    }
}
