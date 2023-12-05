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
import pe.LaCasona.backend_casona.reposity.RoleRepository;
import pe.LaCasona.backend_casona.reposity.UserRepository;

@SpringBootApplication
public class BackendCasonaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendCasonaApplication.class, args);
	}

	@Bean
	CommandLineRunner run(RoleRepository roleRepository, UserRepository userRepository,
			PasswordEncoder passwordEncoder) {
		return args -> {
			if (roleRepository.findByAuthority("ADMIN").isPresent())
				return;

			Role adminRole = roleRepository.save(new Role("ADMIN"));

			if (roleRepository.findByAuthority("USER").isPresent())
				return;

			roleRepository.save(new Role("USER"));

			if (roleRepository.findByAuthority("CASHIER").isPresent())
				return;

			roleRepository.save(new Role("CASHIER"));

			if (roleRepository.findByAuthority("CHEF").isPresent())
				return;

			roleRepository.save(new Role("CHEF"));

			if (roleRepository.findByAuthority("DELIVERY").isPresent())
				return;

			roleRepository.save(new Role("DELIVERY"));

			if (roleRepository.findByAuthority("WAITER").isPresent())
				return;

			roleRepository.save(new Role("WAITER"));

			if (roleRepository.findByAuthority("COUNTER").isPresent())
				return;

			roleRepository.save(new Role("COUNTER"));

			Set<Role> roles = new HashSet<>();
			roles.add(adminRole);

			AplicationUser admin = new AplicationUser(1, "admin", passwordEncoder.encode("password"), roles);

			userRepository.save(admin);
		};
	}
}
