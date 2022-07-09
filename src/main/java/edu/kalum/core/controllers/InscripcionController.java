package edu.kalum.core.controllers;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import edu.kalum.core.model.dao.services.IAspiranteService;
import edu.kalum.core.model.dao.services.ICarreraTecnicaService;
import edu.kalum.core.model.dao.services.IInscripcionService;
import edu.kalum.core.model.dtos.EnrollmentRequestDTO;
import edu.kalum.core.model.entities.Aspirante;
import edu.kalum.core.model.entities.CarreraTecnica;
import edu.kalum.core.model.entities.Inscripcion;
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
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/kalum-management/v1")
public class InscripcionController {

    Logger logger = LoggerFactory.getLogger(InscripcionController.class);

    @Value("${edu.kalum.core.configuration.page.size}")
    private Integer size;

    @Autowired
    private IInscripcionService iInscripcionService;

    @Autowired
    private IAspiranteService iAspiranteService;

    @Autowired
    private ICarreraTecnicaService iCarreraTecnicaService;

    @GetMapping("/inscripciones")
    public ResponseEntity<?> listarInscripciones() {
        Map<String, Object> response = new HashMap<>();
        logger.info("Iniciando proceso de listar todas las inscripciones.");
        try {
            List<Inscripcion> inscripciones = iInscripcionService.findAll();
            if (inscripciones == null) {
                logger.warn("No hay inscripciones disponibles.");
                response.put("Error", "No hay inscripciones disponibles.");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NO_CONTENT);
            } else {
                logger.info("Se listaron las inscripciones exitosamente.");
                response.put("Mensaje", "Se listaron las inscripciones exitosamente.");
                return new ResponseEntity<List<Inscripcion>>(inscripciones, HttpStatus.OK);
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

    @GetMapping("/inscripciones/page/{page}")
    public ResponseEntity<?> index(@PathVariable int page){
        Map<String,Object> response = new HashMap<>();
        logger.info("Inciando proceso de listar inscripciones por pagina");
        try {
            Pageable pageable = PageRequest.of(page,size);
            Page<Inscripcion> inscripcionPage = iInscripcionService.findAll(pageable);
            if (inscripcionPage == null || inscripcionPage.isEmpty()) {
                logger.warn("Error, no hay inscripciones disponibles");
                response.put("Error","no hay inscripciones disponibles");
                return new ResponseEntity<Map<String,Object>>(response, HttpStatus.NO_CONTENT);
            } else {
                logger.info("Se listo la pagina "+page+ " de inscripciones exitosamente");
                return new ResponseEntity<Page<Inscripcion>>(inscripcionPage,HttpStatus.OK);
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

    @PostMapping("/inscripciones/enrollment")
    public ResponseEntity<?> enrollmentProcess(@Valid @RequestBody EnrollmentRequestDTO request, BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        logger.info("Iniciando proceso de encolar a RabbitMQ la inscripcion");
        if (result.hasErrors()) {
            List<String> errores = result.getFieldErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());

            response.put("Errores", errores);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }
        try {
            Aspirante aspirante = iAspiranteService.findById(request.getNoExpediente());
            if (aspirante == null) {
                logger.warn("El aspirante con el expediente: ".concat(request.getNoExpediente()).concat(" no existe"));
                response.put("Error", "El aspirante con el expediente: ".concat(request.getNoExpediente()).concat(" no existe"));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }
            CarreraTecnica carreraTecnica = iCarreraTecnicaService.findById(request.getCarreraId());
            if (carreraTecnica == null) {
                logger.warn("La carrera tecnica con el id: ".concat(request.getCarreraId()).concat(" no existe"));
                response.put("Error", "La carrera tecnica con el id: ".concat(request.getCarreraId()).concat(" no existe"));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NO_CONTENT);
            }
            boolean respuesta = crearSolicitudInscripcion(request);
            if (respuesta == true){
                return new ResponseEntity<String>("la solicitud de inscripcion fue generada con exito",HttpStatus.OK);
            }else {
                response.put("Error","Error al momento de crear la solicitud de inscripcion con el expediente ".concat(request.getNoExpediente()));
                response.put("Mensaje","Error al escribir en la cola");
                return new ResponseEntity<Map<String,Object>>(response,HttpStatus.SERVICE_UNAVAILABLE);
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
        } catch (Exception e) {
            logger.error("Error al momento de realizar la consulta a la base de datos");
            response.put("Mensaje", "Error al momento de realizar la consulta a la base de datos");
            response.put("Error", e.getMessage().concat(": ").concat(e.getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    private boolean crearSolicitudInscripcion(EnrollmentRequestDTO request) throws Exception {
        boolean respuesta = false;
        Connection connection = null;
        Channel channel = null;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("/");

        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            String message = new Gson().toJson(request);
            channel.basicPublish("","",null, message.getBytes(StandardCharsets.UTF_8));
            respuesta = true;
        } catch (Exception e){
            logger.error("Error al escribir a la cola, ".concat(e.getMessage()));
        } finally {
            channel.close();
            connection.close();
        }
        return respuesta;
    }

}
