package edu.kalum.core.model.dao.services;

import edu.kalum.core.model.dao.IAspiranteDao;
import edu.kalum.core.model.entities.Aspirante;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AspiranteServiceImp implements IAspiranteService{

    @Autowired
    private IAspiranteDao aspiranteDao;

    @Override
    public List<Aspirante> findAll() {
        return aspiranteDao.findAll();
    }

    @Override
    public Aspirante findById(String aspiranteId) {
        return aspiranteDao.findById(aspiranteId).orElse(null);
    }

    @Override
    public Aspirante save(Aspirante aspirante) {
        return aspiranteDao.save(aspirante);
    }

    @Override
    public void delete(Aspirante aspirante) {
        aspiranteDao.delete(aspirante);
    }

    @Override
    public Page<Aspirante> findAll(Pageable pageable) {
        return aspiranteDao.findAll(pageable);
    }

}
