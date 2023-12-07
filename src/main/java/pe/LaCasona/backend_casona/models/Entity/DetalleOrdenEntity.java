package pe.LaCasona.backend_casona.models.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "detalle_orden")
@Getter
@Setter
public class DetalleOrdenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_orden")
    private Integer idDetalleOrden;

    private Integer cantidad;

    @ManyToOne
    @JoinColumn(name = "id_orden")
    private OrdenEntity orden;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private ProductoEntity producto;

}
