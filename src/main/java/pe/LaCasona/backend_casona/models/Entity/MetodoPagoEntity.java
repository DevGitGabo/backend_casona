package pe.LaCasona.backend_casona.models.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pe.LaCasona.backend_casona.models.DTO.MetodoPago;

@Entity
@Table(name = "metodo_pago")
@Getter
@Setter
@ToString
public class MetodoPagoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_metodo_pago")
    private Integer idMetodoPago;
    @Enumerated(EnumType.STRING)
    private MetodoPago nombre;
    private String descripcion;
}
