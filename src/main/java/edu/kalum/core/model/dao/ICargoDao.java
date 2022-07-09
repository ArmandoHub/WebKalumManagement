package edu.kalum.core.model.dao;


import edu.kalum.core.model.entities.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICargoDao extends JpaRepository<Cargo, String> {

}
