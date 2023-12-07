package pe.LaCasona.backend_casona.models.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FiltroReporteDTO {
    private String Tipo;
    private String FechaInicio;
    private String FechaCulminacion;
    private List<Producto> productos;
}
