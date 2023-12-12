package pe.LaCasona.backend_casona.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import pe.LaCasona.backend_casona.models.Auth.AplicationUser;
import pe.LaCasona.backend_casona.models.DTO.UserAdmDTO;
import pe.LaCasona.backend_casona.models.Entity.CamareroEntity;
import pe.LaCasona.backend_casona.models.Entity.UsuarioEntity;
import pe.LaCasona.backend_casona.reposity.CamareroRepository;
import pe.LaCasona.backend_casona.reposity.RoleRepository;
import pe.LaCasona.backend_casona.reposity.UserRepository;
import pe.LaCasona.backend_casona.reposity.UsuarioRepository;
import pe.LaCasona.backend_casona.utils.Log;

import java.util.*;

@Service
public class UserService implements UserDetailsService {
    @Autowired private PasswordEncoder encoder;
    @Autowired private UserRepository userRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private CamareroRepository camareroRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("user is not valid"));
    }
    @Transactional
    public void deleteUser(String id) {
        try {
            UsuarioEntity userDelete = usuarioRepository.findByIdUsuario(Integer.valueOf(id));
            List<CamareroEntity> camarerosToDelete = camareroRepository.findByUsuario(userDelete);

            for (CamareroEntity camarero : camarerosToDelete) {
                camareroRepository.delete(camarero);
            }

            userRepository.deleteById(Integer.valueOf(id));
            usuarioRepository.deleteById(Integer.valueOf(id));
        } catch (Exception e) {
            Log.logError("Error al eliminar el usuario con ID " + id);
        }
    }

    public List<UserAdmDTO> getAllUsers() {
        List<UsuarioEntity> usuarios = usuarioRepository.findAll();
        List<UserAdmDTO> userDTOs = new ArrayList<>();

        for (UsuarioEntity usuario : usuarios) {
            AplicationUser aplicationUser = usuario.getUsuarios().iterator().next();

            UserAdmDTO userDTO = new UserAdmDTO(
                    usuario.getIdUsuario(),
                    aplicationUser.getUsername(),
                    usuario.getEmail(),
                    aplicationUser.getPassword(),
                    aplicationUser.getAuthorities().toString()
            );
            userDTOs.add(userDTO);
        }

        return userDTOs;
    }

    public UserAdmDTO save(UserAdmDTO user) {
        // Crear un nuevo UsuarioEntity
        UsuarioEntity nuevoUsuario = new UsuarioEntity();

        // Crear un nuevo AplicationUser y asignarle los atributos del DTO
        AplicationUser nuevoAplicationUser = new AplicationUser();
        nuevoAplicationUser.setUsername(user.getUsername());
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        nuevoAplicationUser.setPassword(encodedPassword);
        nuevoAplicationUser.setAuthorities(roleRepository.findByAuthority(user.getRole()));

        nuevoUsuario.setEmail(user.getEmail());

        // Agregar el nuevo AplicationUser al conjunto de usuarios del UsuarioEntity
        Set<AplicationUser> usuarios = new HashSet<>();
        usuarios.add(nuevoAplicationUser);
        nuevoUsuario.setUsuarios(usuarios);

        userRepository.save(nuevoAplicationUser);

        // Guardar el nuevo usuario en la base de datos
        UsuarioEntity usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        // Crear instancia de CamareroEntity dependiendo del tipo de usuario
        switch (user.getRole()) {
            case "WAITER":
                CamareroEntity waiterCamarero = new CamareroEntity(
                        user.getUsername() + "_Waiter",
                        user.getUsername() + "_Waiter",
                        usuarioGuardado
                );
                camareroRepository.save(waiterCamarero);
                break;
            case "ADMIN":
                CamareroEntity adminCamarero = new CamareroEntity(
                        user.getUsername() + "_Admin",
                        user.getUsername() + "_Admin",
                        usuarioGuardado
                );
                camareroRepository.save(adminCamarero);
                break;
            case "DELIVERY":
                CamareroEntity deliveryCamarero = new CamareroEntity(
                        user.getUsername() + "_Delivery",
                        user.getUsername() + "_Delivery",
                        usuarioGuardado
                );
                camareroRepository.save(deliveryCamarero);
                break;
            default:
                // No hacer nada por defecto
                break;
        }

        // Devolver el UserAdmDTO correspondiente al usuario recién guardado
        return new UserAdmDTO(
                usuarioGuardado.getIdUsuario(),
                user.getUsername(),
                user.getEmail(),
                encodedPassword,
                user.getRole()
        );
    }

    public UserAdmDTO updateUser(String id, UserAdmDTO updatedUser) {
        // Obtener el UsuarioEntity existente por ID
        Integer userId = Integer.parseInt(id);
        Optional<UsuarioEntity> optionalUsuario = usuarioRepository.findById(userId);

        if (optionalUsuario.isPresent()) {
            UsuarioEntity usuarioExistente = optionalUsuario.get();

            // Actualizar el AplicationUser existente
            AplicationUser aplicationUserExistente = usuarioExistente.getUsuarios().iterator().next();
            aplicationUserExistente.setUsername(updatedUser.getUsername());
            String encodedPassword = passwordEncoder.encode(updatedUser.getPassword());
            aplicationUserExistente.setPassword(encodedPassword);
            aplicationUserExistente.setAuthorities(roleRepository.findByAuthority(updatedUser.getRole()));

            // Actualizar otros atributos del UsuarioEntity según sea necesario
            usuarioExistente.setEmail(updatedUser.getEmail());

            // Guardar los cambios en el AplicationUser y el UsuarioEntity
            userRepository.save(aplicationUserExistente);
            usuarioRepository.save(usuarioExistente);

            // Devolver el UserAdmDTO actualizado
            return new UserAdmDTO(
                    usuarioExistente.getIdUsuario(),
                    updatedUser.getUsername(),
                    updatedUser.getEmail(),
                    encodedPassword,
                    updatedUser.getRole()
            );
        } else {
            return null;
        }
    }
}
