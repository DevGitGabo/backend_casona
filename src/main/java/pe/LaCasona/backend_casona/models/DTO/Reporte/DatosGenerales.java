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
public class DatosGenerales {
    private BigDecimal totalVentas;
    private BigDecimal totalGanancias;
    private Map<String, BigDecimal> ventasPorMes;
    private Map<String, BigDecimal> gananciasPorMes;
    private ProductoReporteDTO productoMasVendido;
    private ProductoReporteDTO productoMasRentable;

    public DatosGenerales(BigDecimal totalVentas, BigDecimal totalGanancias, Map<String, BigDecimal> ventasPorMes, Map<String, BigDecimal> gananciasPorMes, ProductoReporteDTO productoMasVendido, ProductoReporteDTO productoMasRentable) {
        this.totalVentas = totalVentas;
        this.totalGanancias = totalGanancias;
        this.ventasPorMes = ventasPorMes;
        this.gananciasPorMes = gananciasPorMes;
        this.productoMasVendido = productoMasVendido;
        this.productoMasRentable = productoMasRentable;
    }
}
