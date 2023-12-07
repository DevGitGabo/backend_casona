package pe.LaCasona.backend_casona.models.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PedidoResponseDTO {
    private boolean status;
    public PedidoResponseDTO(boolean status) {
        this.status = status;
    }
    public PedidoResponseDTO() {
    }
}
