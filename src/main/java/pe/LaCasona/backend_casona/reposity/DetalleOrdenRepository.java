package pe.LaCasona.backend_casona.reposity;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.LaCasona.backend_casona.models.Entity.DetalleOrdenEntity;
import pe.LaCasona.backend_casona.models.Entity.OrdenEntity;

import java.util.List;

public interface DetalleOrdenRepository extends JpaRepository<DetalleOrdenEntity, Integer> {
    List<DetalleOrdenEntity> findByOrden(OrdenEntity orden);
}
