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

            if (order.getDireccion() == null){
                orden.setDelivery(false);

                CamareroEntity camarero = camareroRepository.findByIdCamarero(order.getIdRegistrador());
                if (camarero != null) {
                    orden.setCamarero(camarero);
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

        }catch (Exception e){
            Log.logError("Error" + e.getMessage());
            pedidoResponseDTO.setStatus(false);
        };

        return pedidoResponseDTO;
    }
}
