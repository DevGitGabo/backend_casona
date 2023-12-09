package pe.LaCasona.backend_casona.models.DTO.Cashier;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class BoletaDTO {
    private int boletaId;
    private List<ItemDTO> items;
    private BigDecimal total;
    private String clienteNombre;
    private String clienteDireccion;
    private String fechaEmision;
    private String metodoPago;

}
