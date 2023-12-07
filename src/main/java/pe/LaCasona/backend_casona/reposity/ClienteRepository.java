package pe.LaCasona.backend_casona.reposity;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.LaCasona.backend_casona.models.Entity.ClienteEntity;
import pe.LaCasona.backend_casona.models.Entity.UsuarioEntity;

public interface ClienteRepository extends JpaRepository<ClienteEntity, Integer> {
    ClienteEntity findByIdCliente(int id);
    ClienteEntity findByUsuario(UsuarioEntity usuario);
}
