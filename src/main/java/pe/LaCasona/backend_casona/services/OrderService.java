package pe.LaCasona.backend_casona.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.LaCasona.backend_casona.models.Auth.AplicationUser;
import pe.LaCasona.backend_casona.models.Auth.Role;
import pe.LaCasona.backend_casona.models.DTO.*;
import pe.LaCasona.backend_casona.models.Entity.*;
import pe.LaCasona.backend_casona.reposity.*;
import pe.LaCasona.backend_casona.utils.Log;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {
    @Autowired
    private OrdenRepository ordenRepository;
    @Autowired
    private DetalleOrdenRepository detalleOrdenRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private MetodoPagoRepository metodoPagoRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private CamareroRepository camareroRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DetallePagoRepository detallePagoRepository;
    @Autowired
    private InformationDeliveryRepository informationDeliveryRepository;
    public CEResponseDTO updateStatus(int id, CambioStatusDTO updateStatus){
        CEResponseDTO ceResponseDTO = new CEResponseDTO();

        OrdenEntity ordenActualizada = ordenRepository.findByIdOrden(id);
        ceResponseDTO.setStatus(false);

        if (updateStatus.getStatus().equals("EN_PREPARACION")||updateStatus.getStatus().equals("LISTO")){
            ordenActualizada.setEstado(updateStatus.getStatus());
            ceResponseDTO.setStatus(true);
            ordenRepository.save(ordenActualizada);
        }

        return ceResponseDTO;
    }
    public PedidoResponseDTO registerOrder(PedidoDTO order) {

        PedidoResponseDTO pedidoResponseDTO = new PedidoResponseDTO(true);

        try {

            OrdenEntity orden = new OrdenEntity();

            Date utilDate = new Date(System.currentTimeMillis());
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

            MetodoPagoEntity metodoPago = metodoPagoRepository.findByNombre(order.getMetodoPago());

            if (order.getNombre().equals("ANONIMO")){
                orden.setCliente(clienteRepository.findByIdCliente(1));
            }else {
                UsuarioEntity usuario = usuarioRepository.findByEmail(order.getCorreo()).orElse(null);
                ClienteEntity cliente = clienteRepository.findByUsuario(usuario);

                if (cliente != null) {
                    orden.setCliente(cliente);
                } else {
                    ClienteEntity clienteAnonimo = new ClienteEntity();

                    clienteAnonimo.setPrimerNombre(order.getNombre());
                    clienteAnonimo.setApellido(order.getApellidos());
                    clienteAnonimo.setTelefono(Integer.valueOf(order.getTelefono()));
                    if (order.getDireccion() != null){
                        clienteAnonimo.setDireccion(order.getDireccion());
                    }

                    if (clienteAnonimo.getNumeroCompra() == null){
                        clienteAnonimo.setNumeroCompra(0);
                    }

                    clienteAnonimo.setNumeroCompra(clienteAnonimo.getNumeroCompra()+1);
                    clienteAnonimo.setNumeroRUC(order.getRuc());

                    Set<Role> userRoles = roleRepository.findByAuthority("USER");

                    AplicationUser user = new AplicationUser(1, order.getNombre()+order.getIdRegistrador()+order.getApellidos(), passwordEncoder.encode("password"), userRoles);
                    Set<AplicationUser> userUsers = new HashSet<>();
                    UsuarioEntity userUsuario = new UsuarioEntity(userUsers);

                    userUsers.add(user);
                    userUsuario.setEmail(order.getCorreo());

                    userRepository.save(user);
                    usuarioRepository.save(userUsuario);

                    clienteAnonimo.setUsuario(userUsuario);

                    clienteRepository.save(clienteAnonimo);
                    orden.setCliente(clienteAnonimo);
                }
            }

            orden.setFechaOrden(sqlDate);
            orden.setMetodoPago(metodoPago);

            CamareroEntity camarero = camareroRepository.findByIdCamarero(order.getIdRegistrador());
            if (camarero != null) {
                orden.setCamarero(camarero);

            }else {
                if (order.getDireccion() == null){
                    orden.setDelivery(false);
                    orden.setCamarero(camareroRepository.findByIdCamarero(2));
                }else {
                    orden.setDelivery(true);
                    orden.setCamarero(camareroRepository.findByIdCamarero(3));
                }
            }

            orden.setEstado("PENDIENTE");

            List<PedidoDTO.ItemPedido> items = order.getItems();

            BigDecimal montoTotal = new BigDecimal(0);

            ordenRepository.save(orden);

            for (PedidoDTO.ItemPedido item : items) {
                Producto producto = item.getProducto();
                ProductoEntity productoEntity =  productoRepository.findByProducto(producto);

                int cantidad = item.getCantidad();
                BigDecimal precio = productoEntity.getPrecioItem();
                BigDecimal montoTotalPorProducto = precio.multiply(BigDecimal.valueOf(cantidad));

                montoTotal = montoTotal.add(montoTotalPorProducto);

                DetalleOrdenEntity detalleOrden = new DetalleOrdenEntity();
                detalleOrden.setCantidad(cantidad);
                detalleOrden.setOrden(orden); // Asocia el detalle con la orden
                detalleOrden.setProducto(productoEntity);

                detalleOrdenRepository.save(detalleOrden);
            }

            DetallePagoEntity detallePago = new DetallePagoEntity();
            Date fechaEmision = new Date(System.currentTimeMillis());
            long unDiaEnMillis = 24 * 60 * 60 * 1000L; // 24 horas * 60 minutos * 60 segundos * 1000 milisegundos
            Date fechaVencimiento = new Date(fechaEmision.getTime() + unDiaEnMillis);

            detallePago.setIGV(montoTotal.multiply(new BigDecimal("0.18"))); // Multiplica por 0.18 utilizando BigDecimal
            detallePago.setFechaDeEmision(fechaEmision);
            detallePago.setFechaDeVencimiento(fechaVencimiento);
            detallePago.setEstadoFactura("PENDIENTE");
            detallePago.setPagoAPlazo(false);
            detallePago.setTotal(montoTotal);
            detallePago.setMetodoPago(orden.getMetodoPago());

            orden.setMontoTotal(montoTotal);
            orden.setDetallePago(detallePago);

            ordenRepository.save(orden);
            detallePagoRepository.save(detallePago);

            InformacionDeliveryEntity delivery = new InformacionDeliveryEntity();
            delivery.setOrden(orden);
            delivery.setEstadoDelivery("EN_ESPERA");
            delivery.setDireccionDelivery(order.getDireccion());

            informationDeliveryRepository.save(delivery);

        }catch (Exception e){
            Log.logError("Error" + e.getMessage());
            pedidoResponseDTO.setStatus(false);
        };

        return pedidoResponseDTO;
    }

    public List<PedidosDTO> getAllOrders() {
        List<OrdenEntity> ordenEntities = ordenRepository.findAll();

        return ordenEntities.stream()
                .map(this::mapToPedidosDTO)
                .collect(Collectors.toList());
    }
    public List<OrdenResponseDTO> getAllOrdersForDay() {
        Date today = new Date(System.currentTimeMillis());
        Date tomorrow = new Date(today.getTime() + 24 * 60 * 60 * 1000L); // Añade 24 horas para incluir todo el día

        List<OrdenEntity> ordenEntities = ordenRepository.findByFechaOrdenBetween(today, tomorrow);

        return ordenEntities.stream()
                .map(this::mapToOrdersDTO)
                .collect(Collectors.toList());
    }

    private OrdenResponseDTO mapToOrdersDTO(OrdenEntity ordenEntity) {
        OrdenResponseDTO orden = new OrdenResponseDTO();

        orden.setStatus(ordenEntity.getEstado());
        orden.setId(ordenEntity.getIdOrden());
        orden.setMonto_total(ordenEntity.getMontoTotal());

        InformacionDeliveryEntity infoDelivery = informationDeliveryRepository.findByOrden(ordenEntity);
        List<DetalleOrdenEntity> Ordenes = detalleOrdenRepository.findByOrden(ordenEntity);

        // Mapea los elementos de ItemPedido
        List<PedidoDTO.ItemPedido> itemPedidos = Ordenes.stream()
                .map(detalleOrdenEntity -> new PedidoDTO.ItemPedido(
                        detalleOrdenEntity.getProducto().getProducto(),
                        detalleOrdenEntity.getCantidad()))
                .collect(Collectors.toList());

        orden.setItems(itemPedidos);

        return orden;
    }

    private PedidosDTO mapToPedidosDTO(OrdenEntity ordenEntity) {
        PedidosDTO pedidoDTO = new PedidosDTO();

        // Asigna los valores de la entidad a DTO
        pedidoDTO.setNombre(ordenEntity.getCliente().getPrimerNombre());
        pedidoDTO.setApellidos(ordenEntity.getCliente().getApellido());

        InformacionDeliveryEntity infoDelivery = informationDeliveryRepository.findByOrden(ordenEntity);

        pedidoDTO.setDireccion(infoDelivery.getDireccionDelivery());
        pedidoDTO.setTelefono(String.valueOf(ordenEntity.getCliente().getTelefono()));
        pedidoDTO.setCorreo(ordenEntity.getCliente().getUsuario().getEmail());
        pedidoDTO.setMetodoPago(ordenEntity.getMetodoPago().getNombre());
        pedidoDTO.setNameUser(ordenEntity.getCamarero().getPrimerNombre());
        pedidoDTO.setApellidoUser(ordenEntity.getCamarero().getApellido());
        pedidoDTO.setRuc(ordenEntity.getCliente().getNumeroRUC());
        pedidoDTO.setNameCliente(ordenEntity.getCliente().getPrimerNombre());
        pedidoDTO.setApellidoCliente(ordenEntity.getCliente().getApellido());

        List<DetalleOrdenEntity> Ordenes = detalleOrdenRepository.findByOrden(ordenEntity);

        // Mapea los elementos de ItemPedido
        List<PedidoDTO.ItemPedido> itemPedidos = Ordenes.stream()
                .map(detalleOrdenEntity -> new PedidoDTO.ItemPedido(
                        detalleOrdenEntity.getProducto().getProducto(),
                        detalleOrdenEntity.getCantidad()))
                .collect(Collectors.toList());

        pedidoDTO.setItems(itemPedidos);

        return pedidoDTO;
    }
}
