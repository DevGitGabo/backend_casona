package pe.LaCasona.backend_casona.reposity;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.LaCasona.backend_casona.models.Entity.DetallePagoEntity;

public interface DetallePagoRepository extends JpaRepository<DetallePagoEntity, Integer> {
}
