package edu.kalum.core.model.dao.services;

import edu.kalum.core.model.entities.Jornada;

import java.util.List;

public interface IJornadaService {

    public List<Jornada> findAll();
    public Jornada findById(String jornadaId);
}
