package pe.LaCasona.backend_casona.models.DTO.Cashier;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pe.LaCasona.backend_casona.models.DTO.PedidoDTO;
import pe.LaCasona.backend_casona.models.DTO.Producto;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class FacturaOrders {
    private int id;
    private String status;
    private BigDecimal monto_total;
    public List<ItemDTO> items;
}
