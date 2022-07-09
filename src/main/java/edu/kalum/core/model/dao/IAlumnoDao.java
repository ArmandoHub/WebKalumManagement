package edu.kalum.core.model.dao;

import edu.kalum.core.model.entities.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAlumnoDao extends JpaRepository<Alumno,String> {

}
