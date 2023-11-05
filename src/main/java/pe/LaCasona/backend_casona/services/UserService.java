package pe.LaCasona.backend_casona.services;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import pe.LaCasona.backend_casona.models.AplicationUser;
import pe.LaCasona.backend_casona.models.Role;

public class UserService implements UserDetailsService {

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("In the user details service");

        if (!username.equals("Ethan"))
            throw new UsernameNotFoundException("Not Etham");

        Set<Role> roles = new HashSet<>();
        roles.add(new Role(1, "USER"));

        return new AplicationUser(1, "Ethan", encoder.encode("password"), roles);

    }

}
