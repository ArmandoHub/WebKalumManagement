package edu.kalum.core.model.dao.services;

import edu.kalum.core.model.dao.IExamenAdmisionDao;
import edu.kalum.core.model.entities.ExamenAdmision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamenAdmisionServiceImp implements IExamenAdmisionService{

    @Autowired
    private IExamenAdmisionDao examenAdmisionDao;

    @Override
    public List<ExamenAdmision> findAll() {
        return examenAdmisionDao.findAll();
    }

    @Override
    public ExamenAdmision findById(String examenId) {
        return examenAdmisionDao.findById(examenId).orElse(null);
    }

    @Override
    public ExamenAdmision save(ExamenAdmision examenAdmision) {
        return examenAdmisionDao.save(examenAdmision);
    }

    @Override
    public void delete(ExamenAdmision examenAdmision) {
        examenAdmisionDao.delete(examenAdmision);
    }

    @Override
    public Page<ExamenAdmision> findAll(Pageable pageable) {
        return examenAdmisionDao.findAll(pageable);
    }


}
