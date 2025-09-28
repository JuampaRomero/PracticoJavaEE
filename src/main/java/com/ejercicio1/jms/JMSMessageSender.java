package com.ejercicio1.jms;

import com.ejercicio1.entities.TrabajadorSalud;
import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.jms.*;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class JMSMessageSender {
    
    private static final Logger LOGGER = Logger.getLogger(JMSMessageSender.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    
    @Resource(lookup = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;
    
    @Resource(lookup = "java:/jms/queue/queue_alta_trabajadorsalud")
    private Queue queue;
    
    // Formato mensaje: cedula|nombre|apellido|especialidad|matriculaProfesional|fechaIngreso|activo
    public void enviarMensajeAlta(TrabajadorSalud trabajador) throws JMSException {
        Connection connection = null;
        Session session = null;
        
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            MessageProducer producer = session.createProducer(queue);
            
            String mensajeTexto = construirMensaje(trabajador);
            
            TextMessage message = session.createTextMessage(mensajeTexto);
            
            producer.send(message);
            
            LOGGER.log(Level.INFO, "Mensaje enviado a la cola para trabajador con cédula: {0}", trabajador.getCedula());
            
        } finally {
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
    
    private String construirMensaje(TrabajadorSalud trabajador) {
        StringBuilder mensaje = new StringBuilder();
        
        mensaje.append(trabajador.getCedula() != null ? trabajador.getCedula() : "");
        mensaje.append("|");
        
        mensaje.append(trabajador.getNombre() != null ? trabajador.getNombre() : "");
        mensaje.append("|");
        
        mensaje.append(trabajador.getApellido() != null ? trabajador.getApellido() : "");
        mensaje.append("|");
        
        mensaje.append(trabajador.getEspecialidad() != null ? trabajador.getEspecialidad() : "");
        mensaje.append("|");
        
        mensaje.append(trabajador.getMatriculaProfesional() != null ? trabajador.getMatriculaProfesional() : "");
        mensaje.append("|");
        
        if (trabajador.getFechaIngreso() != null) {
            mensaje.append(trabajador.getFechaIngreso().format(DATE_FORMATTER));
        }
        mensaje.append("|");
        
        mensaje.append(trabajador.isActivo());
        
        return mensaje.toString();
    }
}