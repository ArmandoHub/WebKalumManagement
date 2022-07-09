package edu.kalum.core.controllers;

import edu.kalum.core.model.dao.services.ICarreraTecnicaService;
import edu.kalum.core.model.dao.services.IInversionCarreraTecnicaService;
import edu.kalum.core.model.entities.InversionCarreraTecnica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/kalum-management/v1")
public class InversionCarreraTecnicaController {

    Logger logger = LoggerFactory.getLogger(InscripcionController.class);

    @Value("${edu.kalum.core.configuration.page.size}")
    private Integer size;

    @Autowired
    private IInversionCarreraTecnicaService iInversionCarreraTecnicaService;

    @Autowired
    private ICarreraTecnicaService iCarreraTecnicaService;

    @GetMapping("/inversion-carrera-tecnica")
    public ResponseEntity<?> listarInversionCarrerasTecnicas(){
        Map<String, Object> response = new HashMap<>();
        logger.info("Iniciando proceso de listar las inversiones por carrera tecnica.");
        try {
            List<InversionCarreraTecnica> inversionCarreraTecnicas = iInversionCarreraTecnicaService.findAll();
            if (inversionCarreraTecnicas == null || inversionCarreraTecnicas.isEmpty()) {
                logger.warn("No hay inversiones de carreras tecnicas disponibles.");
                response.put("Error","No hay inversiones de carreras tecnicas disponibles.");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NO_CONTENT);
            } else {
                logger.info("Se listaron las inversiones de carreras tecnicas exitosamente.");
                response.put("Mensaje", "Se listaron las inversiones de carreras tecnicas exitosamente.");
                return new ResponseEntity<List<InversionCarreraTecnica>>(inversionCarreraTecnicas, HttpStatus.OK);
            }
        } catch (CannotCreateTransactionException e) {
            logger.error("Error, no se pudo acceder a la base de datos");
            response.put("Error", "no se pudo acceder a la base de datos");
            response.put("Mensaje", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        } catch (DataAccessException e) {
            logger.error("Error al momento de realizar la consulta a la base de datos");
            response.put("Mensaje", "Error al momento de realizar la consulta a la base de datos");
            response.put("Error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }


}
