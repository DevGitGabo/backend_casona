package pe.LaCasona.backend_casona.models.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class PedidosDTO {
    private String nombre;
    private String apellidos;
    private String direccion;
    private String telefono;
    private String correo;
    private List<PedidoDTO.ItemPedido> items;
    private MetodoPago metodoPago;
    private String nameUser;
    private String apellidoUser;
    private String nameCliente;
    private String apellidoCliente;
    private String ruc;

    public PedidosDTO() {
    }
}
