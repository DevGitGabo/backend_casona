package pe.LaCasona.backend_casona.models.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "camarero")
@Getter
@Setter
public class CamareroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_camarero")
    private Integer idCamarero;

    @Column(name = "primer_nombre")
    private String primerNombre;

    private String apellido;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private UsuarioEntity usuario;

    public CamareroEntity(String primerNombre, String apellido, UsuarioEntity usuario) {
        this.primerNombre = primerNombre;
        this.apellido = apellido;
        this.usuario = usuario;
    }

    public CamareroEntity() {

    }
}
