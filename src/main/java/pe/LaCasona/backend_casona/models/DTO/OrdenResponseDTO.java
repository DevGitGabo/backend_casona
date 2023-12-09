package pe.LaCasona.backend_casona.models.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ToString
public class OrdenResponseDTO {
    private int id;
    private List<PedidoDTO.ItemPedido> items;
    private String status;
    private BigDecimal monto_total;
    @Getter
    @Setter
    public static class ItemPedido {
        private Producto producto;
        private int cantidad;
        public ItemPedido() {
        }
        public ItemPedido(Producto producto, int cantidad) {
            this.producto = producto;
            this.cantidad = cantidad;
        }
    }
}
