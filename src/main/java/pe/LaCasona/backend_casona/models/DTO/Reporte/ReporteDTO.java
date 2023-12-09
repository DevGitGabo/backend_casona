package pe.LaCasona.backend_casona.models.DTO.Reporte;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pe.LaCasona.backend_casona.models.DTO.Producto;

import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class ReporteDTO {
    private String tipoReporte;
    private String fechaInicio;
    private String fechaCulminacion;
    private List<Producto> producto;
}
