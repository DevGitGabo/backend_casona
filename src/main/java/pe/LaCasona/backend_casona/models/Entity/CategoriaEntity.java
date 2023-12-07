package pe.LaCasona.backend_casona.models.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categoria")
@Getter
@Setter
public class CategoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer idCategoria;

    @Column(name = "nombre_categoria")
    private String nombreCategoria;

    public CategoriaEntity(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public CategoriaEntity() {
    }
}
