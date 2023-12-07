package pe.LaCasona.backend_casona.models.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import pe.LaCasona.backend_casona.models.Auth.AplicationUser;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class LoginResponseDTO {
    private UserDTO user;
    private String token;
    public LoginResponseDTO(AplicationUser applicationUser, String token) {
        this.user = new UserDTO(applicationUser.getUsername(), getRoles(applicationUser));
        this.token = token;
    }
    private List<String> getRoles(AplicationUser applicationUser) {
        return applicationUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)  // Access the authority directly
                .collect(Collectors.toList());
    }
    @Getter
    @Setter
    private static class UserDTO {
        private String username;
        private List<String> role;

        public UserDTO(String username, List<String> roles) {
            this.username = username;
            this.role = roles;
        }
    }
}
