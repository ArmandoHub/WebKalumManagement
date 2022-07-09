package edu.kalum.core.model.dao.services;

import edu.kalum.core.model.dao.ICarreraTecnicaDao;
import edu.kalum.core.model.entities.CarreraTecnica;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarreraTecnicaServiceImp implements ICarreraTecnicaService{

    @Autowired
    private ICarreraTecnicaDao carreraTecnicaDao;

    @Override
    public List<CarreraTecnica> findAll() {
        return carreraTecnicaDao.findAll();
    }

    @Override
    public Page<CarreraTecnica> findAll(Pageable pageable) {
        return carreraTecnicaDao.findAll(pageable);
    }

    @Override
    public CarreraTecnica findById(String carreraId) {
        return carreraTecnicaDao.findById(carreraId).orElse(null);
    }

    @Override
    public CarreraTecnica save(CarreraTecnica carreraTecnica) {
        return carreraTecnicaDao.save(carreraTecnica);
    }

    @Override
    public void delete(CarreraTecnica carreraTecnica) {
        carreraTecnicaDao.delete(carreraTecnica);
    }
}