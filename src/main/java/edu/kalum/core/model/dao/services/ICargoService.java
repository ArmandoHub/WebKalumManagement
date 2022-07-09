package edu.kalum.core.model.dao.services;

import edu.kalum.core.model.entities.Cargo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICargoService {
    public List<Cargo> findAll();
    public Cargo finById(String cargoId);
    public Cargo save(Cargo cargo);
    public void delete(Cargo cargo);
    public Page findAll(Pageable pageable);
}
