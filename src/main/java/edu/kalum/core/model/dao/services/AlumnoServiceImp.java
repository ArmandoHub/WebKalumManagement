package edu.kalum.core.model.dao.services;

import edu.kalum.core.model.dao.IAlumnoDao;
import edu.kalum.core.model.entities.Alumno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlumnoServiceImp implements IAlumnoService {

    @Autowired
    private IAlumnoDao alumnoDao;

    @Override
    public List<Alumno> findAll() {
        return alumnoDao.findAll();
    }

    @Override
    public Alumno findById(String alumnoId) {
        return alumnoDao.findById(alumnoId).orElse(null);
    }

    @Override
    public Alumno save(Alumno alumno) {
        return alumnoDao.save(alumno);
    }
    @Override
    public void delete(Alumno alumno) {
        alumnoDao.delete(alumno);
    }

    @Override
    public Page<Alumno> findAll(Pageable pageable) {
        return alumnoDao.findAll(pageable);
    }

}
