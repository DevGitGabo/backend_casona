package pe.LaCasona.backend_casona.models.Entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "orden")
public class OrdenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orden")
    private Integer idOrden;

    @Column(name = "fecha_orden")
    private Timestamp fechaOrden;

    private String estado;

    @Column(name = "monto_total")
    private BigDecimal montoTotal;

    @Column(name = "is_delivery")
    private String isDelivery;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private ClienteEntity cliente;

    @ManyToOne
    @JoinColumn(name = "id_camarero")
    private CamareroEntity camarero;

    @ManyToOne
    @JoinColumn(name = "id_administrador")
    private AdministradorEntity administrador;

    @ManyToOne
    @JoinColumn(name = "id_metodo_pago")
    private MetodoPagoEntity metodoPago;

    // Getters and setters

}
