package edu.kalum.core.model.dao.services;

import edu.kalum.core.model.entities.Inscripcion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IInscripcionService {
    public List<Inscripcion> findAll();
    public Inscripcion findById(String inscripcionId);
    public Page<Inscripcion> findAll(Pageable pageable);
}
