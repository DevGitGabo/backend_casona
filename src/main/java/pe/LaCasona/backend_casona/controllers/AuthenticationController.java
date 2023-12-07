package pe.LaCasona.backend_casona.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import pe.LaCasona.backend_casona.models.DTO.LoginDTO;
import pe.LaCasona.backend_casona.models.DTO.LoginResponseDTO;
import pe.LaCasona.backend_casona.models.DTO.RegisterDTO;
import pe.LaCasona.backend_casona.models.DTO.RegisterResponseDTO;
import pe.LaCasona.backend_casona.services.AuthenticationService;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/")
    public String helloAdminController() {
        return "Authentication level access";
    }
    @PostMapping("/register")
    public RegisterResponseDTO registerUser(@RequestBody RegisterDTO body) {
        return authenticationService.registerUser(body.getUsername(), body.getPassword());
    }
    @PostMapping("/login")
    public LoginResponseDTO loginUser(@RequestBody LoginDTO body) {
        return authenticationService.loginUser(body.getUsername(), body.getPassword());
    }
}
