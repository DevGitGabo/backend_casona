package pe.LaCasona.backend_casona.models.DTO.Cashier;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class FacturaDTO {
    private int facturaId;
    private List<ItemDTO> items;
    private BigDecimal total;
    private String clienteNombre;
    private String clienteDireccion;
    private String clienteRUC;
    private String fechaEmision;
    private String metodoPago;
}
