package pe.LaCasona.backend_casona;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import pe.LaCasona.backend_casona.models.Auth.AplicationUser;
import pe.LaCasona.backend_casona.models.Auth.Role;
import pe.LaCasona.backend_casona.models.Entity.UsuarioEntity;
import pe.LaCasona.backend_casona.reposity.RoleRepository;
import pe.LaCasona.backend_casona.reposity.UserRepository;
import pe.LaCasona.backend_casona.reposity.UsuarioRepository;

@SpringBootApplication
public class BackendCasonaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendCasonaApplication.class, args);
	}

	@Bean
	CommandLineRunner run(RoleRepository roleRepository, UserRepository userRepository,
                          PasswordEncoder passwordEncoder, UsuarioRepository usuarioRepository) {
		return args -> {
			// Admin role
			if (roleRepository.findByAuthority("ADMIN").isPresent())
				return;

			Role adminRole = roleRepository.save(new Role("ADMIN"));

			Set<Role> adminRoles = new HashSet<>();
			adminRoles.add(adminRole);

			AplicationUser admin = new AplicationUser(1, "admin", passwordEncoder.encode("password"), adminRoles);
			Set<AplicationUser> adminUsers = new HashSet<>();
			UsuarioEntity adminUsuario = new UsuarioEntity(adminUsers);

			adminUsers.add(admin);

			userRepository.save(admin);
			usuarioRepository.save(adminUsuario);

			// User role
			if (roleRepository.findByAuthority("USER").isPresent())
				return;

			Role userRole = roleRepository.save(new Role("USER"));

			Set<Role> userRoles = new HashSet<>();
			userRoles.add(userRole);

			AplicationUser user = new AplicationUser(2, "user", passwordEncoder.encode("password"), userRoles);
			Set<AplicationUser> userUsers = new HashSet<>();
			UsuarioEntity userUsuario = new UsuarioEntity(userUsers);

			userUsers.add(user);

			userRepository.save(user);
			usuarioRepository.save(userUsuario);


			// Cashier role
			if (roleRepository.findByAuthority("CASHIER").isPresent())
				return;

			Role cashierRole = roleRepository.save(new Role("CASHIER"));

			Set<Role> cashierRoles = new HashSet<>();
			cashierRoles.add(cashierRole);

			AplicationUser cashier = new AplicationUser(3, "cashier", passwordEncoder.encode("password"), cashierRoles);
			Set<AplicationUser> cashierUsers = new HashSet<>();
			UsuarioEntity cashierUsuario = new UsuarioEntity(cashierUsers);

			cashierUsers.add(cashier);

			userRepository.save(cashier);
			usuarioRepository.save(cashierUsuario);

			// Chef role
			if (roleRepository.findByAuthority("CHEF").isPresent())
				return;

			Role chefRole = roleRepository.save(new Role("CHEF"));

			Set<Role> chefRoles = new HashSet<>();
			chefRoles.add(chefRole);

			AplicationUser chef = new AplicationUser(4, "chef", passwordEncoder.encode("password"), chefRoles);
			Set<AplicationUser> chefUsers = new HashSet<>();
			UsuarioEntity chefUsuario = new UsuarioEntity(chefUsers);

			chefUsers.add(chef);

			userRepository.save(chef);
			usuarioRepository.save(chefUsuario);


			// Delivery role
			if (roleRepository.findByAuthority("DELIVERY").isPresent())
				return;

			Role deliveryRole = roleRepository.save(new Role("DELIVERY"));

			Set<Role> deliveryRoles = new HashSet<>();
			deliveryRoles.add(deliveryRole);

			AplicationUser delivery = new AplicationUser(5, "delivery", passwordEncoder.encode("password"), deliveryRoles);
			Set<AplicationUser> deliveryUsers = new HashSet<>();
			UsuarioEntity deliveryUsuario = new UsuarioEntity(deliveryUsers);

			deliveryUsers.add(delivery);

			userRepository.save(delivery);
			usuarioRepository.save(deliveryUsuario);


			// Waiter role
			if (roleRepository.findByAuthority("WAITER").isPresent())
				return;

			Role waiterRole = roleRepository.save(new Role("WAITER"));

			Set<Role> waiterRoles = new HashSet<>();
			waiterRoles.add(waiterRole);

			AplicationUser waiter = new AplicationUser(6, "waiter", passwordEncoder.encode("password"), waiterRoles);
			Set<AplicationUser> waiterUsers = new HashSet<>();
			UsuarioEntity waiterUsuario = new UsuarioEntity(waiterUsers);

			waiterUsers.add(waiter);

			userRepository.save(waiter);
			usuarioRepository.save(waiterUsuario);


			// Counter role
			if (roleRepository.findByAuthority("COUNTER").isPresent())
				return;

			Role counterRole = roleRepository.save(new Role("COUNTER"));

			Set<Role> counterRoles = new HashSet<>();
			counterRoles.add(counterRole);

			AplicationUser counter = new AplicationUser(7, "counter", passwordEncoder.encode("password"), counterRoles);
			Set<AplicationUser> counterUsers = new HashSet<>();
			UsuarioEntity counterUsuario = new UsuarioEntity(counterUsers);

			counterUsers.add(counter);

			userRepository.save(counter);
			usuarioRepository.save(counterUsuario);

		};
	}
}
