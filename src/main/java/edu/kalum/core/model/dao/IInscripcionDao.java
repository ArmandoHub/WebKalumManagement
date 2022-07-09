package edu.kalum.core.model.dao;


import edu.kalum.core.model.entities.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IInscripcionDao extends JpaRepository<Inscripcion, String> {

}
