package pe.LaCasona.backend_casona.models.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserAdmDTO {
    private int id;
    private String username;
    private String email;
    private String password;
    private String role;

    public UserAdmDTO(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public UserAdmDTO(int id, String username, String email, String password, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public UserAdmDTO() {
    }
}
