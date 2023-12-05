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
    private String email;
    @ManyToOne
    @JoinColumn(name = "id_RRHH")
    private RRHHEntity rrhh;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_user_junction",
        joinColumns = @JoinColumn(name = "id_usuario"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<AplicationUser> usuarios;
    public UsuarioEntity() {
    }
    public UsuarioEntity(Set<AplicationUser> usuarios) {
        this.usuarios = usuarios;
    }
    public Set<AplicationUser> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(Set<AplicationUser> usuarios) {
        this.usuarios = usuarios;
    }
}
