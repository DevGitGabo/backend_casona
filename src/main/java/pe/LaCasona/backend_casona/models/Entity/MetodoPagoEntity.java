package pe.LaCasona.backend_casona.models.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "metodo_pago")
public class MetodoPagoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_metodo_pago")
    private Integer idMetodoPago;

    private String nombre;

    @ManyToOne
    @JoinColumn(name = "id_detalle_pago")
    private DetallePagoEntity detallePago;

    // Getters and setters

}
