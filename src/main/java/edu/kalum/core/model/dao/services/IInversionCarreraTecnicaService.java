package edu.kalum.core.model.dao.services;


import edu.kalum.core.model.entities.InversionCarreraTecnica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IInversionCarreraTecnicaService {
    public List<InversionCarreraTecnica> findAll();
    public Page<InversionCarreraTecnica> findAll(Pageable pageable);
    public InversionCarreraTecnica findById(String inversionId);
    public InversionCarreraTecnica save(InversionCarreraTecnica inversionCarreraTecnica);
    public void delete(InversionCarreraTecnica inversionCarreraTecnica);
}
