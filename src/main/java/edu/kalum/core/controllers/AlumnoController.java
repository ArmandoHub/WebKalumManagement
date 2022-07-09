package edu.kalum.core.controllers;

import edu.kalum.core.model.dao.services.IAlumnoService;
import edu.kalum.core.model.entities.Alumno;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/kalum-management/v1")
public class AlumnoController {

    Logger logger = LoggerFactory.getLogger(AlumnoController.class);

    @Value("${edu.kalum.core.configuration.page.size}")
    private Integer size;

    @Autowired
    private IAlumnoService alumnoService;

    @GetMapping("/alumnos")
    public ResponseEntity<?> listarAlumnos(){
        Map<String, Object> response = new HashMap<>();
        logger.info("Inciciando proceso de mostrar alumnos.");
        try{
            List<Alumno> alumnos = alumnoService.findAll();
            if (alumnos == null || alumnos.isEmpty()) {
                logger.warn("No se encontraron alumnos.");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else{
                logger.info("Se consulto la tabla de alumnos");
                return new ResponseEntity<List<Alumno>>(alumnos, HttpStatus.OK);
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

    @GetMapping("/alumnos/{alumnoId}")
    public ResponseEntity<?> mostrarAlumno(@PathVariable String alumnoId){
        Map<String, Object> response = new HashMap<>();
        logger.info("Iniciando proceso de mostrar alumno por id");
        try {
            Alumno alumno = alumnoService.findById(alumnoId);
            if (alumno == null) {
                logger.warn("El alumno con el id: ".concat(alumnoId).concat(" no existe."));
                response.put("Error","El alumno con el id: ".concat(alumnoId).concat(" no existe."));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            } else {
                logger.info("el id: ".concat(alumnoId).concat(" del alumno: ".concat(alumno.nombres)));
                return new ResponseEntity<Alumno>(alumno, HttpStatus.OK);
            }
        } catch (CannotCreateTransactionException e){
        logger.error("Error ,No se pudo conectar a la base de datos");
        response.put("Mensaje", "Error al momento de conectarse a la base de datos");
        response.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }   catch (DataAccessException e){
        logger.error("Error al momento de realizar la consulta a la base de datos");
        response.put("Mensaje","Error al momento de realizar la consulta a la base de datos");
        response.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @PostMapping("/alumnos")
    public ResponseEntity<?> saveAlumno(@Valid @RequestBody Alumno value, BindingResult result){
        Map<String, Object> response = new HashMap<>();
        logger.info("Iniciando proceso de guardar alumno");
        if (result.hasErrors()) {
            List<String> errores = result.getFieldErrors().stream().map(error -> error.getDefaultMessage()).collect(Collectors.toList());
            response.put("Errores", errores);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }
        try {
            alumnoService.save(value);
            logger.info("El alumno se guardo exitosamente.");
            response.put("Mensaje","El alumno se guardo exitosamente.");
            response.put("Alumno",value);
            return new ResponseEntity<Map<String,Object>>(response, HttpStatus.CREATED);
        } catch (CannotCreateTransactionException e){
            logger.error("Error ,No se pudo conectar a la base de datos");
            response.put("Mensaje", "Error al momento de conectarse a la base de datos");
            response.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }   catch (DataAccessException e){
            logger.error("Error al momento de realizar la consulta a la base de datos");
            response.put("Mensaje","Error al momento de realizar la consulta a la base de datos");
            response.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }


    @PutMapping("/alumnos/{alumnoId}")
    public ResponseEntity<?> updateAlumno(@Valid @RequestBody Alumno value, BindingResult result, @PathVariable String alumnoId){
        Map<String,Object> response = new HashMap<>();
        logger.info("Se inicio proceso de actualizar un nuevo aspirante");
        if (result.hasErrors()) {
            List<String> errores = result.getFieldErrors().stream().map(error -> error.getDefaultMessage()).collect(Collectors.toList());
            response.put("Errores",errores);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }
        try {
            Alumno alumno = alumnoService.findById(alumnoId);
            if (alumno == null) {
                logger.warn("Error al momento de actualizar, el alumno con el id: ".concat(alumnoId).concat(" no existe"));
                response.put("Menaje","Error al momento de actualizar, el alumno con el id: ".concat(alumnoId).concat(" no existe"));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
            alumnoService.save(value);
            logger.info("Se actualizo correctamente el nuevo aspirante.");
            response.put("Mensaje","Se actualizo correctamente el nuevo aspirante.");
            response.put("Aspirante",value);
            return new ResponseEntity<Map<String, Object>>(response,HttpStatus.OK);
        } catch (CannotCreateTransactionException e){
            logger.error("Error ,No se pudo conectar a la base de datos");
            response.put("Mensaje", "Error al momento de conectarse a la base de datos");
            response.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }   catch (DataAccessException e){
            logger.error("Error al momento de realizar la actualizacion a la base de datos");
            response.put("Mensaje","Error al momento de realizar la actualizacion a la base de datos");
            response.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @DeleteMapping("/alumnos/{alumnoId}")
    public ResponseEntity<?> deleteAlumno(@PathVariable String alumnoId){
        Map<String, Object> response = new HashMap<>();
        logger.info("Inciando proceso de eliminar alumno por id");
        try {
            Alumno alumno = alumnoService.findById(alumnoId);
            if (alumno == null) {
                logger.warn("Error, el alumno con el id: ".concat(alumnoId).concat(" no existe"));
                response.put("Mensaje","Error, el alumno con el id: ".concat(alumnoId).concat(" no existe"));
                return new ResponseEntity<Map<String,Object>>(response, HttpStatus.BAD_REQUEST);
            } else {
                alumnoService.delete(alumno);
                logger.info("El alumno se elimino exitosamente");
                response.put("Mensaje","El alumno se elimino exitosamente");
                response.put("Alumno eliminado",alumno);
                return new ResponseEntity<Map<String , Object>>(response, HttpStatus.OK);
            }
        } catch (CannotCreateTransactionException e){
            logger.error("Error ,No se pudo conectar a la base de datos");
            response.put("Mensaje", "Error al momento de conectarse a la base de datos");
            response.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }   catch (DataAccessException e){
            logger.error("Error al momento de eliminar el registro a la base de datos");
            response.put("Mensaje","Error al momento de eliminar el registro a la base de datos");
            response.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @GetMapping("/alumnos/page/{page}")
    public ResponseEntity<?> index(@PathVariable int page){
        Map<String, Object> response = new HashMap<>();
        logger.info("Iniciando proceso de listar alumos por pagina");
        try {
            Pageable pageable = PageRequest.of(page,size);
            Page<Alumno> alumnoPage = alumnoService.findAll(pageable);
            if (alumnoPage == null || alumnoPage.isEmpty()) {
                logger.warn("Error, no hay alumnos disponibles");
                response.put("Error","no hay alumnos disponibles");
                return new ResponseEntity<Map<String,Object>>(response, HttpStatus.NO_CONTENT);
            } else {
                logger.info("Se listo la pagina " + page +" de alumnos con exito");
                return new ResponseEntity<Page<Alumno>>(alumnoPage,HttpStatus.OK);
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
