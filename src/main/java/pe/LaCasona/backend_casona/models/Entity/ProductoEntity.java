package pe.LaCasona.backend_casona.models.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pe.LaCasona.backend_casona.models.DTO.Producto;

import java.math.BigDecimal;

@Entity
@Table(name = "producto")
@Getter
@Setter
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(name = "nombre_item")
    @Enumerated(EnumType.STRING)
    private Producto producto;

    @Column(name = "precio_item")
    private BigDecimal precioItem;

    @ManyToOne
    @JoinColumn(name = "id_inventario")
    private InventarioEntity inventario;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private CategoriaEntity categoria;

}
