package pe.LaCasona.backend_casona;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import pe.LaCasona.backend_casona.models.Auth.AplicationUser;
import pe.LaCasona.backend_casona.models.Auth.Role;
import pe.LaCasona.backend_casona.models.DTO.MetodoPago;
import pe.LaCasona.backend_casona.models.DTO.Producto;
import pe.LaCasona.backend_casona.models.Entity.*;
import pe.LaCasona.backend_casona.reposity.*;

@SpringBootApplication
public class BackendCasonaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendCasonaApplication.class, args);
	}

	@Bean
	CommandLineRunner run(RoleRepository roleRepository, UserRepository userRepository,
						  PasswordEncoder passwordEncoder, UsuarioRepository usuarioRepository,
						  MetodoPagoRepository metodoPagoRepository, ClienteRepository clienteRepository,
						  CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
		return args -> {
			// Admin role
			if (!roleRepository.findByAuthority("ADMIN").isEmpty())
				return;

			Role adminRole = roleRepository.save(new Role("ADMIN"));

			Set<Role> adminRoles = new HashSet<>();
			adminRoles.add(adminRole);

			AplicationUser admin = new AplicationUser(1, "admin", passwordEncoder.encode("password"), adminRoles);
			Set<AplicationUser> adminUsers = new HashSet<>();
			UsuarioEntity adminUsuario = new UsuarioEntity(adminUsers);

			adminUsers.add(admin);
			adminUsuario.setEmail("admin@gmail.com");

			userRepository.save(admin);
			usuarioRepository.save(adminUsuario);

			// User role
			if (!roleRepository.findByAuthority("USER").isEmpty())
				return;

			Role userRole = roleRepository.save(new Role("USER"));

			Set<Role> userRoles = new HashSet<>();
			userRoles.add(userRole);

			AplicationUser user = new AplicationUser(2, "user", passwordEncoder.encode("password"), userRoles);
			Set<AplicationUser> userUsers = new HashSet<>();
			UsuarioEntity userUsuario = new UsuarioEntity(userUsers);

			userUsers.add(user);
			userUsuario.setEmail("user@gmail.com");

			userRepository.save(user);
			usuarioRepository.save(userUsuario);


			// Cashier role
			if (!roleRepository.findByAuthority("CASHIER").isEmpty())
				return;

			Role cashierRole = roleRepository.save(new Role("CASHIER"));

			Set<Role> cashierRoles = new HashSet<>();
			cashierRoles.add(cashierRole);

			AplicationUser cashier = new AplicationUser(3, "cashier", passwordEncoder.encode("password"), cashierRoles);
			Set<AplicationUser> cashierUsers = new HashSet<>();
			UsuarioEntity cashierUsuario = new UsuarioEntity(cashierUsers);

			cashierUsers.add(cashier);
			cashierUsuario.setEmail("cashier@gmail.com");

			userRepository.save(cashier);
			usuarioRepository.save(cashierUsuario);

			// Chef role
			if (!roleRepository.findByAuthority("CHEF").isEmpty())
				return;

			Role chefRole = roleRepository.save(new Role("CHEF"));

			Set<Role> chefRoles = new HashSet<>();
			chefRoles.add(chefRole);

			AplicationUser chef = new AplicationUser(4, "chef", passwordEncoder.encode("password"), chefRoles);
			Set<AplicationUser> chefUsers = new HashSet<>();
			UsuarioEntity chefUsuario = new UsuarioEntity(chefUsers);

			chefUsers.add(chef);
			chefUsuario.setEmail("chef@gmail.com");

			userRepository.save(chef);
			usuarioRepository.save(chefUsuario);


			// Delivery role
			if (!roleRepository.findByAuthority("DELIVERY").isEmpty())
				return;

			Role deliveryRole = roleRepository.save(new Role("DELIVERY"));

			Set<Role> deliveryRoles = new HashSet<>();
			deliveryRoles.add(deliveryRole);

			AplicationUser delivery = new AplicationUser(5, "delivery", passwordEncoder.encode("password"), deliveryRoles);
			Set<AplicationUser> deliveryUsers = new HashSet<>();
			UsuarioEntity deliveryUsuario = new UsuarioEntity(deliveryUsers);

			deliveryUsers.add(delivery);
			deliveryUsuario.setEmail("delivery@gmail.com");

			userRepository.save(delivery);
			usuarioRepository.save(deliveryUsuario);


			// Waiter role
			if (!roleRepository.findByAuthority("WAITER").isEmpty())
				return;

			Role waiterRole = roleRepository.save(new Role("WAITER"));

			Set<Role> waiterRoles = new HashSet<>();
			waiterRoles.add(waiterRole);

			AplicationUser waiter = new AplicationUser(6, "waiter", passwordEncoder.encode("password"), waiterRoles);
			Set<AplicationUser> waiterUsers = new HashSet<>();
			UsuarioEntity waiterUsuario = new UsuarioEntity(waiterUsers);

			waiterUsers.add(waiter);
			waiterUsuario.setEmail("waiter@gmail.com");

			userRepository.save(waiter);
			usuarioRepository.save(waiterUsuario);

			// Counter role
			if (!roleRepository.findByAuthority("COUNTER").isEmpty())
				return;

			Role counterRole = roleRepository.save(new Role("COUNTER"));

			Set<Role> counterRoles = new HashSet<>();
			counterRoles.add(counterRole);

			AplicationUser counter = new AplicationUser(7, "counter", passwordEncoder.encode("password"), counterRoles);
			Set<AplicationUser> counterUsers = new HashSet<>();
			UsuarioEntity counterUsuario = new UsuarioEntity(counterUsers);

			counterUsers.add(counter);
			counterUsuario.setEmail("counter@gmail.com");

			userRepository.save(counter);
			usuarioRepository.save(counterUsuario);

			MetodoPagoEntity metodoPago = new MetodoPagoEntity();
			metodoPago.setNombre(MetodoPago.YAPE);
			metodoPago.setDescripcion("Realice el pago utilizando la aplicación YAPE. Ingrese el número de teléfono asociado a su cuenta YAPE y confirme la transacción.");

			metodoPagoRepository.save(metodoPago);

			MetodoPagoEntity metodoPago1 = new MetodoPagoEntity();
			metodoPago1.setNombre(MetodoPago.PLIN);
			metodoPago1.setDescripcion("Realice el pago utilizando la aplicación PLIN. Escanee el código QR proporcionado o ingrese el código de referencia en la aplicación.");

			metodoPagoRepository.save(metodoPago1);

			MetodoPagoEntity metodoPago2 = new MetodoPagoEntity();
			metodoPago2.setNombre(MetodoPago.TARJETA);
			metodoPago2.setDescripcion("Tarjeta de Crédito', 'Pago mediante tarjeta de crédito. Proporcione el número de tarjeta, la fecha de vencimiento y el código de seguridad.");

			metodoPagoRepository.save(metodoPago2);

			MetodoPagoEntity metodoPago3 = new MetodoPagoEntity();
			metodoPago3.setNombre(MetodoPago.EFECTIVO);
			metodoPago3.setDescripcion("Pago en efectivo en el momento de la entrega o consumo.");

			metodoPagoRepository.save(metodoPago3);

			ClienteEntity clienteAnonimo = new ClienteEntity();

			clienteAnonimo.setPrimerNombre("Anónimo");
			clienteAnonimo.setApellido("Anónimo");
			clienteAnonimo.setTelefono(0);
			clienteAnonimo.setDireccion("Dirección Anónima");
			clienteAnonimo.setNumeroCompra(0);
			clienteAnonimo.setNumeroRUC("RUC Anónimo");
			clienteAnonimo.setUsuario(userUsuario);

			clienteRepository.save(clienteAnonimo);

			CategoriaEntity categoria = new CategoriaEntity();
			categoria.setNombreCategoria("HAMBURGUESAS");
			categoriaRepository.save(categoria);

			CategoriaEntity categoria2 = new CategoriaEntity();
			categoria2.setNombreCategoria("BEBIDAS");
			categoriaRepository.save(categoria2);

			CategoriaEntity categoria3 = new CategoriaEntity();
			categoria3.setNombreCategoria("PLATOS");
			categoriaRepository.save(categoria3);

			CategoriaEntity categoria4 = new CategoriaEntity();
			categoria4.setNombreCategoria("AGREGADOS");
			categoriaRepository.save(categoria4);

			ProductoEntity producto = new ProductoEntity();
			producto.setCategoria(categoria);
			producto.setProducto(Producto.SIMPLE_DE_POLLO);
			producto.setPrecioItem(new BigDecimal(5));
			productoRepository.save(producto);

			ProductoEntity producto2 = new ProductoEntity();
			producto2.setCategoria(categoria);
			producto2.setProducto(Producto.CHESSE);
			producto2.setPrecioItem(new BigDecimal(6));
			productoRepository.save(producto2);

			ProductoEntity producto3 = new ProductoEntity();
			producto3.setCategoria(categoria);
			producto3.setProducto(Producto.ROYAL);
			producto3.setPrecioItem(new BigDecimal(6));
			productoRepository.save(producto);

			ProductoEntity producto4 = new ProductoEntity();
			producto4.setCategoria(categoria);
			producto4.setProducto(Producto.ESPECIAL);
			producto4.setPrecioItem(new BigDecimal(7));
			productoRepository.save(producto4);

			ProductoEntity producto5 = new ProductoEntity();
			producto5.setCategoria(categoria);
			producto5.setProducto(Producto.MIXTA);
			producto5.setPrecioItem(new BigDecimal(7));
			productoRepository.save(producto5);

			ProductoEntity producto6 = new ProductoEntity();
			producto6.setCategoria(categoria);
			producto6.setProducto(Producto.CASONA);
			producto6.setPrecioItem(new BigDecimal(10));
			productoRepository.save(producto6);

			ProductoEntity producto7 = new ProductoEntity();
			producto7.setCategoria(categoria);
			producto7.setProducto(Producto.CHORIPAN);
			producto7.setPrecioItem(new BigDecimal(5.5));
			productoRepository.save(producto7);

			ProductoEntity producto8 = new ProductoEntity();
			producto8.setCategoria(categoria2);
			producto8.setProducto(Producto.CAFE);
			producto8.setPrecioItem(new BigDecimal(2.5));
			productoRepository.save(producto8);

			ProductoEntity producto9 = new ProductoEntity();
			producto9.setCategoria(categoria2);
			producto9.setProducto(Producto.INFUSIONES);
			producto9.setPrecioItem(new BigDecimal(2));
			productoRepository.save(producto9);

			ProductoEntity producto10 = new ProductoEntity();
			producto10.setCategoria(categoria2);
			producto10.setProducto(Producto.CHICHA_MORADA);
			producto10.setPrecioItem(new BigDecimal(2));
			productoRepository.save(producto10);

			ProductoEntity producto11 = new ProductoEntity();
			producto11.setCategoria(categoria2);
			producto11.setProducto(Producto.JARRA_CHICHA_MORADA);
			producto11.setPrecioItem(new BigDecimal(8));
			productoRepository.save(producto11);

			ProductoEntity producto12 = new ProductoEntity();
			producto12.setCategoria(categoria2);
			producto12.setProducto(Producto.INKA_COLA);
			producto12.setPrecioItem(new BigDecimal(3));
			productoRepository.save(producto12);

			ProductoEntity producto13 = new ProductoEntity();
			producto13.setCategoria(categoria2);
			producto13.setProducto(Producto.COCA_COLA);
			producto13.setPrecioItem(new BigDecimal(3));
			productoRepository.save(producto13);

			ProductoEntity producto14 = new ProductoEntity();
			producto14.setCategoria(categoria2);
			producto14.setProducto(Producto.GUARANA);
			producto14.setPrecioItem(new BigDecimal(2));
			productoRepository.save(producto14);

			ProductoEntity producto15 = new ProductoEntity();
			producto15.setCategoria(categoria2);
			producto15.setProducto(Producto.AGUA_MINERAL);
			producto15.setPrecioItem(new BigDecimal(2));
			productoRepository.save(producto15);

			ProductoEntity producto16 = new ProductoEntity();
			producto16.setCategoria(categoria3);
			producto16.setProducto(Producto.SALCHIPAPA);
			producto16.setPrecioItem(new BigDecimal(2.5));
			productoRepository.save(producto16);

			ProductoEntity producto17 = new ProductoEntity();
			producto17.setCategoria(categoria3);
			producto17.setProducto(Producto.SALCHIPOLLO);
			producto17.setPrecioItem(new BigDecimal(2));
			productoRepository.save(producto17);

			ProductoEntity producto18 = new ProductoEntity();
			producto18.setCategoria(categoria4);
			producto18.setProducto(Producto.CHORIZO);
			producto18.setPrecioItem(new BigDecimal(2));
			productoRepository.save(producto18);

			ProductoEntity producto19 = new ProductoEntity();
			producto19.setCategoria(categoria4);
			producto19.setProducto(Producto.HOTDOG);
			producto19.setPrecioItem(new BigDecimal(1));
			productoRepository.save(producto19);

			ProductoEntity producto20 = new ProductoEntity();
			producto20.setCategoria(categoria4);
			producto20.setProducto(Producto.JAMON);
			producto20.setPrecioItem(new BigDecimal(1));
			productoRepository.save(producto20);

			ProductoEntity producto21 = new ProductoEntity();
			producto21.setCategoria(categoria4);
			producto21.setProducto(Producto.QUESO);
			producto21.setPrecioItem(new BigDecimal(1));
			productoRepository.save(producto21);

			ProductoEntity producto22 = new ProductoEntity();
			producto22.setCategoria(categoria4);
			producto22.setProducto(Producto.QUESO);
			producto22.setPrecioItem(new BigDecimal(1));
			productoRepository.save(producto22);

			ProductoEntity producto23 = new ProductoEntity();
			producto23.setCategoria(categoria4);
			producto23.setProducto(Producto.QUESO);
			producto23.setPrecioItem(new BigDecimal(1));
			productoRepository.save(producto23);
		};
	}
}
