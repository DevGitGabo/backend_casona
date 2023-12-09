package pe.LaCasona.backend_casona.models.DTO.Reporte;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class ReporteResponseDTO {
    private DatosGenerales datosGenerales;
    private List<ProductoReporteDTO> productos;

    public ReporteResponseDTO(DatosGenerales datosGenerales, List<ProductoReporteDTO> productos) {
        this.datosGenerales = datosGenerales;
        this.productos = productos;
    }
}
