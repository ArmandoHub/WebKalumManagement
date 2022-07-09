package edu.kalum.core.controllers;

import edu.kalum.core.model.dao.services.IExamenAdmisionService;
import edu.kalum.core.model.entities.ExamenAdmision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/kalum-management/v1")
public class ExamenAdmisionController {

    Logger logger = LoggerFactory.getLogger(ExamenAdmisionController.class);

    @Value("${edu.kalum.core.configuration.page.size}")
    private Integer size;

    @Autowired
    private IExamenAdmisionService examenAdmisionService;

    @GetMapping("/examenes-admision")
    public ResponseEntity<?> listarExamenesAdmision(){
        Map<String, Object> response = new HashMap<>();
        logger.info("Inciciando proceso de mostrar examenes de admision.");
        try {
            List<ExamenAdmision> examenesAdmision = examenAdmisionService.findAll();
            if (examenesAdmision == null) {
                logger.warn("No se encontraron examenes de admision.");
                response.put("Error","No se encontraron examenes de admision.");
                return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NO_CONTENT);
            } else {
                logger.info("Se consulto la tabla de examen_admision");
                return new ResponseEntity<List<ExamenAdmision>>(examenesAdmision, HttpStatus.OK);
            }
        } catch (CannotCreateTransactionException e){
            logger.error("Error, no se pudo acceder a la base de datos");
            response.put("Error","no se pudo acceder a la base de datos");
            response.put("Mensaje",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        } catch (DataAccessException e){
            logger.error("Error al momento de realizar la consulta a la base de datos");
            response.put("Mensaje","Error al momento de realizar la consulta a la base de datos");
            response.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @GetMapping("/examenes-admision/{examenId}")
    public ResponseEntity<?> mostrarExamenDeAdmision(@PathVariable String examenId){
        Map<String, Object> response = new HashMap<>();
        logger.info("Iniciando proceso de mostrar examen por ID");
        try {
            ExamenAdmision examenAdmision = examenAdmisionService.findById(examenId);
            if (examenAdmision == null) {
                logger.warn("Error, No se encontraron examenes.");
                response.put("Error","No se encontraron examenes.");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NO_CONTENT);
            } else {
                logger.info("Se mostro el examen correctamente.");
                return new ResponseEntity<ExamenAdmision>(examenAdmision, HttpStatus.OK);
            }
        }  catch (CannotCreateTransactionException e){
            logger.error("Error, no se pudo acceder a la base de datos");
            response.put("Error","no se pudo acceder a la base de datos");
            response.put("Mensaje",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        } catch (DataAccessException e){
            logger.error("Error al momento de realizar la consulta a la base de datos");
            response.put("Mensaje","Error al momento de realizar la consulta a la base de datos");
            response.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @PostMapping("/examenes-admision")
    public ResponseEntity<?> save(@Valid @RequestBody ExamenAdmision value, BindingResult result){
        Map<String, Object> response = new HashMap<>();
        if (result.hasErrors()) {
            List<String> errores = result.getFieldErrors().stream().map(error -> error.getDefaultMessage()).collect(Collectors.toList());
            response.put("Errores",errores);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }
        try {
            value.setExamenId(UUID.randomUUID().toString());
            ExamenAdmision examenAdmision = examenAdmisionService.save(value);
            logger.info("Se almaceno correctamente el examen de admision");
            response.put("Mensaje","Se almaceno correctamente el examen de admision");
            response.put("Examen de admision",examenAdmision);
            return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
        }  catch (CannotCreateTransactionException e){
            logger.error("Error, no se pudo acceder a la base de datos");
            response.put("Error","no se pudo acceder a la base de datos");
            response.put("Mensaje",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        } catch (DataAccessException e){
            logger.error("Error al momento de crear el nuevo registro");
            response.put("Mensaje","Error al momento de crear el nuevo registro");
            response.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @PutMapping("/examenes-admision/{examenId}")
    public ResponseEntity<?> update(@Valid @RequestBody ExamenAdmision value, BindingResult result, @PathVariable String examenId){
        Map<String, Object> response = new HashMap<>();
        if (result.hasErrors()) {
            List<String> errores = result.getFieldErrors().stream().map(error -> error.getDefaultMessage()).collect(Collectors.toList());
            response.put("Errores",errores);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }
        try {
            ExamenAdmision examenAdmision = examenAdmisionService.findById(examenId);
            if (examenAdmision == null) {
                logger.warn("Error, no se encontro el examen de admision con el id: ".concat(examenId));
                response.put("Error","no se encontro el examen de admision con el id: ".concat(examenId));
                return new ResponseEntity<Map<String, Object>>(response,HttpStatus.NOT_FOUND);
            } else {
                examenAdmision.setFechaExamen(value.getFechaExamen());
                examenAdmisionService.save(examenAdmision);
                logger.info("Se actualizo correctamente el examen de admision.");
                response.put("Mensaje","Se actualizo correctamente el examen de admision.");
                response.put("Examen de admision",examenAdmision);
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
            }
        }   catch (CannotCreateTransactionException e){
            logger.error("Error, no se pudo acceder a la base de datos");
            response.put("Error","no se pudo acceder a la base de datos");
            response.put("Mensaje",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        } catch (DataAccessException e){
            logger.error("Error al momento de actualizar el examen de admision");
            response.put("Mensaje","Error al momento de actualizar el examen de admision");
            response.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @DeleteMapping("/examenes-admision/{examenId}")
    public ResponseEntity<?> delete(@PathVariable String examenId){
        Map<String, Object> response = new HashMap<>();
        logger.info("Se inicio proceso para eliminar examen por ID");
        try {
            ExamenAdmision examenAdmision = examenAdmisionService.findById(examenId);
            if (examenAdmision == null) {
                logger.warn("No se encontro el examen de admision con el id: ".concat(examenId));
                response.put("Error","No se encontro el examen de admision con el id: ".concat(examenId));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NO_CONTENT);
            } else {
                examenAdmisionService.delete(examenAdmision);
                logger.info("Se elimino el examen con el id: ".concat(examenId).concat(" exitosamente."));
                response.put("Mensaje","Se elimino el examen con el id: ".concat(examenId).concat(" exitosamente."));
                return new ResponseEntity<Map<String, Object>>(response,HttpStatus.OK);
            }
        }   catch (CannotCreateTransactionException e){
            logger.error("Error, no se pudo acceder a la base de datos");
            response.put("Error","no se pudo acceder a la base de datos");
            response.put("Mensaje",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        } catch (DataAccessException e){
            logger.error("Error al momento de eliminar el examen de admision");
            response.put("Mensaje","Error al momento de eliminar el examen de admision");
            response.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }

    }

    @GetMapping("/examenes-admision/page/{page}")
    public ResponseEntity<?> index(@PathVariable int page){
        Map<String,Object> response = new HashMap<>();
        logger.info("Inciando proceso de listar examenes de admision por pagina");
        try {
            Pageable pageable = PageRequest.of(page,size);
            Page<ExamenAdmision> examenAdmisionPage = examenAdmisionService.findAll(pageable);
            if (examenAdmisionPage == null || examenAdmisionPage.isEmpty()) {
                logger.warn("Error, no hay examenes de admision disponibles");
                response.put("Error","no hay examenes de admision disponibles");
                return new ResponseEntity<Map<String,Object>>(response, HttpStatus.NO_CONTENT);
            } else{
                logger.info("Se listo la pagina "+page+ " de examenes de admision exitosamente");
                return new ResponseEntity<Page<ExamenAdmision>>(examenAdmisionPage,HttpStatus.OK);
            }
        } catch (CannotCreateTransactionException e){
            logger.error("Error ,No se pudo conectar a la base de datos");
            response.put("Mensaje", "Error al momento de conectarse a la base de datos");
            response.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }   catch (DataAccessException e){
            logger.error("Error al momento de consulta a la base de datos");
            response.put("Mensaje","Error al momento de consulta a la base de datos");
            response.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

}
