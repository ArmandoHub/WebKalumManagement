package edu.kalum.core.model.dao.services;

import edu.kalum.core.model.dao.IInversionCarreraTecnicaDao;
import edu.kalum.core.model.entities.InversionCarreraTecnica;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InversionCarreraTecnicaServiceImp implements IInversionCarreraTecnicaService{

    @Autowired
    private IInversionCarreraTecnicaDao iInversionCarreraTecnicaDao;

    @Override
    public List<InversionCarreraTecnica> findAll() {
        return iInversionCarreraTecnicaDao.findAll();
    }

    @Override
    public Page<InversionCarreraTecnica> findAll(Pageable pageable) {
        return iInversionCarreraTecnicaDao.findAll(pageable);
    }

    @Override
    public InversionCarreraTecnica findById(String inversionId) {
        return iInversionCarreraTecnicaDao.findById(inversionId).orElse(null);
    }

    @Override
    public InversionCarreraTecnica save(InversionCarreraTecnica inversionCarreraTecnica) {
        return iInversionCarreraTecnicaDao.save(inversionCarreraTecnica);
    }

    @Override
    public void delete(InversionCarreraTecnica inversionCarreraTecnica) {
        iInversionCarreraTecnicaDao.delete(inversionCarreraTecnica);
    }
}
