package pe.LaCasona.backend_casona.models.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class PedidoDTO {
    private String nombre;
    private String apellidos;
    private String direccion;
    private String telefono;
    private String correo;
    private List<ItemPedido> items;
    private MetodoPago metodoPago;
    private int idRegistrador;
    private String RUC;
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

    public PedidoDTO(String nombre, String apellidos, String direccion, String telefono, String correo, List<ItemPedido> items, MetodoPago metodoPago, int idRegistrador, String RUC) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.direccion = direccion;
        this.telefono = telefono;
        this.correo = correo;
        this.items = items;
        this.metodoPago = metodoPago;
        this.idRegistrador = idRegistrador;
        this.RUC = RUC;
    }

    public PedidoDTO(String nombre, String apellidos, String telefono, String correo, List<ItemPedido> items, MetodoPago metodoPago, int idRegistrador, String RUC) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.correo = correo;
        this.items = items;
        this.metodoPago = metodoPago;
        this.idRegistrador = idRegistrador;
        this.RUC = RUC;
    }

    public PedidoDTO() {
    }
}
