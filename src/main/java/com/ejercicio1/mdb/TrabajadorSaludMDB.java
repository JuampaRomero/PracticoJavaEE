package com.ejercicio1.mdb;

import com.ejercicio1.business.BusinessException;
import com.ejercicio1.business.TrabajadorSaludServiceLocal;
import com.ejercicio1.entities.TrabajadorSalud;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Message Driven Bean para procesar alta de TrabajadorSalud.
 * Escucha mensajes de la cola queue_alta_trabajadorsalud.
 * El formato del mensaje es: cedula|nombre|apellido|especialidad|matriculaProfesional|fechaIngreso|activo
 */
@MessageDriven(
    activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/queue_alta_trabajadorsalud"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
    }
)
public class TrabajadorSaludMDB implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(TrabajadorSaludMDB.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    
    @EJB
    private TrabajadorSaludServiceLocal trabajadorSaludService;

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            try {
                String contenido = textMessage.getText();
                LOGGER.log(Level.INFO, "Mensaje recibido: {0}", contenido);
                
                // Parsear el mensaje y crear el trabajador
                TrabajadorSalud trabajador = parsearMensaje(contenido);
                
                // Realizar el alta del trabajador
                trabajadorSaludService.agregarTrabajador(trabajador);
                
                LOGGER.log(Level.INFO, "Trabajador agregado exitosamente: {0}", trabajador.getCedula());
                
            } catch (JMSException e) {
                LOGGER.log(Level.SEVERE, "Error al procesar mensaje JMS", e);
            } catch (IllegalArgumentException e) {
                LOGGER.log(Level.SEVERE, "Error al parsear mensaje: formato inválido", e);
            } catch (BusinessException e) {
                LOGGER.log(Level.SEVERE, "Error de negocio al agregar trabajador: " + e.getMessage(), e);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error inesperado al procesar mensaje", e);
            }
        } else {
            LOGGER.log(Level.WARNING, "Mensaje recibido no es de tipo TextMessage");
        }
    }
    
    /**
     * Parsea el mensaje de texto y crea un objeto TrabajadorSalud.
     * 
     * @param mensaje String con formato: cedula|nombre|apellido|especialidad|matriculaProfesional|fechaIngreso|activo
     * @return TrabajadorSalud con los datos parseados
     * @throws IllegalArgumentException si el formato es inválido
     */
    private TrabajadorSalud parsearMensaje(String mensaje) {
        if (mensaje == null || mensaje.trim().isEmpty()) {
            throw new IllegalArgumentException("Mensaje vacío");
        }
        
        String[] campos = mensaje.split("\\|");
        
        if (campos.length != 7) {
            throw new IllegalArgumentException("Mensaje debe tener 7 campos separados por '|'");
        }
        
        try {
            TrabajadorSalud trabajador = new TrabajadorSalud();
            
            // cedula
            trabajador.setCedula(campos[0].trim());
            
            // nombre
            trabajador.setNombre(campos[1].trim());
            
            // apellido
            trabajador.setApellido(campos[2].trim());
            
            // especialidad
            trabajador.setEspecialidad(campos[3].trim());
            
            // matriculaProfesional
            try {
                trabajador.setMatriculaProfesional(Integer.parseInt(campos[4].trim()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Matrícula profesional debe ser un número entero");
            }
            
            // fechaIngreso
            try {
                LocalDate fecha = LocalDate.parse(campos[5].trim(), DATE_FORMATTER);
                trabajador.setFechaIngreso(fecha);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Fecha de ingreso debe tener formato ISO (YYYY-MM-DD)");
            }
            
            // activo
            String activoStr = campos[6].trim().toLowerCase();
            if ("true".equals(activoStr) || "1".equals(activoStr) || "si".equals(activoStr)) {
                trabajador.setActivo(true);
            } else if ("false".equals(activoStr) || "0".equals(activoStr) || "no".equals(activoStr)) {
                trabajador.setActivo(false);
            } else {
                throw new IllegalArgumentException("Campo activo debe ser true/false, 1/0, o si/no");
            }
            
            return trabajador;
            
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al parsear campos del mensaje: " + e.getMessage(), e);
        }
    }
}