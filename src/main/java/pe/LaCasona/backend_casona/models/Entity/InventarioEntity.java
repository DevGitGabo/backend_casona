package pe.LaCasona.backend_casona.models.Entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "inventario")
public class InventarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventario")
    private Integer idInventario;

    private Timestamp dia;
    private String estado;

    // Getters and setters

}
