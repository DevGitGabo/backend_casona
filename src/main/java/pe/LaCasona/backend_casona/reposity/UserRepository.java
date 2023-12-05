package pe.LaCasona.backend_casona.reposity;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.LaCasona.backend_casona.models.Auth.AplicationUser;

@Repository
public interface UserRepository extends JpaRepository<AplicationUser, Integer> {
    Optional<AplicationUser> findByUsername(String username);
}
