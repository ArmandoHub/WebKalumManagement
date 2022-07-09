package edu.kalum.core.model.dao.services;

import edu.kalum.core.model.dao.ICargoDao;
import edu.kalum.core.model.entities.Cargo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CargoServiceImp implements ICargoService {

    @Autowired
    private ICargoDao cargoDao;


    @Override
    public List<Cargo> findAll() {
        return cargoDao.findAll();
    }

    @Override
    public Cargo finById(String cargoId) {
        return cargoDao.findById(cargoId).orElse(null);
    }

    @Override
    public Cargo save(Cargo cargo) {
        return cargoDao.save(cargo);
    }

    @Override
    public void delete(Cargo cargo) {
        cargoDao.delete(cargo);
    }

    @Override
    public Page findAll(Pageable pageable) {
        return cargoDao.findAll(pageable);
    }
}
