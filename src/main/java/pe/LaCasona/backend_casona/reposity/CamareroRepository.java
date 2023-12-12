package pe.LaCasona.backend_casona.reposity;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.LaCasona.backend_casona.models.Entity.CamareroEntity;
import pe.LaCasona.backend_casona.models.Entity.UsuarioEntity;

import java.util.List;

public interface CamareroRepository extends JpaRepository<CamareroEntity, Integer> {
    CamareroEntity findByIdCamarero (int id);
    List<CamareroEntity> findByUsuario(UsuarioEntity usuario);
}
