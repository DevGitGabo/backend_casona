package pe.LaCasona.backend_casona.models.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "administrador")
public class AdministradorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_administrador")
    private Integer idAdministrador;

    @Column(name = "primer_nombre")
    private String primerNombre;

    private String apellido;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private UsuarioEntity usuario;

    // Getters and setters

}
