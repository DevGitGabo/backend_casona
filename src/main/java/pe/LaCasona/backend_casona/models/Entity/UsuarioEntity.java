package pe.LaCasona.backend_casona.models.Entity;

import jakarta.persistence.*;
import pe.LaCasona.backend_casona.models.Auth.AplicationUser;

import java.util.Set;

@Entity
@Table(name = "usuario")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "nombre_usuario")
    private String nombreUsuario;

    @Column(name = "hash_contraseña")
    private String hashContraseña;

    @Column(name = "tipo_usuario")
    private String tipoUsuario;

    private String email;

    @ManyToOne
    @JoinColumn(name = "id_RRHH")
    private RRHHEntity rrhh;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_role_junction",
        joinColumns = @JoinColumn(name = "id_usuario"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<AplicationUser> usuarios;

    public Set<AplicationUser> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(Set<AplicationUser> usuarios) {
        this.usuarios = usuarios;
    }
}
