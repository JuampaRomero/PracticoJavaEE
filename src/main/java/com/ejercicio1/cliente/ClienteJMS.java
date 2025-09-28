package com.ejercicio1.cliente;

import jakarta.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Scanner;

/**
 * Cliente standalone para enviar mensajes JMS a la cola de alta de trabajadores.
 * Este cliente puede ejecutarse fuera del servidor de aplicaciones.
 */
public class ClienteJMS {
    
    private static final String CONNECTION_FACTORY_JNDI = "jms/RemoteConnectionFactory";
    private static final String QUEUE_JNDI = "jms/queue/queue_alta_trabajadorsalud";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    
    private Context context;
    private ConnectionFactory connectionFactory;
    private Queue queue;
    
    public ClienteJMS() throws NamingException {
        initializeJNDI();
    }
    
    /**
     * Inicializa el contexto JNDI para conectarse al servidor remoto
     */
    private void initializeJNDI() throws NamingException {
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        props.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
        
        // Credenciales si es necesario (ajustar según la configuración del servidor)
        // props.put(Context.SECURITY_PRINCIPAL, "usuario");
        // props.put(Context.SECURITY_CREDENTIALS, "password");
        
        context = new InitialContext(props);
        
        // Buscar la connection factory y la cola
        connectionFactory = (ConnectionFactory) context.lookup(CONNECTION_FACTORY_JNDI);
        queue = (Queue) context.lookup(QUEUE_JNDI);
    }
    
    /**
     * Envía un mensaje a la cola con los datos del trabajador
     */
    public void enviarMensaje(String cedula, String nombre, String apellido, 
                             String especialidad, Integer matricula, 
                             LocalDate fechaIngreso, boolean activo) throws JMSException {
        
        Connection connection = null;
        Session session = null;
        
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            MessageProducer producer = session.createProducer(queue);
            
            // Construir el mensaje con formato: cedula|nombre|apellido|especialidad|matriculaProfesional|fechaIngreso|activo
            String mensajeTexto = construirMensaje(cedula, nombre, apellido, 
                                                  especialidad, matricula, 
                                                  fechaIngreso, activo);
            
            TextMessage message = session.createTextMessage(mensajeTexto);
            
            producer.send(message);
            
            System.out.println("Mensaje enviado exitosamente: " + mensajeTexto);
            
        } finally {
            if (session != null) session.close();
            if (connection != null) connection.close();
        }
    }
    
    /**
     * Construye el mensaje de texto con el formato esperado por el MDB
     */
    private String construirMensaje(String cedula, String nombre, String apellido,
                                   String especialidad, Integer matricula,
                                   LocalDate fechaIngreso, boolean activo) {
        StringBuilder mensaje = new StringBuilder();
        
        mensaje.append(cedula != null ? cedula : "").append("|");
        mensaje.append(nombre != null ? nombre : "").append("|");
        mensaje.append(apellido != null ? apellido : "").append("|");
        mensaje.append(especialidad != null ? especialidad : "").append("|");
        mensaje.append(matricula != null ? matricula : "").append("|");
        mensaje.append(fechaIngreso != null ? fechaIngreso.format(DATE_FORMATTER) : "").append("|");
        mensaje.append(activo);
        
        return mensaje.toString();
    }
    
    /**
     * Método principal con interfaz de línea de comandos
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        try {
            ClienteJMS cliente = new ClienteJMS();
            
            System.out.println("=== Cliente JMS - Alta de Trabajador de Salud ===");
            System.out.println();
            
            boolean continuar = true;
            
            while (continuar) {
                System.out.println("Ingrese los datos del trabajador:");
                
                System.out.print("Cédula: ");
                String cedula = scanner.nextLine();
                
                System.out.print("Nombre: ");
                String nombre = scanner.nextLine();
                
                System.out.print("Apellido: ");
                String apellido = scanner.nextLine();
                
                System.out.print("Especialidad: ");
                String especialidad = scanner.nextLine();
                
                System.out.print("Matrícula Profesional (número): ");
                Integer matricula = null;
                String matriculaStr = scanner.nextLine();
                if (!matriculaStr.trim().isEmpty()) {
                    try {
                        matricula = Integer.parseInt(matriculaStr);
                    } catch (NumberFormatException e) {
                        System.out.println("Advertencia: Matrícula inválida, se enviará vacía");
                    }
                }
                
                System.out.print("Fecha de Ingreso (YYYY-MM-DD) [Enter para hoy]: ");
                LocalDate fechaIngreso = LocalDate.now();
                String fechaStr = scanner.nextLine();
                if (!fechaStr.trim().isEmpty()) {
                    try {
                        fechaIngreso = LocalDate.parse(fechaStr);
                    } catch (Exception e) {
                        System.out.println("Advertencia: Fecha inválida, se usará la fecha actual");
                    }
                }
                
                System.out.print("¿Activo? (s/n) [s]: ");
                String activoStr = scanner.nextLine();
                boolean activo = !activoStr.toLowerCase().startsWith("n");
                
                try {
                    // Enviar el mensaje
                    cliente.enviarMensaje(cedula, nombre, apellido, 
                                        especialidad, matricula, 
                                        fechaIngreso, activo);
                    
                    System.out.println("\n✓ Mensaje enviado exitosamente a la cola JMS");
                    
                } catch (JMSException e) {
                    System.err.println("\n✗ Error al enviar el mensaje: " + e.getMessage());
                    e.printStackTrace();
                }
                
                System.out.print("\n¿Desea enviar otro trabajador? (s/n): ");
                String respuesta = scanner.nextLine();
                continuar = respuesta.toLowerCase().startsWith("s");
            }
            
            // Cerrar el contexto
            if (cliente.context != null) {
                cliente.context.close();
            }
            
            System.out.println("\nCliente finalizado.");
            
        } catch (NamingException e) {
            System.err.println("Error al conectar con el servidor JMS:");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
    
    /**
     * Método alternativo para envío programático (sin interacción del usuario)
     */
    public static void enviarMensajeProgramatico() {
        try {
            ClienteJMS cliente = new ClienteJMS();
            
            // Ejemplo de envío programático
            cliente.enviarMensaje("12345678", "Juan", "Pérez", 
                                "Cardiología", 98765, 
                                LocalDate.now(), true);
            
            System.out.println("Mensaje enviado programáticamente");
            
            if (cliente.context != null) {
                cliente.context.close();
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}