package pe.LaCasona.backend_casona.models.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cliente")
public class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente;

    @Column(name = "primer_nombre")
    private String primerNombre;

    private String apellido;
    private String email;
    private Integer telefono;
    private String direccion;
    @Column(name = "numero_compra")
    private Integer numeroCompra;
    @Column(name = "numero_RUC")
    private String numeroRUC;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private UsuarioEntity usuario;

    // Getters and setters

}
