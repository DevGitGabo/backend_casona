package pe.LaCasona.backend_casona.models.DTO.Reporte;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Map;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class ProductoReporteDTO {
    private String nombreProducto;
    private int cantidadVendida;
    private BigDecimal gananciaTotal;
    private BigDecimal precioUnitario;
    private Map<String, Integer> ventasPorMesProducto;

    public ProductoReporteDTO(String nombreProducto, int cantidadVendida, BigDecimal gananciaTotal, BigDecimal precioUnitario, Map<String, Integer> ventasPorMesProducto) {
        this.nombreProducto = nombreProducto;
        this.cantidadVendida = cantidadVendida;
        this.gananciaTotal = gananciaTotal;
        this.precioUnitario = precioUnitario;
        this.ventasPorMesProducto = ventasPorMesProducto;
    }
}
