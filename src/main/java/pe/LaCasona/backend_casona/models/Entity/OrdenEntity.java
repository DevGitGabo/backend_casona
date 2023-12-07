package pe.LaCasona.backend_casona.models.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "orden")
@Getter
@Setter
public class OrdenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orden")
    private Integer idOrden;

    @Column(name = "fecha_orden")
    private Date fechaOrden;

    private String estado;

    @Column(name = "monto_total")
    private BigDecimal montoTotal;

    @Column(name = "is_delivery")
    private boolean isDelivery;

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

}
