package edu.kalum.core.model.dao;

import edu.kalum.core.model.entities.Jornada;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IJornadaDao extends JpaRepository<Jornada,String> {

}
