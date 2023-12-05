package pe.LaCasona.backend_casona.models.DTO;

import pe.LaCasona.backend_casona.models.Auth.AplicationUser;

public class LoginResponseDTO {
    private AplicationUser user;
    private String jwt;

    public LoginResponseDTO() {
        super();
    }

    public LoginResponseDTO(AplicationUser user, String jwt) {
        this.user = user;
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }

    public AplicationUser getUser() {
        return user;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public void setUser(AplicationUser user) {
        this.user = user;
    }
}
