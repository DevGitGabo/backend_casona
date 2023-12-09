package pe.LaCasona.backend_casona.reposity;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.LaCasona.backend_casona.models.Entity.OrdenEntity;

import java.util.Date;
import java.util.List;

public interface OrdenRepository extends JpaRepository<OrdenEntity, Integer> {
    List<OrdenEntity> findByFechaOrdenBetween(Date startDate, Date endDate);
    List<OrdenEntity> findAll();
    OrdenEntity findByIdOrden (Integer id);
}

