package com.ejercicio1.jms;

import com.ejercicio1.entities.TrabajadorSalud;
import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.jms.*;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio EJB para enviar mensajes JMS a la cola de alta de trabajadores.
 */
@Stateless
public class JMSMessageSender {
    
    private static final Logger LOGGER = Logger.getLogger(JMSMessageSender.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    
    @Resource(lookup = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;
    
    @Resource(lookup = "java:/jms/queue/queue_alta_trabajadorsalud")
    private Queue queue;
    
    /**
     * Envía un mensaje a la cola JMS con los datos del trabajador para su alta asíncrona.
     * 
     * @param trabajador El trabajador a dar de alta
     * @throws JMSException si ocurre un error al enviar el mensaje
     */
    public void enviarMensajeAlta(TrabajadorSalud trabajador) throws JMSException {
        Connection connection = null;
        Session session = null;
        
        try {
            // Crear conexión y sesión
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            // Crear el productor de mensajes
            MessageProducer producer = session.createProducer(queue);
            
            // Construir el mensaje con formato: cedula|nombre|apellido|especialidad|matriculaProfesional|fechaIngreso|activo
            String mensajeTexto = construirMensaje(trabajador);
            
            // Crear el mensaje de texto
            TextMessage message = session.createTextMessage(mensajeTexto);
            
            // Enviar el mensaje
            producer.send(message);
            
            LOGGER.log(Level.INFO, "Mensaje enviado a la cola para trabajador con cédula: {0}", trabajador.getCedula());
            
        } finally {
            // Cerrar recursos
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException e) {
                    LOGGER.log(Level.WARNING, "Error al cerrar sesión JMS", e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    LOGGER.log(Level.WARNING, "Error al cerrar conexión JMS", e);
                }
            }
        }
    }
    
    /**
     * Construye el mensaje de texto con el formato esperado por el MDB.
     * 
     * @param trabajador El trabajador cuyos datos se incluirán en el mensaje
     * @return String con formato: cedula|nombre|apellido|especialidad|matriculaProfesional|fechaIngreso|activo
     */
    private String construirMensaje(TrabajadorSalud trabajador) {
        StringBuilder mensaje = new StringBuilder();
        
        // cedula
        mensaje.append(trabajador.getCedula() != null ? trabajador.getCedula() : "");
        mensaje.append("|");
        
        // nombre
        mensaje.append(trabajador.getNombre() != null ? trabajador.getNombre() : "");
        mensaje.append("|");
        
        // apellido
        mensaje.append(trabajador.getApellido() != null ? trabajador.getApellido() : "");
        mensaje.append("|");
        
        // especialidad
        mensaje.append(trabajador.getEspecialidad() != null ? trabajador.getEspecialidad() : "");
        mensaje.append("|");
        
        // matriculaProfesional
        mensaje.append(trabajador.getMatriculaProfesional() != null ? trabajador.getMatriculaProfesional() : "");
        mensaje.append("|");
        
        // fechaIngreso
        if (trabajador.getFechaIngreso() != null) {
            mensaje.append(trabajador.getFechaIngreso().format(DATE_FORMATTER));
        }
        mensaje.append("|");
        
        // activo
        mensaje.append(trabajador.isActivo());
        
        return mensaje.toString();
    }
}