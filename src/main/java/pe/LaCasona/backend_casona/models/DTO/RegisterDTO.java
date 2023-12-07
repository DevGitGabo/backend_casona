package pe.LaCasona.backend_casona.models.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RegisterDTO {
    private String username;
    private String gmail;
    private String password;

    public RegisterDTO() {
        super();
    }

    public RegisterDTO(String username, String gmail, String password) {
        this.username = username;
        this.gmail = gmail;
        this.password = password;
    }
}
