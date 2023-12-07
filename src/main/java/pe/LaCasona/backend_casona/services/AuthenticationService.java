package pe.LaCasona.backend_casona.services;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.LaCasona.backend_casona.utils.Log;
import pe.LaCasona.backend_casona.models.Auth.AplicationUser;
import pe.LaCasona.backend_casona.models.DTO.LoginResponseDTO;
import pe.LaCasona.backend_casona.models.Auth.Role;
import pe.LaCasona.backend_casona.models.DTO.RegisterResponseDTO;
import pe.LaCasona.backend_casona.models.Entity.UsuarioEntity;
import pe.LaCasona.backend_casona.reposity.RoleRepository;
import pe.LaCasona.backend_casona.reposity.UserRepository;
import pe.LaCasona.backend_casona.reposity.UsuarioRepository;

@Service
@Transactional
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TokenService tokenService;
    public RegisterResponseDTO registerUser(String username, String password, String email) {
        RegisterResponseDTO response = new RegisterResponseDTO(true);

        if (userRepository.findByUsername(username).isPresent()) {
            Log.logError("El nombre de usuario ya está en uso");
            response.setStatus(false);
            return response;
        }

        if (usuarioRepository.findByEmail(email).isPresent()) {
            Log.logError("El correo ya está en uso");
            response.setStatus(false);
            return response;
        }

        String encodedPassword = passwordEncoder.encode(password);

        // Obtener el primer elemento del conjunto de roles
        Role userRole = roleRepository.findByAuthority("USER").stream()
                .findFirst()
                .orElse(null);

        if (userRole == null) {
            // Manejar el caso en que el conjunto de roles esté vacío
            Log.logError("No se encontró el rol 'USER'");
            response.setStatus(false);
            return response;
        }

        Set<Role> authorities = new HashSet<>();
        authorities.add(userRole);

        AplicationUser newUser = new AplicationUser(0, username, encodedPassword, authorities);
        Set<AplicationUser> users = new HashSet<>();
        UsuarioEntity newUsuario = new UsuarioEntity(users);

        newUsuario.setEmail(email);

        users.add(newUser);

        userRepository.save(newUser);
        usuarioRepository.save(newUsuario);

        return response;
    }
    public LoginResponseDTO loginUser(String username, String password) {

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            String token = tokenService.generateJwt(auth);

            return new LoginResponseDTO(userRepository.findByUsername(username).get(), token);
        } catch (BadCredentialsException e) {
            Log.logError("Invalid username or password" + e);
            return new LoginResponseDTO(null, "");
        } catch (LockedException e) {
            Log.logError("Account locked" + e);
            return new LoginResponseDTO(null, "");
        } catch (DisabledException e) {
            Log.logError("Account disabled" + e);
            return new LoginResponseDTO(null, "");
        } catch (AccountExpiredException e) {
            Log.logError("Account expired" + e);
            return new LoginResponseDTO(null, "");
        } catch (CredentialsExpiredException e) {
            Log.logError("Credentials expired" + e);
            return new LoginResponseDTO(null, "");
        } catch (AuthenticationException e) {
            Log.logError("Authentication failed" + e);
            return new LoginResponseDTO(null, "");
        }
    }
}
