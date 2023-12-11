package pe.LaCasona.backend_casona.reposity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.LaCasona.backend_casona.models.Entity.HistorialReporteEntity;

public interface HistorialReporteRepository extends JpaRepository<HistorialReporteEntity, Long> {
    // Puedes agregar métodos personalizados si necesitas realizar consultas específicas
}
