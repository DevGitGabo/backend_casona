package pe.LaCasona.backend_casona.reposity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.LaCasona.backend_casona.models.Auth.Role;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Set<Role> findByAuthority(String authority);
}
