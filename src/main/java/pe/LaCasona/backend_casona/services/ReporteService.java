package pe.LaCasona.backend_casona.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.LaCasona.backend_casona.models.DTO.Producto;
import pe.LaCasona.backend_casona.models.DTO.Reporte.DatosGenerales;
import pe.LaCasona.backend_casona.models.DTO.Reporte.ProductoReporteDTO;
import pe.LaCasona.backend_casona.models.DTO.Reporte.ReporteDTO;
import pe.LaCasona.backend_casona.models.DTO.Reporte.ReporteResponseDTO;
import pe.LaCasona.backend_casona.models.Entity.*;
import pe.LaCasona.backend_casona.reposity.DetalleOrdenRepository;
import pe.LaCasona.backend_casona.reposity.HistorialReporteRepository;
import pe.LaCasona.backend_casona.reposity.OrdenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import pe.LaCasona.backend_casona.reposity.ProductoRepository;
import pe.LaCasona.backend_casona.utils.Log;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
    @Autowired
    private HistorialReporteRepository historialReporteRepository;

    public List<ReporteResponseDTO> getAllHistoriales() {
        List<HistorialReporteEntity> historiales = historialReporteRepository.findAll();

        return historiales.stream()
                .map(this::convertirStringAReporteDTO)
                .collect(Collectors.toList());
    }

    private ReporteResponseDTO convertirStringAReporteDTO(HistorialReporteEntity historialReporteEntity) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ReporteResponseDTO reporteDTO = objectMapper.readValue(historialReporteEntity.getDetalleReporte(), ReporteResponseDTO.class);
            return reporteDTO;
        } catch (Exception e) {
            e.printStackTrace();  // Manejo apropiado de la excepción según tus necesidades
            return null;
        }
    }

    public ReporteResponseDTO generateReporte(ReporteDTO parametros) {
        ReporteResponseDTO reporte = new ReporteResponseDTO();

        if (parametros == null || todosParametrosVacios(parametros)) {
            // Todos los parámetros están vacíos, generamos el reporte general
            DatosGenerales datosGenerales = generarDatosGenerales();
            reporte.setDatosGenerales(datosGenerales);
            List<ProductoReporteDTO> productos = generarReporteTodosProductos();
            reporte.setProductos(productos);
        } else if (parametros.getProducto() != null && !parametros.getProducto().isEmpty()) {
            // Se proporcionaron productos específicos
            List<ProductoEntity> productosEspecificos = obtenerProductosDesdeDTO(parametros.getProducto());

            if (parametros.getFechaInicio() != null && parametros.getFechaCulminacion() != null) {
                // Se proporcionaron fechas de inicio y culminación, generamos un informe filtrado por fechas y productos
                DatosGenerales datosGenerales = generarDatosGeneralesFiltradoPorFechasYProductos(
                        parametros.getFechaInicio(),
                        parametros.getFechaCulminacion(),
                        productosEspecificos
                );
                reporte.setDatosGenerales(datosGenerales);

                List<ProductoReporteDTO> productos = generarReporteFiltradoPorFechasYProductos(
                        parametros.getFechaInicio(),
                        parametros.getFechaCulminacion(),
                        parametros.getProducto()
                );
                reporte.setProductos(productos);
            } else {
                // No se proporcionaron fechas, generamos un informe solo para productos específicos
                DatosGenerales datosGenerales = generarDatosGeneralesProductosEspecificos(productosEspecificos);
                reporte.setDatosGenerales(datosGenerales);

                List<ProductoReporteDTO> productos = generarReporteProductosEspecificos(parametros.getProducto());
                reporte.setProductos(productos);
            }
        } else if (parametros.getFechaInicio() != null && parametros.getFechaCulminacion() != null) {
            // Se proporcionaron solo fechas de inicio y culminación, generamos un informe filtrado por fechas
            List<ProductoReporteDTO> productos = generarReporteFiltradoPorFechas(
                    parametros.getFechaInicio(),
                    parametros.getFechaCulminacion()
            );
            reporte.setProductos(productos);

            DatosGenerales datosGenerales = generarDatosGeneralesFiltradoPorFechas(
                    parametros.getFechaInicio(),
                    parametros.getFechaCulminacion()
            );
            reporte.setDatosGenerales(datosGenerales);
        }

        // Guardar en el historial
        HistorialReporteEntity historialReporte = new HistorialReporteEntity();
        historialReporte.setFechaCreacion(new Date());
        historialReporte.setDetalleReporte(convertirReporteDTOAString(reporte));  // Ajusta esto según cómo quieras almacenar el detalle

        // Guardar la entidad en la base de datos
        historialReporte = historialReporteRepository.save(historialReporte);

        // Acceder al ID generado
        int idGenerado = historialReporte.getId();

        // Asignar el ID al reporte (ajusta esto según la estructura de tu ReporteResponseDTO)
        reporte.setId(idGenerado);

        historialReporte.setDetalleReporte(convertirReporteDTOAString(reporte));

        historialReporteRepository.save(historialReporte);

        return reporte;
    }

    private String convertirReporteDTOAString(ReporteResponseDTO reporteDTO) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(reporteDTO);
        } catch (Exception e) {
            e.printStackTrace();  // Manejo apropiado de la excepción según tus necesidades
            return null;
        }
    }

    public List<ProductoReporteDTO> generarReporteFiltradoPorFechasYProductos(
            String fechaInicio, String fechaCulminacion, List<Producto> productosEspecificos) {
        // Convertir las fechas de texto a objetos Date
        Date fechaInicioObj = java.sql.Date.valueOf(LocalDate.parse(fechaInicio));
        Date fechaCulminacionObj = java.sql.Date.valueOf(LocalDate.parse(fechaCulminacion));

        // Obtener todas las órdenes en el rango de fechas
        List<OrdenEntity> ordenesEnRango = ordenRepository.findByFechaOrdenBetween(
                fechaInicioObj,
                fechaCulminacionObj
        );

        // Filtrar los detalles de orden relacionados con las órdenes en el rango de fechas
        List<DetalleOrdenEntity> detallesOrdenEnRango = detalleOrdenRepository.findByOrdenIn(ordenesEnRango);

        // Filtrar los detalles de orden por productos específicos
        List<DetalleOrdenEntity> detallesOrdenConProductosEspecificos = detallesOrdenEnRango.stream()
                .filter(detalle -> productosEspecificos.contains(detalle.getProducto().getProducto()))
                .collect(Collectors.toList());

        // Agrupar por producto y sumar las cantidades
        Map<Integer, Integer> ventasPorProducto = detallesOrdenConProductosEspecificos.stream()
                .collect(Collectors.groupingBy(
                        detalle -> detalle.getProducto().getIdProducto(),
                        Collectors.summingInt(DetalleOrdenEntity::getCantidad)
                ));

        // Crear la lista de ProductoReporteDTO a partir de las ventas por producto
        List<ProductoReporteDTO> reporteProductos = ventasPorProducto.entrySet().stream()
                .map(entry -> {
                    Integer idProducto = entry.getKey();
                    Integer cantidadVendida = entry.getValue();

                    // Obtener información adicional del producto
                    ProductoEntity productoEntity = productoRepository.findById(idProducto).orElse(null);

                    if (productoEntity != null) {
                        String nombreProducto = productoEntity.getProducto().toString();
                        BigDecimal precioUnitario = productoEntity.getPrecioItem();
                        BigDecimal gananciaTotal = precioUnitario.multiply(BigDecimal.valueOf(cantidadVendida));

                        // Obtener las ventas por mes del producto
                        Map<String, Integer> ventasPorMes = calcularVentasPorMesParaProducto(
                                detallesOrdenConProductosEspecificos,
                                productoEntity
                        );

                        ProductoReporteDTO productoReporteDTO = new ProductoReporteDTO();
                        productoReporteDTO.setNombreProducto(nombreProducto);
                        productoReporteDTO.setCantidadVendida(cantidadVendida);
                        productoReporteDTO.setGananciaTotal(gananciaTotal);
                        productoReporteDTO.setVentasPorMesProducto(ventasPorMes);
                        productoReporteDTO.setPrecioUnitario(productoEntity.getPrecioItem());

                        return productoReporteDTO;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return reporteProductos;
    }

    private DatosGenerales generarDatosGeneralesFiltradoPorFechasYProductos(
            String fechaInicio, String fechaCulminacion, List<ProductoEntity> productosEspecificos) {
        // Convertir las fechas de texto a objetos Date
        Date fechaInicioObj = java.sql.Date.valueOf(LocalDate.parse(fechaInicio));
        Date fechaCulminacionObj = java.sql.Date.valueOf(LocalDate.parse(fechaCulminacion));

        // Obtener todas las órdenes en el rango de fechas
        List<OrdenEntity> ordenesEnRango = ordenRepository.findByFechaOrdenBetween(
                fechaInicioObj,
                fechaCulminacionObj
        );

        // Filtrar los detalles de orden relacionados con las órdenes en el rango de fechas
        List<DetalleOrdenEntity> detallesOrdenEnRango = detalleOrdenRepository.findByOrdenIn(ordenesEnRango);

        // Filtrar los detalles de orden por productos específicos
        List<DetalleOrdenEntity> detallesOrdenConProductosEspecificos = detallesOrdenEnRango.stream()
                .filter(detalle -> productosEspecificos.contains(detalle.getProducto()))
                .collect(Collectors.toList());

        // Agrupar detalles de orden por orden y sumar montos correspondientes
        Map<OrdenEntity, BigDecimal> montoPorOrden = detallesOrdenConProductosEspecificos.stream()
                .collect(Collectors.groupingBy(
                        DetalleOrdenEntity::getOrden,
                        Collectors.mapping(detalle -> detalle.getProducto().getPrecioItem().multiply(BigDecimal.valueOf(detalle.getCantidad())), Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        // Calcular total de ventas y ganancias
        Integer totaleVentas = detallesOrdenConProductosEspecificos.stream()
                .map(DetalleOrdenEntity::getCantidad)
                .reduce(0, Integer::sum);

        BigDecimal totalVentas = BigDecimal.valueOf(totaleVentas);

        BigDecimal totalGanancias = detallesOrdenConProductosEspecificos.stream()
                .map(detalle -> detalle.getProducto().getPrecioItem().multiply(BigDecimal.valueOf(detalle.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular ventas por mes y ganancias por mes
        Map<String, BigDecimal> ventasPorMes = calcularVentasPorMes(detallesOrdenConProductosEspecificos);
        Map<String, BigDecimal> gananciasPorMes = calcularGananciasPorMesAD(detallesOrdenConProductosEspecificos,productosEspecificos);

        // Calcular producto más vendido y más rentable
        ProductoReporteDTO productoMasVendido = calcularProductoMasVendidoFiltradoPorFechasYProductos(ordenesEnRango, productosEspecificos);
        ProductoReporteDTO productoMasRentable = calcularProductoMasRentableFiltradoPorFechasYProductos(ordenesEnRango, productosEspecificos);

        DatosGenerales datosGenerales = new DatosGenerales();
        datosGenerales.setTotalVentas(totalVentas);
        datosGenerales.setTotalGanancias(totalGanancias);
        datosGenerales.setVentasPorMes(ventasPorMes);
        datosGenerales.setGananciasPorMes(gananciasPorMes);
        datosGenerales.setProductoMasVendido(productoMasVendido);
        datosGenerales.setProductoMasRentable(productoMasRentable);

        return datosGenerales;
    }

    private ProductoReporteDTO calcularProductoMasVendidoFiltradoPorFechasYProductos(
            List<OrdenEntity> ordenesEnRango, List<ProductoEntity> productosEspecificos) {
        // Obtener todos los detalles de orden en el rango de fechas
        List<DetalleOrdenEntity> detallesOrdenEnRango = detalleOrdenRepository.findByOrdenIn(ordenesEnRango);

        // Filtrar los detalles por productos específicos
        List<DetalleOrdenEntity> detallesOrdenConProductosEspecificos = detallesOrdenEnRango.stream()
                .filter(detalle -> productosEspecificos.contains(detalle.getProducto()))
                .collect(Collectors.toList());

        // Agrupar por producto y sumar las cantidades
        Map<Integer, Integer> ventasPorProducto = detallesOrdenConProductosEspecificos.stream()
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
                Map<String, Integer> ventasPorMes = calcularVentasPorMesParaProducto(
                        detallesOrdenConProductosEspecificos,
                        productoMasVendidoEntity
                );

                ProductoReporteDTO productoMasVendido = new ProductoReporteDTO();
                productoMasVendido.setNombreProducto(nombreProductoMasVendido);
                productoMasVendido.setCantidadVendida(cantidadMasVendida);
                productoMasVendido.setGananciaTotal(gananciaTotal);
                productoMasVendido.setVentasPorMesProducto(ventasPorMes);
                productoMasVendido.setPrecioUnitario(precioUnitario);

                return productoMasVendido;
            }
        }

        return null;
    }

    private ProductoReporteDTO calcularProductoMasRentableFiltradoPorFechasYProductos(
            List<OrdenEntity> ordenesEnRango, List<ProductoEntity> productosEspecificos) {
        // Obtener todos los detalles de orden en el rango de fechas
        List<DetalleOrdenEntity> detallesOrdenEnRango = detalleOrdenRepository.findByOrdenIn(ordenesEnRango);

        // Filtrar los detalles por productos específicos
        List<DetalleOrdenEntity> detallesOrdenConProductosEspecificos = detallesOrdenEnRango.stream()
                .filter(detalle -> productosEspecificos.contains(detalle.getProducto()))
                .collect(Collectors.toList());

        // Agrupar por producto y sumar las ganancias
        Map<Integer, BigDecimal> gananciasPorProducto = detallesOrdenConProductosEspecificos.stream()
                .collect(Collectors.groupingBy(
                        detalle -> detalle.getProducto().getIdProducto(),
                        Collectors.mapping(
                                detalle -> {
                                    BigDecimal precioUnitario = detalle.getProducto().getPrecioItem();
                                    return precioUnitario.multiply(BigDecimal.valueOf(detalle.getCantidad()));
                                },
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));

        // Encontrar el producto más rentable
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
                Integer cantidadVendida = detallesOrdenConProductosEspecificos.stream()
                        .filter(detalle -> detalle.getProducto().getIdProducto().equals(idProductoMasRentable))
                        .mapToInt(DetalleOrdenEntity::getCantidad)
                        .sum();

                // Obtener las ventas por mes del producto
                Map<String, Integer> ventasPorMes = calcularVentasPorMesParaProducto(
                        detallesOrdenConProductosEspecificos,
                        productoMasRentableEntity
                );

                ProductoReporteDTO productoMasRentable = new ProductoReporteDTO();
                productoMasRentable.setNombreProducto(nombreProductoMasRentable);
                productoMasRentable.setCantidadVendida(cantidadVendida);
                productoMasRentable.setGananciaTotal(gananciaTotal);
                productoMasRentable.setVentasPorMesProducto(ventasPorMes);
                productoMasRentable.setPrecioUnitario(productoMasRentableEntity.getPrecioItem());

                return productoMasRentable;
            }
        }

        return null;
    }

    private Map<String, Integer> calcularVentasPorMesParaProducto(
            List<DetalleOrdenEntity> detallesOrden, ProductoEntity producto) {
        // Filtrar los detalles de orden relacionados con el producto específico
        List<DetalleOrdenEntity> detallesOrdenParaProducto = detallesOrden.stream()
                .filter(detalle -> detalle.getProducto().equals(producto))
                .collect(Collectors.toList());

        // Agrupar por mes y sumar las cantidades
        return detallesOrdenParaProducto.stream()
                .collect(Collectors.groupingBy(
                        detalle -> obtenerMesDesdeFecha(detalle.getOrden().getFechaOrden()),
                        Collectors.mapping(DetalleOrdenEntity::getCantidad, Collectors.reducing(0, Integer::sum))
                ));
    }

    private String obtenerMesDesdeFecha(Date fecha) {
        // Obtener el mes desde la fecha (puedes ajustar el formato según tus necesidades)
        return new SimpleDateFormat("MM/yyyy").format(fecha);
    }

    private DatosGenerales generarDatosGeneralesProductosEspecificos(List<ProductoEntity> productosEspecificos) {
        // Obtener todas las órdenes relacionadas con los productos específicos
        List<OrdenEntity> ordenesRelacionadas = obtenerOrdenesRelacionadasConProductos(productosEspecificos);

        // Obtener los detalles de orden relacionados con las órdenes
        List<DetalleOrdenEntity> detallesOrdenRelacionados = detalleOrdenRepository.findByOrdenIn(ordenesRelacionadas);

        // Calcular total de ventas y ganancias solo para las órdenes relacionadas
        Integer totaleVentas = detallesOrdenRelacionados.stream()
                .map(DetalleOrdenEntity::getCantidad)
                .reduce(0, Integer::sum);

        BigDecimal totalVentas = BigDecimal.valueOf(totaleVentas);

        BigDecimal totalGanancias = ordenesRelacionadas.stream().map(orden -> orden.getDetallePago().getTotal()).reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular ventas por mes y ganancias por mes solo para las órdenes relacionadas
        Map<String, BigDecimal> ventasPorMes = calcularVentasPorMes(detallesOrdenRelacionados);
        Map<String, BigDecimal> gananciasPorMes = calcularGananciasPorMes(ordenesRelacionadas);

        // Calcular producto más vendido y más rentable solo para las órdenes relacionadas
        ProductoReporteDTO productoMasVendido = calcularProductoMasVendidoFiltradoPorProductos(ordenesRelacionadas);
        ProductoReporteDTO productoMasRentable = calcularProductoMasRentableFiltradoPorProductos(ordenesRelacionadas);

        DatosGenerales datosGenerales = new DatosGenerales();
        datosGenerales.setTotalVentas(totalVentas);
        datosGenerales.setTotalGanancias(totalGanancias);
        datosGenerales.setVentasPorMes(ventasPorMes);
        datosGenerales.setGananciasPorMes(gananciasPorMes);
        datosGenerales.setProductoMasVendido(productoMasVendido);
        datosGenerales.setProductoMasRentable(productoMasRentable);

        return datosGenerales;
    }

    private List<OrdenEntity> obtenerOrdenesRelacionadasConProductos(List<ProductoEntity> productosEspecificos) {
        // Obtener los IDs de los productos específicos
        List<Integer> idsProductosEspecificos = productosEspecificos.stream()
                .map(ProductoEntity::getIdProducto)
                .collect(Collectors.toList());

        // Obtener todas las órdenes que contienen los productos específicos
        List<DetalleOrdenEntity> detallesOrdenRelacionados = detalleOrdenRepository.findByProductoIn(productosEspecificos);
        List<OrdenEntity> ordenesRelacionadas = detallesOrdenRelacionados.stream()
                .map(DetalleOrdenEntity::getOrden)
                .distinct() // Eliminar duplicados
                .collect(Collectors.toList());

        return ordenesRelacionadas;
    }

    private ProductoReporteDTO calcularProductoMasVendidoFiltradoPorProductos(List<OrdenEntity> ordenesRelacionadas) {
        // Obtener todos los detalles de orden relacionados con las órdenes
        List<DetalleOrdenEntity> detallesOrdenRelacionados = detalleOrdenRepository.findByOrdenIn(ordenesRelacionadas);

        // Agrupar por producto y sumar las cantidades
        Map<Integer, Integer> ventasPorProducto = detallesOrdenRelacionados.stream()
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

    private ProductoReporteDTO calcularProductoMasRentableFiltradoPorProductos(List<OrdenEntity> ordenesRelacionadas) {
        // Obtener todos los detalles de orden relacionados con las órdenes
        List<DetalleOrdenEntity> detallesOrdenRelacionados = detalleOrdenRepository.findByOrdenIn(ordenesRelacionadas);

        // Agrupar por producto y sumar las ganancias y las cantidades vendidas
        Map<Integer, BigDecimal> gananciasPorProducto = new HashMap<>();
        Map<Integer, Integer> cantidadesVendidasPorProducto = new HashMap<>();

        detallesOrdenRelacionados.forEach(detalle -> {
            Integer idProducto = detalle.getProducto().getIdProducto();
            BigDecimal precioUnitario = detalle.getProducto().getPrecioItem();

            gananciasPorProducto.merge(idProducto, precioUnitario.multiply(BigDecimal.valueOf(detalle.getCantidad())), BigDecimal::add);
            cantidadesVendidasPorProducto.merge(idProducto, detalle.getCantidad(), Integer::sum);
        });

        // Encontrar el producto con más ganancias
        Map.Entry<Integer, BigDecimal> productoMasRentableEntry = gananciasPorProducto.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        if (productoMasRentableEntry != null) {
            Integer idProductoMasRentable = productoMasRentableEntry.getKey();
            BigDecimal gananciaTotal = productoMasRentableEntry.getValue();
            Integer cantidadVendida = cantidadesVendidasPorProducto.get(idProductoMasRentable);

            // Obtener información adicional del producto
            ProductoEntity productoMasRentableEntity = productoRepository.findById(idProductoMasRentable).orElse(null);

            if (productoMasRentableEntity != null) {
                String nombreProductoMasRentable = productoMasRentableEntity.getProducto().toString();
                BigDecimal precioUnitario = productoMasRentableEntity.getPrecioItem();

                // Obtener las ventas por mes del producto
                Map<String, Integer> ventasPorMesProducto = obtenerVentasPorMesProducto(productoMasRentableEntity);

                return new ProductoReporteDTO(nombreProductoMasRentable, cantidadVendida, gananciaTotal, precioUnitario, ventasPorMesProducto);
            }
        }

        return null;
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

        List<DetalleOrdenEntity> dOrdenesEnrango = new ArrayList<>();
        for (OrdenEntity orden : ordenesEnRango) {
            List<DetalleOrdenEntity> detallesOrdenDeLaOrden = detalleOrdenRepository.findAllByOrden(orden);
            dOrdenesEnrango.addAll(detallesOrdenDeLaOrden);
        }

        Integer totaleVentas = dOrdenesEnrango.stream()
                .map(DetalleOrdenEntity::getCantidad)
                .reduce(0, Integer::sum);

        BigDecimal totalVentas = BigDecimal.valueOf(totaleVentas);
        BigDecimal totalGanancias = ordenesEnRango.stream().map(orden -> orden.getDetallePago().getTotal()).reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular ventas por mes y ganancias por mes solo para las órdenes en el rango de fechas
        Map<String, BigDecimal> ventasPorMes = calcularVentasPorMes(dOrdenesEnrango);
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

        // Agrupar por producto y sumar las ganancias y las cantidades vendidas
        Map<Integer, BigDecimal> gananciasPorProducto = new HashMap<>();
        Map<Integer, Integer> cantidadesVendidasPorProducto = new HashMap<>();

        detallesOrdenEnRango.forEach(detalle -> {
            Integer idProducto = detalle.getProducto().getIdProducto();
            BigDecimal precioUnitario = detalle.getProducto().getPrecioItem();

            gananciasPorProducto.merge(idProducto, precioUnitario.multiply(BigDecimal.valueOf(detalle.getCantidad())), BigDecimal::add);
            cantidadesVendidasPorProducto.merge(idProducto, detalle.getCantidad(), Integer::sum);
        });

        // Encontrar el producto con más ganancias
        Map.Entry<Integer, BigDecimal> productoMasRentableEntry = gananciasPorProducto.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        if (productoMasRentableEntry != null) {
            Integer idProductoMasRentable = productoMasRentableEntry.getKey();
            BigDecimal gananciaTotal = productoMasRentableEntry.getValue();
            Integer cantidadVendida = cantidadesVendidasPorProducto.get(idProductoMasRentable);

            // Obtener información adicional del producto
            ProductoEntity productoMasRentableEntity = productoRepository.findById(idProductoMasRentable).orElse(null);

            if (productoMasRentableEntity != null) {
                String nombreProductoMasRentable = productoMasRentableEntity.getProducto().toString();
                BigDecimal precioUnitario = productoMasRentableEntity.getPrecioItem();

                // Obtener las ventas por mes del producto
                Map<String, Integer> ventasPorMesProducto = obtenerVentasPorMesProducto(productoMasRentableEntity);

                return new ProductoReporteDTO(nombreProductoMasRentable, cantidadVendida,gananciaTotal, precioUnitario , ventasPorMesProducto);
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
        List<DetalleOrdenEntity> detallesOrdenes = detalleOrdenRepository.findAll();

        // Calcular total de ventas y ganancias
        BigDecimal totalVentas = detallesOrdenes.stream()
                .map(detalle -> BigDecimal.valueOf(detalle.getCantidad()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalGanancias = ordenes.stream().map(OrdenEntity::getDetallePago)
                .map(DetallePagoEntity::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular ventas por mes y ganancias por mes
        Map<String, BigDecimal> ventasPorMes = calcularVentasPorMes(detallesOrdenes);
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

    private Map<String, BigDecimal> calcularVentasPorMes(List<DetalleOrdenEntity> detallesOrden) {
        Map<String, BigDecimal> ventasPorMes = new HashMap<>();

        for (DetalleOrdenEntity detalle : detallesOrden) {
            YearMonth yearMonth = YearMonth.from(detalle.getOrden().getFechaOrden().toLocalDate());

            BigDecimal totalVentas = ventasPorMes.getOrDefault(yearMonth.toString(), BigDecimal.ZERO);
            totalVentas = totalVentas.add(BigDecimal.valueOf(detalle.getCantidad()));

            ventasPorMes.put(yearMonth.toString(), totalVentas);
        }

        return ventasPorMes;
    }
    private Map<String, BigDecimal> calcularGananciasPorMesAD(List<DetalleOrdenEntity> detallesOrden, List<ProductoEntity> productosEspecificos) {
        Map<String, BigDecimal> gananciasPorMes = new HashMap<>();

        for (DetalleOrdenEntity detalle : detallesOrden) {
            YearMonth yearMonth = YearMonth.from(detalle.getOrden().getFechaOrden().toLocalDate());

            if (productosEspecificos.contains(detalle.getProducto())) {
                BigDecimal gananciasDetalle = detalle.getProducto().getPrecioItem().multiply(BigDecimal.valueOf(detalle.getCantidad()));

                BigDecimal gananciasMes = gananciasPorMes.getOrDefault(yearMonth.toString(), BigDecimal.ZERO);
                gananciasMes = gananciasMes.add(gananciasDetalle);

                gananciasPorMes.put(yearMonth.toString(), gananciasMes);
            }
        }

        return gananciasPorMes;
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

        // Agrupar por producto y sumar las ganancias y las cantidades vendidas
        Map<Integer, BigDecimal> gananciasPorProducto = new HashMap<>();
        Map<Integer, Integer> cantidadesVendidasPorProducto = new HashMap<>();

        detallesOrden.forEach(detalle -> {
            Integer idProducto = detalle.getProducto().getIdProducto();
            BigDecimal precioUnitario = detalle.getProducto().getPrecioItem();

            gananciasPorProducto.merge(idProducto, precioUnitario.multiply(BigDecimal.valueOf(detalle.getCantidad())), BigDecimal::add);
            cantidadesVendidasPorProducto.merge(idProducto, detalle.getCantidad(), Integer::sum);
        });

        // Encontrar el producto con más ganancias
        Map.Entry<Integer, BigDecimal> productoMasRentableEntry = gananciasPorProducto.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        if (productoMasRentableEntry != null) {
            Integer idProductoMasRentable = productoMasRentableEntry.getKey();
            BigDecimal gananciaTotal = productoMasRentableEntry.getValue();
            Integer cantidadVendida = cantidadesVendidasPorProducto.get(idProductoMasRentable);

            // Obtener información adicional del producto
            ProductoEntity productoMasRentableEntity = productoRepository.findById(idProductoMasRentable).orElse(null);

            if (productoMasRentableEntity != null) {
                String nombreProductoMasRentable = productoMasRentableEntity.getProducto().toString();
                BigDecimal precioUnitario = productoMasRentableEntity.getPrecioItem();

                // Obtener las ventas por mes del producto
                Map<String, Integer> ventasPorMesProducto = obtenerVentasPorMesProducto(productoMasRentableEntity);

                return new ProductoReporteDTO(nombreProductoMasRentable, cantidadVendida,gananciaTotal, precioUnitario, ventasPorMesProducto);
            }
        }

        return null;
    }
}
