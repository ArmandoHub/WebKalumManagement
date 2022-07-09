package edu.kalum.core.controllers;

import edu.kalum.core.model.dao.services.ICarreraTecnicaService;
import edu.kalum.core.model.entities.CarreraTecnica;
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
public class CarreraTecnicaController {

    private Logger logger = LoggerFactory.getLogger(CarreraTecnicaController.class);

    @Value("${edu.kalum.core.configuration.page.size}")
    private  Integer size;

    @Autowired
    private ICarreraTecnicaService carreraTecnicaService;

    @GetMapping("/carreras-tecnicas")
    public ResponseEntity<?> listarCarrerasTecnicas(){
        Map<String, Object> response = new HashMap<>();
        logger.info("Iniciando proceso de listar las carreras tecnicas");
        try {
            List<CarreraTecnica> carrerasTecnicas = carreraTecnicaService.findAll();
            if (carrerasTecnicas == null) {
                logger.error("NO se encontraron carreras tecnicas");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                logger.info("Se consulto las carreras tecnicas");
                return new ResponseEntity<List<CarreraTecnica>>(carrerasTecnicas, HttpStatus.OK);
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

    @GetMapping("/carreras-tecnicas/{carreraId}")
    public ResponseEntity<?> mostrarCarreraTecnica(@PathVariable String carreraId){
        Map<String, Object> response = new HashMap<>();
        logger.info("Iniciando busqueda de carrera tecnica por id");
        try {
            CarreraTecnica carreraTecnica = carreraTecnicaService.findById(carreraId);
            if (carreraTecnica == null) {
                logger.warn("No existe la carrera tecnica con el id ".concat(carreraId));
                response.put("Mensaje","no existe la carrera con el id ".concat(carreraId));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            } else {
                logger.info("Se encontro la carrera con el id".concat(carreraId).concat(" exitosamente"));
                return new ResponseEntity<CarreraTecnica>(carreraTecnica, HttpStatus.OK);
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


    @PostMapping("/carreras-tecnicas")
    public ResponseEntity<?> create(@Valid @RequestBody CarreraTecnica value, BindingResult result){
        Map<String, Object> response = new HashMap<>();
        if (result.hasErrors() == true) {
            List<String> errores = result.getFieldErrors().stream().map(error -> error.getDefaultMessage()).collect(Collectors.toList());
            response.put("Errores",errores);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }
        try {
            value.setCarreraId(UUID.randomUUID().toString());
            CarreraTecnica carreraTecnica = carreraTecnicaService.save(value);
            response.put("Mensaje","La carrera se almaceno correctamente");
            response.put("Carrera tecnica,",carreraTecnica);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        } catch (CannotCreateTransactionException e){
            logger.error("Error ,No se pudo conectar a la base de datos");
            response.put("Mensaje", "Error al momento de conectarse a la base de datos");
            response.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }   catch (DataAccessException e){
            logger.error("Error al momento de crear el nuevo registro");
            response.put("Mensaje","Error al momento de crear el nuevo registro");
            response.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @PutMapping("/carreras-tecnicas/{carreraId}")
    public ResponseEntity update(@Valid @RequestBody CarreraTecnica value, BindingResult result, @PathVariable String carreraId){
        Map<String, Object> response = new HashMap<>();
        if (result.hasErrors()) {
            List<String> errores = result.getFieldErrors().stream().map(error -> error.getDefaultMessage()).collect(Collectors.toList());
            response.put("Errores",errores);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }
        try {
            CarreraTecnica carreraTecnica = carreraTecnicaService.findById(carreraId);
            if (carreraTecnica == null) {
                response.put("Mensaje","La carrera con el id ".concat(carreraId).concat(" no existe"));
                return new ResponseEntity<Map<String,Object>>(response, HttpStatus.NOT_FOUND);
            } else {
                carreraTecnica.setCarreraTecnica(value.getCarreraTecnica());
                carreraTecnicaService.save(carreraTecnica);
                response.put("MEnsaje","La carrera tecnica se actualizo exitosamente");
                response.put("Carrera tecnica",carreraTecnica);
                return new ResponseEntity<Map<String , Object>>(response, HttpStatus.OK);
            }
        } catch (CannotCreateTransactionException e){
            logger.error("Error ,No se pudo conectar a la base de datos");
            response.put("Mensaje", "Error al momento de conectarse a la base de datos");
            response.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }   catch (DataAccessException e){
            logger.error("Error al momento de actualizar el registro");
            response.put("Mensaje","Error al momento de actualizar el registro");
            response.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @DeleteMapping("/carreras-tecnicas/{carreraId}")
    public ResponseEntity<?> delete(@PathVariable String carreraId){
        Map<String, Object> response = new HashMap<>();
        try {
            CarreraTecnica carreraTecnica = carreraTecnicaService.findById(carreraId);
            if (carreraTecnica == null) {
                response.put("Mensaje","La carrera con el id ".concat(carreraId).concat(" no existe"));
                return new ResponseEntity<Map<String,Object>>(response, HttpStatus.NOT_FOUND);
            } else {
                carreraTecnicaService.delete(carreraTecnica);
                response.put("MEnsaje","La carrera tecnica se elimino exitosamente");
                response.put("Carrera tecnica eliminada",carreraTecnica);
                return new ResponseEntity<Map<String , Object>>(response, HttpStatus.OK);
            }
        }  catch (CannotCreateTransactionException e){
            logger.error("Error ,No se pudo conectar a la base de datos");
            response.put("Mensaje", "Error al momento de conectarse a la base de datos");
            response.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }   catch (DataAccessException e){
            logger.error("Error al momento de eliminar el registro");
            response.put("Mensaje","Error al momento de eliminar el registro");
            response.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @GetMapping("/carreras-tecnicas/page/{page}")
    public ResponseEntity<?> index(@PathVariable int page){
        Map<String, Object> response = new HashMap<>();
        try {
            Pageable pageable = PageRequest.of(page,size);
            Page<CarreraTecnica> carrerasTecnicasPage = carreraTecnicaService.findAll(pageable);
            if (carrerasTecnicasPage == null && carrerasTecnicasPage.getSize() == 0) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<Page<CarreraTecnica>>(carrerasTecnicasPage, HttpStatus.OK);
            }
        }   catch (CannotCreateTransactionException e){
            logger.error("Error ,No se pudo conectar a la base de datos");
            response.put("Mensaje", "Error al momento de conectarse a la base de datos");
            response.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }   catch (DataAccessException e){
            logger.error("Error al momento de eliminar el registro");
            response.put("Mensaje","Error al momento de eliminar el registro");
            response.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

}
