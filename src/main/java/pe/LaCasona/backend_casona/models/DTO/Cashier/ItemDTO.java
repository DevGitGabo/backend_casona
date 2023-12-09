package pe.LaCasona.backend_casona.models.DTO.Cashier;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pe.LaCasona.backend_casona.models.DTO.Producto;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class ItemDTO {
    private Producto nombreProducto;
    private int cantidad;
    private BigDecimal precioUnitario;

    public ItemDTO(Producto nombreProducto, int cantidad, BigDecimal precioUnitario) {
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }
}
