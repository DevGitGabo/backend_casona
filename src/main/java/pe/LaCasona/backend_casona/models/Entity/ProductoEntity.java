package pe.LaCasona.backend_casona.models.Entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "producto")
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(name = "nombre_item")
    private String nombreItem;

    @Column(name = "precio_item")
    private BigDecimal precioItem;

    @ManyToOne
    @JoinColumn(name = "id_inventario")
    private InventarioEntity inventario;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private CategoriaEntity categoria;

    // Getters and setters

}
