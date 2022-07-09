package edu.kalum.core.controllers;


import edu.kalum.core.model.dao.services.IAspiranteService;
import edu.kalum.core.model.dao.services.ICarreraTecnicaService;
import edu.kalum.core.model.dao.services.IExamenAdmisionService;
import edu.kalum.core.model.dao.services.IJornadaService;
import edu.kalum.core.model.entities.Aspirante;
import edu.kalum.core.model.entities.CarreraTecnica;
import edu.kalum.core.model.entities.ExamenAdmision;
import edu.kalum.core.model.entities.Jornada;
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
public class AspiranteController {

    private Logger logger = LoggerFactory.getLogger(AspiranteController.class);

    @Value("${edu.kalum.core.configuration.page.size}")
    private Integer size;

    @Autowired
    private IAspiranteService aspiranteService;

    @Autowired
    private ICarreraTecnicaService carreraTecnicaService;

    @Autowired
    private IJornadaService jornadaService;

    @Autowired
    private IExamenAdmisionService examenAdmisionService;

    @GetMapping("/aspirantes")
    public ResponseEntity<?> listarAspirantes(){
        Map<String, Object> response = new HashMap<>();
        logger.info("Se inicio proceso de listar aspirantes");
        try {
            List<Aspirante> aspirantes = aspiranteService.findAll();
            if (aspirantes == null) {
                logger.warn("Error, No se pudo listar los aspirantes");
                response.put("Error","No se pudo listar los aspirantes");
                return new ResponseEntity<Map<String, Object>>(response,HttpStatus.NO_CONTENT);
            } else {
                logger.info("Se pudo listar los aspirantes correctamente.");
                return new ResponseEntity<List<Aspirante>>(aspirantes,HttpStatus.OK);
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

    @GetMapping("aspirantes/{aspiranteId}")
    public ResponseEntity<?> mostrarAspirante(@PathVariable String aspiranteId){
        Map<String, Object> response = new HashMap<>();
        logger.info("Se inicio proceso de mostrar aspirante por ID");
        try {
            Aspirante aspirante = aspiranteService.findById(aspiranteId);
            if (aspirante == null) {
                logger.warn("Error, No se pudo mostrar el aspirante.");
                response.put("Error","No se pudo mostrar el aspirante.");
                return new ResponseEntity<Map<String, Object>>(response,HttpStatus.NO_CONTENT);
            } else {
                logger.info("Se listo los aspirantes exitosamente");
                return new ResponseEntity<Aspirante>(aspirante, HttpStatus.OK);
            }
        }   catch (CannotCreateTransactionException e){
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

    @PostMapping("/aspirantes")
    public ResponseEntity<?> save(@Valid @RequestBody Aspirante value, BindingResult result){
        Map<String,Object> response = new HashMap<>();
        logger.info("Se inicio proceso de guardar un nuevo aspirante");
        if (result.hasErrors()) {
            List<String> errores = result.getFieldErrors().stream().map(error -> error.getDefaultMessage()).collect(Collectors.toList());
            response.put("Errores",errores);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        } try {
            Aspirante aspirante = aspiranteService.findById(value.getNoExpediente());
            if (aspirante != null) {
                logger.warn("Ya xiste un registro con el numero de expediente: ".concat(value.getNoExpediente()));
                response.put("Mensaje","Ya xiste un registro con el numero de expediente: ".concat(value.getNoExpediente()));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
            CarreraTecnica carreraTecnica = carreraTecnicaService.findById(value.getCarreraTecnica().getCarreraId());
            if (carreraTecnica == null) {
                logger.warn("No existe la carrera con el id: ".concat(value.getCarreraTecnica().getCarreraId()));
                response.put("Mensaje","No existe la carrera con el id: ".concat(value.getCarreraTecnica().getCarreraId()));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            } else {
                value.setCarreraTecnica(carreraTecnica);
            }
            Jornada jornada = jornadaService.findById(value.getJornada().getJornadaId());
            if (jornada == null) {
                logger.warn("No existe la jornada con codigo: ".concat(value.getJornada().getJornadaId()));
                response.put("Mensaje","No existe la jornada con codigo: ".concat(value.getJornada().getJornadaId()));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            } else {
                value.setJornada(jornada);
            }

            ExamenAdmision examenAdmision = examenAdmisionService.findById(value.getExamenAdmision().getExamenId());
            if (examenAdmision == null) {
                logger.warn("NO existe el examen con el id: ".concat(value.getExamenAdmision().getExamenId()));
                response.put("Mensaje","NO existe el examen con el id: ".concat(value.getExamenAdmision().getExamenId()));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            } else {
                value.setExamenAdmision(examenAdmision);
            }

            aspiranteService.save(value);
            logger.info("Se almaceno correctamente el nuevo aspirante.");
            response.put("Mensaje","Se almaceno correctamente el nuevo aspirante.");
            response.put("Aspirante",value);
            return new ResponseEntity<Map<String, Object>>(response,HttpStatus.CREATED);

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

    @PutMapping("/aspirantes/{noExpediente}")
    public ResponseEntity<?> update(@Valid @RequestBody Aspirante value, BindingResult result, @PathVariable String noExpediente){
        Map<String,Object> response = new HashMap<>();
        logger.info("Se inicio proceso de actualizar un aspirante");
        if (result.hasErrors()) {
            List<String> errores = result.getFieldErrors().stream().map(error -> error.getDefaultMessage()).collect(Collectors.toList());
            response.put("Errores",errores);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        } try {
            Aspirante aspirante = aspiranteService.findById(noExpediente);
            if (aspirante == null) {
                logger.warn("No xiste un aspirante con numero de expediente: ".concat(value.getNoExpediente()));
                response.put("Mensaje","No xiste un aspirante con numero de expediente: ".concat(value.getNoExpediente()));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
            CarreraTecnica carreraTecnica = carreraTecnicaService.findById(value.getCarreraTecnica().getCarreraId());
            if (carreraTecnica == null) {
                logger.warn("No existe la carrera con el id: ".concat(value.getCarreraTecnica().getCarreraId()));
                response.put("Mensaje","No existe la carrera con el id: ".concat(value.getCarreraTecnica().getCarreraId()));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            } else {
                value.setCarreraTecnica(carreraTecnica);
            }
            Jornada jornada = jornadaService.findById(value.getJornada().getJornadaId());
            if (jornada == null) {
                logger.warn("No existe la jornada con codigo: ".concat(value.getJornada().getJornadaId()));
                response.put("Mensaje","No existe la jornada con codigo: ".concat(value.getJornada().getJornadaId()));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            } else {
                value.setJornada(jornada);
            }

            ExamenAdmision examenAdmision = examenAdmisionService.findById(value.getExamenAdmision().getExamenId());
            if (examenAdmision == null) {
                logger.warn("NO existe el examen con el id: ".concat(value.getExamenAdmision().getExamenId()));
                response.put("Mensaje","NO existe el examen con el id: ".concat(value.getExamenAdmision().getExamenId()));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            } else {
                value.setExamenAdmision(examenAdmision);
            }

            aspiranteService.save(value);
            logger.info("Se almaceno correctamente el nuevo aspirante.");
            response.put("Mensaje","Se almaceno correctamente el nuevo aspirante.");
            response.put("Aspirante",value);
            return new ResponseEntity<Map<String, Object>>(response,HttpStatus.CREATED);

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

    @DeleteMapping("/aspirantes/{noExpediente}")
    public ResponseEntity<?> deleteAspirante(@PathVariable String noExpediente){
        Map<String,Object> response = new HashMap<>();
        logger.info("Se inicio proceso de eliminar un aspirante");
        try {
            Aspirante aspirante = aspiranteService.findById(noExpediente);
            if (aspirante == null) {
                logger.warn("No xiste un aspirante con numero de expediente: ".concat(noExpediente));
                response.put("Mensaje","No xiste un aspirante con numero de expediente: ".concat(noExpediente));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            } else {
                aspiranteService.delete(aspirante);
                response.put("Mensaje","El aspirante se elimino exitosamente");
                response.put("Aspirnte eliminado",aspirante);
                return new ResponseEntity<Map<String , Object>>(response, HttpStatus.OK);
            }

        } catch (CannotCreateTransactionException e){
            logger.error("Error, no se pudo acceder a la base de datos");
            response.put("Error","no se pudo acceder a la base de datos");
            response.put("Mensaje",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        } catch (DataAccessException e){
            logger.error("Error al momento de eliminar el registro a la base de datos");
            response.put("Mensaje","Error al momento de eliminar el registro a la base de datos");
            response.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @GetMapping("/aspirantes/page/{page}")
    public ResponseEntity<?> index (@PathVariable int page){
        Map<String,Object> response = new HashMap<>();
        logger.info("Inciando proceso de listar aspirantes por pagina");
        try {
            Pageable pageable = PageRequest.of(page,size);
            Page<Aspirante> aspirantePage = aspiranteService.findAll(pageable);
            if (aspirantePage == null || aspirantePage.isEmpty()) {
                logger.warn("Error, no hay aspirantes disponibles");
                response.put("Error","no hay aspirantes disponibles");
                return new ResponseEntity<Map<String,Object>>(response, HttpStatus.NO_CONTENT);
            } else {
                logger.info("Se listo la pagina "+page+ " de aspirantes exitosamente");
                return new ResponseEntity<Page<Aspirante>>(aspirantePage,HttpStatus.OK);
            }
        }catch (CannotCreateTransactionException e){
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
