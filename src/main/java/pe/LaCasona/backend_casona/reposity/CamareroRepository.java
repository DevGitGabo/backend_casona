package pe.LaCasona.backend_casona.reposity;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.LaCasona.backend_casona.models.Entity.CamareroEntity;

public interface CamareroRepository extends JpaRepository<CamareroEntity, Integer> {
    CamareroEntity findByIdCamarero (int id);
}
