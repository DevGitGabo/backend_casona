package pe.LaCasona.backend_casona.models.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "informacion_delivery")
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

    // Getters and setters

}
