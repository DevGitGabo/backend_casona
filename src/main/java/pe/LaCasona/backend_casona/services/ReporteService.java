package pe.LaCasona.backend_casona.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.LaCasona.backend_casona.models.DTO.Producto;
import pe.LaCasona.backend_casona.models.DTO.Reporte.DatosGenerales;
import pe.LaCasona.backend_casona.models.DTO.Reporte.ProductoReporteDTO;
import pe.LaCasona.backend_casona.models.DTO.Reporte.ReporteDTO;
import pe.LaCasona.backend_casona.models.DTO.Reporte.ReporteResponseDTO;
import pe.LaCasona.backend_casona.models.Entity.DetalleOrdenEntity;
import pe.LaCasona.backend_casona.models.Entity.DetallePagoEntity;
import pe.LaCasona.backend_casona.models.Entity.OrdenEntity;
import pe.LaCasona.backend_casona.models.Entity.ProductoEntity;
import pe.LaCasona.backend_casona.reposity.DetalleOrdenRepository;
import pe.LaCasona.backend_casona.reposity.OrdenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import pe.LaCasona.backend_casona.reposity.ProductoRepository;
import pe.LaCasona.backend_casona.utils.Log;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReporteService {

    @Autowired
    private OrdenRepository ordenRepository;
    @Autowired
    private DetalleOrdenRepository detalleOrdenRepository;
    @Autowired
    private ProductoRepository productoRepository;

    public ReporteResponseDTO generateReporte(ReporteDTO parametros) {
        ReporteResponseDTO reporte = new ReporteResponseDTO();

        if (parametros == null || todosParametrosVacios(parametros)) {
            // Todos los parámetros están vacíos, generamos el reporte general
            DatosGenerales datosGenerales = generarDatosGenerales();
            reporte.setDatosGenerales(datosGenerales);
            List<ProductoReporteDTO> productos = generarReporteTodosProductos();
            reporte.setProductos(productos);
        }else if (parametros.getProducto() != null && !parametros.getProducto().isEmpty()) {
            // Se proporcionaron productos específicos, generamos un informe para esos productos
            List<ProductoReporteDTO> productos = generarReporteProductosEspecificos(parametros.getProducto());
            reporte.setProductos(productos);
        }else if (parametros.getFechaInicio() != null && parametros.getFechaCulminacion() != null) {
            // Se proporcionaron fechas de inicio y culminación, generamos un informe filtrado por fechas
            List<ProductoReporteDTO> productos = generarReporteFiltradoPorFechas(parametros.getFechaInicio(), parametros.getFechaCulminacion());
            reporte.setProductos(productos);
            DatosGenerales datosGenerales = generarDatosGeneralesFiltradoPorFechas(parametros.getFechaInicio(), parametros.getFechaCulminacion());
            reporte.setDatosGenerales(datosGenerales);
        }

        return reporte;
    }

    private DatosGenerales generarDatosGeneralesFiltradoPorFechas(String fechaInicio, String fechaCulminacion) {
        // Convertir las fechas de texto a objetos Date
        Date fechaInicioObj = java.sql.Date.valueOf(LocalDate.parse(fechaInicio));
        Date fechaCulminacionObj = java.sql.Date.valueOf(LocalDate.parse(fechaCulminacion));

        // Obtener todas las órdenes en el rango de fechas
        List<OrdenEntity> ordenesEnRango = ordenRepository.findByFechaOrdenBetween(
                fechaInicioObj,
                fechaCulminacionObj
        );

        // Calcular total de ventas y ganancias solo para las órdenes en el rango de fechas
        BigDecimal totalVentas = ordenesEnRango.stream().map(OrdenEntity::getMontoTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalGanancias = ordenesEnRango.stream().map(orden -> orden.getDetallePago().getTotal()).reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular ventas por mes y ganancias por mes solo para las órdenes en el rango de fechas
        Map<String, BigDecimal> ventasPorMes = calcularVentasPorMes(ordenesEnRango);
        Map<String, BigDecimal> gananciasPorMes = calcularGananciasPorMes(ordenesEnRango);

        // Calcular producto más vendido y más rentable solo para las órdenes en el rango de fechas
        ProductoReporteDTO productoMasVendido = calcularProductoMasVendidoFiltradoPorFechas(ordenesEnRango);
        ProductoReporteDTO productoMasRentable = calcularProductoMasRentableFiltradoPorFechas(ordenesEnRango);

        DatosGenerales datosGenerales = new DatosGenerales();
        datosGenerales.setTotalVentas(totalVentas);
        datosGenerales.setTotalGanancias(totalGanancias);
        datosGenerales.setVentasPorMes(ventasPorMes);
        datosGenerales.setGananciasPorMes(gananciasPorMes);
        datosGenerales.setProductoMasVendido(productoMasVendido);
        datosGenerales.setProductoMasRentable(productoMasRentable);

        return datosGenerales;
    }

    private ProductoReporteDTO calcularProductoMasRentableFiltradoPorFechas(List<OrdenEntity> ordenesEnRango) {
        // Obtener todos los detalles de orden en el rango de fechas
        List<DetalleOrdenEntity> detallesOrdenEnRango = detalleOrdenRepository.findByOrdenIn(ordenesEnRango);

        // Agrupar por producto y sumar las ganancias
        Map<Integer, BigDecimal> gananciasPorProducto = detallesOrdenEnRango.stream()
                .collect(Collectors.groupingBy(
                        detalle -> detalle.getProducto().getIdProducto(),
                        Collectors.mapping(detalle -> {
                            BigDecimal precioUnitario = detalle.getProducto().getPrecioItem();
                            return precioUnitario.multiply(BigDecimal.valueOf(detalle.getCantidad()));
                        }, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        // Encontrar el producto con más ganancias
        Map.Entry<Integer, BigDecimal> productoMasRentableEntry = gananciasPorProducto.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        if (productoMasRentableEntry != null) {
            Integer idProductoMasRentable = productoMasRentableEntry.getKey();
            BigDecimal gananciaTotal = productoMasRentableEntry.getValue();

            // Obtener información adicional del producto
            ProductoEntity productoMasRentableEntity = productoRepository.findById(idProductoMasRentable).orElse(null);

            if (productoMasRentableEntity != null) {
                String nombreProductoMasRentable = productoMasRentableEntity.getProducto().toString();
                BigDecimal precioUnitario = productoMasRentableEntity.getPrecioItem();

                // Obtener las ventas por mes del producto
                Map<String, Integer> ventasPorMesProducto = obtenerVentasPorMesProducto(productoMasRentableEntity);

                return new ProductoReporteDTO(nombreProductoMasRentable, 0, precioUnitario, gananciaTotal, ventasPorMesProducto);
            }
        }

        return null;
    }

    private ProductoReporteDTO calcularProductoMasVendidoFiltradoPorFechas(List<OrdenEntity> ordenesEnRango) {
        // Obtener todos los detalles de orden en el rango de fechas
        List<DetalleOrdenEntity> detallesOrdenEnRango = detalleOrdenRepository.findByOrdenIn(ordenesEnRango);

        // Agrupar por producto y sumar las cantidades
        Map<Integer, Integer> ventasPorProducto = detallesOrdenEnRango.stream()
                .collect(Collectors.groupingBy(
                        detalle -> detalle.getProducto().getIdProducto(),
                        Collectors.summingInt(DetalleOrdenEntity::getCantidad)
                ));

        // Encontrar el producto con más ventas
        Map.Entry<Integer, Integer> productoMasVendidoEntry = ventasPorProducto.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        if (productoMasVendidoEntry != null) {
            Integer idProductoMasVendido = productoMasVendidoEntry.getKey();
            Integer cantidadMasVendida = productoMasVendidoEntry.getValue();

            // Obtener información adicional del producto
            ProductoEntity productoMasVendidoEntity = productoRepository.findById(idProductoMasVendido).orElse(null);

            if (productoMasVendidoEntity != null) {
                String nombreProductoMasVendido = productoMasVendidoEntity.getProducto().toString();
                BigDecimal precioUnitario = productoMasVendidoEntity.getPrecioItem();

                BigDecimal gananciaTotal = precioUnitario.multiply(BigDecimal.valueOf(cantidadMasVendida));

                // Obtener las ventas por mes del producto
                Map<String, Integer> ventasPorMesProducto = obtenerVentasPorMesProducto(productoMasVendidoEntity);

                return new ProductoReporteDTO(nombreProductoMasVendido, cantidadMasVendida, gananciaTotal, precioUnitario, ventasPorMesProducto);
            }
        }

        return null;
    }


    private List<ProductoReporteDTO> generarReporteFiltradoPorFechas(String fechaInicio, String fechaCulminacion) {
        // Convertir las fechas de texto a objetos java.sql.Date
        Date fechaInicioDate = java.sql.Date.valueOf(LocalDate.parse(fechaInicio));
        Date fechaCulminacionDate = java.sql.Date.valueOf(LocalDate.parse(fechaCulminacion));

        // Obtener todas las órdenes en el rango de fechas
        List<OrdenEntity> ordenesEnRango = ordenRepository.findByFechaOrdenBetween(
                fechaInicioDate,
                new Date(fechaCulminacionDate.getTime() + 24 * 60 * 60 * 1000 - 1)  // Fin del día de la fechaCulminacionDate
        );

        // Obtener los detalles de orden relacionados con las órdenes en el rango de fechas
        List<DetalleOrdenEntity> detallesOrdenEnRango = detalleOrdenRepository.findByOrdenIn(ordenesEnRango);

        // Agrupar los detalles por producto
        Map<Integer, List<DetalleOrdenEntity>> detallesPorProducto = detallesOrdenEnRango.stream()
                .collect(Collectors.groupingBy(detalle -> detalle.getProducto().getIdProducto()));

        // Generar informe para cada producto
        return detallesPorProducto.entrySet().stream()
                .map(entry -> generarReporteProductoFiltradoPorFechas(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private ProductoReporteDTO generarReporteProductoFiltradoPorFechas(Integer idProducto, List<DetalleOrdenEntity> detallesOrden) {
        // Obtener el producto correspondiente al ID
        Optional<ProductoEntity> productoEntityOptional = productoRepository.findById(idProducto);

        if (productoEntityOptional.isPresent()) {
            ProductoEntity productoEntity = productoEntityOptional.get();

            // Filtrar los detalles de orden por producto
            List<DetalleOrdenEntity> detallesOrdenConProducto = detallesOrden.stream()
                    .filter(detalle -> detalle.getProducto().equals(productoEntity))
                    .collect(Collectors.toList());

            // Obtener el nombre y precio unitario del producto
            String nombreProducto = productoEntity.getProducto().toString();
            BigDecimal precioUnitario = productoEntity.getPrecioItem();

            // Calcular la cantidad vendida y la ganancia total
            int cantidadVendida = detallesOrdenConProducto.stream()
                    .mapToInt(DetalleOrdenEntity::getCantidad)
                    .sum();

            BigDecimal gananciaTotal = detallesOrdenConProducto.stream()
                    .map(detalle -> {
                        BigDecimal precioUnitarioDetalle = detalle.getProducto().getPrecioItem();
                        return precioUnitarioDetalle.multiply(BigDecimal.valueOf(detalle.getCantidad()));
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Calcular las ventas por mes del producto
            Map<String, Integer> ventasPorMesProducto = obtenerVentasPorMesProducto(productoEntity, detallesOrdenConProducto);

            // Retorna el ProductoReporteDTO con la información filtrada por fechas
            return new ProductoReporteDTO(nombreProducto, cantidadVendida, gananciaTotal, precioUnitario, ventasPorMesProducto);
        }

        return null;
    }

    private Map<String, Integer> obtenerVentasPorMesProducto(ProductoEntity productoEntity, List<DetalleOrdenEntity> detallesOrdenConProducto) {
        Map<String, Integer> ventasPorMes = new HashMap<>();

        // Formato para obtener el mes y año (por ejemplo, "2023-01" para enero de 2023)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        // Itera sobre los detalles de las órdenes y cuenta las ventas por mes
        for (DetalleOrdenEntity detalleOrden : detallesOrdenConProducto) {
            LocalDate fechaOrden = detalleOrden.getOrden().getFechaOrden().toLocalDate();
            String mesYAnio = fechaOrden.format(formatter);

            // Incrementa la cuenta de ventas por mes
            ventasPorMes.put(mesYAnio, ventasPorMes.getOrDefault(mesYAnio, 0) + detalleOrden.getCantidad());
        }

        return ventasPorMes;
    }

    private List<ProductoReporteDTO> generarReporteProductosEspecificos(List<Producto> productos) {
        // Obtener los productos específicos
        List<ProductoEntity> productosEspecificos = obtenerProductosDesdeDTO(productos);

        // Generar informe para cada producto específico
        return productosEspecificos.stream()
                .map(this::generarReporteProducto)
                .collect(Collectors.toList());
    }

    private List<ProductoEntity> obtenerProductosDesdeDTO(List<Producto> productosDTO) {
        // Convertir los elementos del enum Producto a entidades ProductoEntity
        List<ProductoEntity> productosEspecificos = productosDTO.stream()
                .map(this::convertirProductoAEntity)
                .collect(Collectors.toList());

        return productosEspecificos;
    }

    private ProductoEntity convertirProductoAEntity(Producto producto) {
        // Busca el ProductoEntity en el repositorio según el nombre del producto en el enum
        ProductoEntity productoEntity = productoRepository.findByProducto(producto);

        // Verifica si se encontró el ProductoEntity en el repositorio
        if (productoEntity != null) {
            return productoEntity;
        } else {
            Log.logError("No se encontró el ProductoEntity para el producto: " + producto.toString());
        }

        return productoEntity;
    }

    private List<ProductoReporteDTO> generarReporteTodosProductos() {
        // Obtener todos los productos
        List<ProductoEntity> productos = productoRepository.findAll();

        // Generar informe para cada producto
        return productos.stream()
                .map(this::generarReporteProducto)
                .collect(Collectors.toList());
    }
    private ProductoReporteDTO generarReporteProducto(ProductoEntity productoEntity) {
        // Obtener todos los detalles de orden relacionados con el producto
        List<DetalleOrdenEntity> detallesOrdenConProducto = detalleOrdenRepository.findByProducto(productoEntity);

        // Sumar las cantidades vendidas
        int cantidadVendida = detallesOrdenConProducto.stream()
                .mapToInt(DetalleOrdenEntity::getCantidad)
                .sum();

        // Calcular la ganancia total
        BigDecimal gananciaTotal = detallesOrdenConProducto.stream()
                .map(detalle -> {
                    BigDecimal precioUnitario = detalle.getProducto().getPrecioItem();
                    return precioUnitario.multiply(BigDecimal.valueOf(detalle.getCantidad()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular las ventas por mes del producto
        Map<String, Integer> ventasPorMesProducto = obtenerVentasPorMesProducto(productoEntity);

        // Obtener el nombre y precio unitario del producto
        String nombreProducto = String.valueOf(productoEntity.getProducto());
        BigDecimal precioUnitario = productoEntity.getPrecioItem();

        return new ProductoReporteDTO(nombreProducto, cantidadVendida, gananciaTotal, precioUnitario, ventasPorMesProducto);
    }
    private boolean todosParametrosVacios(ReporteDTO parametros) {
        return parametros.getTipoReporte() == null
                && parametros.getFechaInicio() == null
                && parametros.getFechaCulminacion() == null
                && parametros.getProducto() == null;
    }

    private DatosGenerales generarDatosGenerales() {
        DatosGenerales datosGenerales = new DatosGenerales();

        // Obtener todas las órdenes
        List<OrdenEntity> ordenes = ordenRepository.findAll();

        // Calcular total de ventas y ganancias
        BigDecimal totalVentas = ordenes.stream().map(OrdenEntity::getMontoTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalGanancias = ordenes.stream().map(OrdenEntity::getDetallePago)
                .map(DetallePagoEntity::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular ventas por mes y ganancias por mes
        Map<String, BigDecimal> ventasPorMes = calcularVentasPorMes(ordenes);
        Map<String, BigDecimal> gananciasPorMes = calcularGananciasPorMes(ordenes);

        // Calcular producto más vendido y más rentable
        ProductoReporteDTO productoMasVendido = calcularProductoMasVendido();
        ProductoReporteDTO productoMasRentable = calcularProductoMasRentable();

        datosGenerales.setTotalVentas(totalVentas);
        datosGenerales.setTotalGanancias(totalGanancias);
        datosGenerales.setVentasPorMes(ventasPorMes);
        datosGenerales.setGananciasPorMes(gananciasPorMes);
        datosGenerales.setProductoMasVendido(productoMasVendido);
        datosGenerales.setProductoMasRentable(productoMasRentable);

        return datosGenerales;
    }

    private Map<String, BigDecimal> calcularVentasPorMes(List<OrdenEntity> ordenes) {
        Map<String, BigDecimal> ventasPorMes = new HashMap<>();

        for (OrdenEntity orden : ordenes) {
            YearMonth yearMonth = YearMonth.from(orden.getFechaOrden().toLocalDate());

            BigDecimal totalVentas = ventasPorMes.getOrDefault(yearMonth.toString(), BigDecimal.ZERO);
            totalVentas = totalVentas.add(orden.getMontoTotal());

            ventasPorMes.put(yearMonth.toString(), totalVentas);
        }

        return ventasPorMes;
    }

    private Map<String, BigDecimal> calcularGananciasPorMes(List<OrdenEntity> ordenes) {
        Map<String, BigDecimal> gananciasPorMes = new HashMap<>();

        for (OrdenEntity orden : ordenes) {
            YearMonth yearMonth = YearMonth.from(orden.getFechaOrden().toLocalDate());

            BigDecimal totalGanancias = gananciasPorMes.getOrDefault(yearMonth.toString(), BigDecimal.ZERO);
            totalGanancias = totalGanancias.add(orden.getDetallePago().getTotal());

            gananciasPorMes.put(yearMonth.toString(), totalGanancias);
        }

        return gananciasPorMes;
    }

    private ProductoReporteDTO calcularProductoMasVendido() {
        // Obtener todos los detalles de orden
        List<DetalleOrdenEntity> detallesOrden = detalleOrdenRepository.findAll();

        // Agrupar por producto y sumar las cantidades
        Map<Integer, Integer> ventasPorProducto = detallesOrden.stream()
                .collect(Collectors.groupingBy(
                        detalle -> detalle.getProducto().getIdProducto(),
                        Collectors.summingInt(DetalleOrdenEntity::getCantidad)
                ));

        // Encontrar el producto con más ventas
        Map.Entry<Integer, Integer> productoMasVendidoEntry = ventasPorProducto.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        if (productoMasVendidoEntry != null) {
            Integer idProductoMasVendido = productoMasVendidoEntry.getKey();
            Integer cantidadMasVendida = productoMasVendidoEntry.getValue();

            // Obtener información adicional del producto
            ProductoEntity productoMasVendidoEntity = productoRepository.findById(idProductoMasVendido).orElse(null);

            if (productoMasVendidoEntity != null) {
                String nombreProductoMasVendido = String.valueOf(productoMasVendidoEntity.getProducto());
                BigDecimal precioUnitario = productoMasVendidoEntity.getPrecioItem();

                BigDecimal gananciaTotal = precioUnitario.multiply(BigDecimal.valueOf(cantidadMasVendida));

                // Obtener las ventas por mes del producto
                Map<String, Integer> ventasPorMesProducto = obtenerVentasPorMesProducto(productoMasVendidoEntity);

                return new ProductoReporteDTO(nombreProductoMasVendido, cantidadMasVendida, gananciaTotal, precioUnitario, ventasPorMesProducto);
            }
        }

        return null;
    }
    private Map<String, Integer> obtenerVentasPorMesProducto(ProductoEntity productoEntity) {
        Map<String, Integer> ventasPorMes = new HashMap<>();

        // Obtén los detalles de las órdenes que contienen este producto
        List<DetalleOrdenEntity> detallesOrdenConProducto = detalleOrdenRepository.findByProducto(productoEntity);

        // Formato para obtener el mes y año (por ejemplo, "2023-01" para enero de 2023)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        // Itera sobre los detalles de las órdenes y cuenta las ventas por mes
        for (DetalleOrdenEntity detalleOrden : detallesOrdenConProducto) {
            LocalDate fechaOrden = detalleOrden.getOrden().getFechaOrden().toLocalDate();
            String mesYAnio = fechaOrden.format(formatter);

            // Incrementa la cuenta de ventas por mes
            ventasPorMes.put(mesYAnio, ventasPorMes.getOrDefault(mesYAnio, 0) + detalleOrden.getCantidad());
        }

        return ventasPorMes;
    }
    private ProductoReporteDTO calcularProductoMasRentable() {
        // Obtener todos los detalles de orden
        List<DetalleOrdenEntity> detallesOrden = detalleOrdenRepository.findAll();

        // Agrupar por producto y sumar las ganancias
        Map<Integer, BigDecimal> gananciasPorProducto = detallesOrden.stream()
                .collect(Collectors.groupingBy(
                        detalle -> detalle.getProducto().getIdProducto(),
                        Collectors.mapping(detalle -> {
                            BigDecimal precioUnitario = detalle.getProducto().getPrecioItem();
                            return precioUnitario.multiply(BigDecimal.valueOf(detalle.getCantidad()));
                        }, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        // Encontrar el producto con más ganancias
        Map.Entry<Integer, BigDecimal> productoMasRentableEntry = gananciasPorProducto.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        if (productoMasRentableEntry != null) {
            Integer idProductoMasRentable = productoMasRentableEntry.getKey();
            BigDecimal gananciaTotal = productoMasRentableEntry.getValue();

            // Obtener información adicional del producto
            ProductoEntity productoMasRentableEntity = productoRepository.findById(idProductoMasRentable).orElse(null);

            if (productoMasRentableEntity != null) {
                String nombreProductoMasRentable = productoMasRentableEntity.getProducto().toString();
                BigDecimal precioUnitario = productoMasRentableEntity.getPrecioItem();

                // Obtener las ventas por mes del producto
                Map<String, Integer> ventasPorMesProducto = obtenerVentasPorMesProducto(productoMasRentableEntity);

                return new ProductoReporteDTO(nombreProductoMasRentable, 0, precioUnitario, gananciaTotal, ventasPorMesProducto);
            }
        }

        return null;
    }
}
