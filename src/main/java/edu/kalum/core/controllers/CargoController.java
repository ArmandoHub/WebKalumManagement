package edu.kalum.core.controllers;

import edu.kalum.core.model.dao.services.ICargoService;
import edu.kalum.core.model.entities.Cargo;
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
public class CargoController {

    Logger logger = LoggerFactory.getLogger(CargoController.class);

    @Value("${edu.kalum.core.configuration.page.size}")
    private Integer size;

    @Autowired
    private ICargoService cargoService;

    @GetMapping("/cargos")
    public ResponseEntity<?> listarCargos(){
        Map<String, Object> response = new HashMap<>();
        List<Cargo> cargos = cargoService.findAll();
        logger.info("Iniciando proceso de listar cargos.");
        try {
            if (cargos == null) {
                logger.warn("Error, cargos esta vacia");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                logger.info("Se listo los cargos exitosamente.");
                return new ResponseEntity<List<Cargo>>(cargos, HttpStatus.OK);
            } } catch (CannotCreateTransactionException e){
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

    @GetMapping("/cargos/{cargoId}")
    public ResponseEntity<?> mostrarCargo(@PathVariable String cargoId){
        Map<String, Object> response = new HashMap<>();
        Cargo cargo = cargoService.finById(cargoId);
        logger.info("Iniciando proceso de mostrar cargo por id");
        try {
            if (cargo == null) {
                logger.warn("El cargo con el id: ".concat(cargoId).concat(" no existe."));
                response.put("Error","El cargo con el id: ".concat(cargoId).concat(" no existe."));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            } else {
                logger.info("el id: ".concat(cargoId).concat(" del cargo: ".concat(cargo.descripcion)));
                return new ResponseEntity<Cargo>(cargo, HttpStatus.OK);
            }
        }  catch (CannotCreateTransactionException e){
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

    @PostMapping("/cargos")
    public ResponseEntity saveCargo(@Valid @RequestBody Cargo value, BindingResult result){
        Map<String, Object> response = new HashMap<>();
        logger.info("Iniciando proceso de guardar nuevo cargo");
        if (result.hasErrors()) {
            List<String> errores = result.getFieldErrors().stream().map(error -> error.getDefaultMessage()).collect(Collectors.toList());
            response.put("Errores", errores);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }
        try {
            value.setCargoId(UUID.randomUUID().toString());
            Cargo cargo = cargoService.save(value);
            logger.info("Se almaceno correctamente el nuevo cargo.");
            response.put("Cargo",value);
            response.put("Mensaje","Se almaceno correctamente el nuevo cargo.");
            return new ResponseEntity<Map<String, Object>>(response,HttpStatus.CREATED);

        }  catch (CannotCreateTransactionException e){
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

    @PutMapping("/cargos/{cargoId}")
    public ResponseEntity<?> updateCargo(@Valid @RequestBody Cargo value,  BindingResult result, @PathVariable String cargoId){
        Map<String,Object> response = new HashMap<>();
        logger.info("Se inicio proceso de actualizar un nuevo cargo");
        if (result.hasErrors()) {
            List<String> errores = result.getFieldErrors().stream().map(error -> error.getDefaultMessage()).collect(Collectors.toList());
            response.put("Errores",errores);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }
        try {
            Cargo cargo = cargoService.finById(cargoId);
            if (cargo == null) {
                logger.warn("El cargo con el id: ".concat(cargoId).concat(" no existe"));
                response.put("Mensaje","El cargo con el id: ".concat(cargoId).concat(" no existe"));
                return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
            } else {
                cargoService.save(value);
                logger.info("El nuevo cargo se guardo con exito");
                response.put("Mensaje","El nuevo cargo se actualizo con exito");
                response.put("Cargo",value);
                return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
            }
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

    @DeleteMapping("/cargos/{cargoId}")
    public ResponseEntity<?> deleteCargo(@PathVariable String cargoId){
        Map<String, Object> response = new HashMap<>();
        try {
            Cargo cargo = cargoService.finById(cargoId);
            if (cargo == null) {
                logger.warn("El cargo con el id: ".concat(cargoId).concat(" no existe"));
                response.put("Mensaje","El cargo con el id: ".concat(cargoId).concat(" no existe"));
                return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
            } else {
                cargoService.delete(cargo);
                response.put("Mensaje","El cargo se elimino exitosamente");
                response.put("Cargo eliminada",cargo);
                return new ResponseEntity<Map<String , Object>>(response, HttpStatus.OK);
            }
        } catch (CannotCreateTransactionException e){
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

    @GetMapping("/cargos/page/{page}")
    public ResponseEntity<?> index(@PathVariable int page){
        Map<String, Object> response = new HashMap<>();
        logger.info("Iniciando proceso de listar cargo por pagina");
        try {
            Pageable pageable = PageRequest.of(page,size);
            Page<Cargo> cargoPage = cargoService.findAll(pageable);
            if (cargoPage == null || cargoPage.isEmpty()) {
                logger.warn("No se pudo listar los cargos por pagina.");
                response.put("Mensaje","No se pudo listar los cargos por pagina.");
                return new ResponseEntity<Map<String,Object>>(response, HttpStatus.NO_CONTENT);
            } else {
                logger.info("Se listo la pagina " + page + " de cargos exitosamente");
                return new ResponseEntity<Page<Cargo>>(cargoPage, HttpStatus.OK);
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

