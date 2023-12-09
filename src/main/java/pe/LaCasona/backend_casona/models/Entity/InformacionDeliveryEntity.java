package pe.LaCasona.backend_casona.models.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "informacion_delivery")
@Getter
@Setter
public class InformacionDeliveryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_delivery")
    private Integer idDelivery;

    @Column(name = "direccion_delivery")
    private String direccionDelivery;

    @Column(name = "estado_delivery")
    private String estadoDelivery;

    @ManyToOne
    @JoinColumn(name = "id_orden")
    private OrdenEntity orden;
}
