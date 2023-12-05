package pe.LaCasona.backend_casona.models.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "camarero")
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

    // Getters and setters

}
