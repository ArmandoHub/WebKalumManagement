package edu.kalum.core.model.dao.services;

import edu.kalum.core.model.entities.ExamenAdmision;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


public interface IExamenAdmisionService {

    public List<ExamenAdmision> findAll();
    public ExamenAdmision findById(String examenId);
    public ExamenAdmision save(ExamenAdmision examenAdmision);
    public void delete(ExamenAdmision examenAdmision);
    public Page<ExamenAdmision> findAll(Pageable pageable);
}
