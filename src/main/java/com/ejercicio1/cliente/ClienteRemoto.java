package com.ejercicio1.cliente;

import com.ejercicio1.business.TrabajadorSaludServiceRemote;
import com.ejercicio1.dao.TrabajadorSaludDAORemote;
import com.ejercicio1.entities.TrabajadorSalud;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
import java.util.Scanner;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Cliente remoto para acceder a los EJBs del sistema de gestion de trabajadores de salud.
 * 
 * Esta clase demuestra como un cliente Java standalone puede conectarse a un servidor
 * de aplicaciones Jakarta EE y utilizar los servicios remotos expuestos por los EJBs.
 * 
 * IMPORTANTE: Este cliente necesita:
 * 1. El servidor de aplicaciones (ej: WildFly, GlassFish) ejecutandose
 * 2. La aplicacion desplegada en el servidor
 * 3. Las librerias cliente del servidor de aplicaciones en el classpath
 * 
 * @author Sistema de Gestion de Identidades
 */
public class ClienteRemoto {
    
    // Referencias a los EJBs remotos
    private TrabajadorSaludServiceRemote servicioRemoto;
    private TrabajadorSaludDAORemote daoRemoto;
    
    // Scanner para leer entrada del usuario
    private Scanner scanner;
    
    // Formateador de fechas para mostrar las fechas de manera legible
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /**
     * Constructor del cliente remoto.
     * Inicializa el scanner para entrada de usuario.
     */
    public ClienteRemoto() {
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Metodo principal para inicializar la conexion con los EJBs remotos.
     * 
     * Este metodo configura las propiedades JNDI necesarias para conectarse
     * al servidor de aplicaciones y buscar los EJBs remotos.
     * 
     * NOTA: Las propiedades JNDI varian segun el servidor de aplicaciones:
     * - WildFly/JBoss: usa propiedades especificas de JBoss
     * - GlassFish: usa propiedades diferentes
     * - WebLogic: tiene su propia configuracion
     */
    public void conectar() throws NamingException {
        System.out.println("========================================");
        System.out.println("Iniciando conexion con el servidor...");
        System.out.println("========================================\n");
        
        // Configurar las propiedades JNDI
        // Estas propiedades son para WildFly/JBoss EAP
        Properties props = new Properties();
        
        // Especifica la implementacion del contexto inicial
        // Para WildFly/JBoss usamos el contexto remoto de WildFly
        props.put(Context.INITIAL_CONTEXT_FACTORY, 
                  "org.wildfly.naming.client.WildFlyInitialContextFactory");
        
        // URL del servidor donde esta desplegada la aplicacion
        // Formato: remote+http://[servidor]:[puerto]
        // Puerto 8080 es el puerto HTTP estandar de WildFly
        props.put(Context.PROVIDER_URL, "remote+http://localhost:8080");
        
        // Credenciales de seguridad (si el servidor las requiere)
        // En produccion, estas credenciales NO deben estar hardcodeadas
        // props.put(Context.SECURITY_PRINCIPAL, "usuario");
        // props.put(Context.SECURITY_CREDENTIALS, "password");
        
        try {
            // Crear el contexto inicial con las propiedades configuradas
            Context contexto = new InitialContext(props);
            
            // Buscar los EJBs remotos usando JNDI
            // El nombre JNDI sigue el patron:
            // ejb:[app-name]/[module-name]/[distinct-name]/[bean-name]![fully-qualified-interface-name]
            
            // Para una aplicacion WAR en WildFly, el patron JNDI es:
            // ejb:/[nombre-war-sin-extension]/[nombre-del-bean]![interfaz-completa]
            // O tambien puede ser:
            // java:global/[nombre-aplicacion]/[nombre-del-bean]![interfaz-completa]
            
            System.out.println("Buscando EJB de servicio de negocio...");
            
            // Intentamos primero con el patron ejb:
            String jndiServicio = "ejb:/GestorIdentidades-1.0-SNAPSHOT/TrabajadorSaludService!" + 
                                  "com.ejercicio1.business.TrabajadorSaludServiceRemote";
            
            try {
                servicioRemoto = (TrabajadorSaludServiceRemote) contexto.lookup(jndiServicio);
                System.out.println("  [OK] Servicio encontrado con patron ejb:/");
            } catch (NamingException e) {
                // Si falla, intentamos con el patron global
                System.out.println("  [!] No encontrado con patron ejb:/, intentando con java:global/");
                jndiServicio = "java:global/GestorIdentidades-1.0-SNAPSHOT/TrabajadorSaludService!" + 
                               "com.ejercicio1.business.TrabajadorSaludServiceRemote";
                servicioRemoto = (TrabajadorSaludServiceRemote) contexto.lookup(jndiServicio);
                System.out.println("  [OK] Servicio encontrado con patron java:global/");
            }
            
            System.out.println("Buscando EJB de acceso a datos...");
            
            String jndiDAO = "ejb:/GestorIdentidades-1.0-SNAPSHOT/TrabajadorSaludDAO!" + 
                            "com.ejercicio1.dao.TrabajadorSaludDAORemote";
            
            try {
                daoRemoto = (TrabajadorSaludDAORemote) contexto.lookup(jndiDAO);
                System.out.println("  [OK] DAO encontrado con patron ejb:/");
            } catch (NamingException e) {
                // Si falla, intentamos con el patron global
                System.out.println("  [!] No encontrado con patron ejb:/, intentando con java:global/");
                jndiDAO = "java:global/GestorIdentidades-1.0-SNAPSHOT/TrabajadorSaludDAO!" + 
                         "com.ejercicio1.dao.TrabajadorSaludDAORemote";
                daoRemoto = (TrabajadorSaludDAORemote) contexto.lookup(jndiDAO);
                System.out.println("  [OK] DAO encontrado con patron java:global/");
            }
            
            System.out.println("\n[OK] Conexion establecida exitosamente");
            System.out.println("[OK] EJBs remotos disponibles\n");
            
            // Cerrar el contexto (buena practica)
            contexto.close();
            
        } catch (NamingException e) {
            System.err.println("\n[ERROR] Error al conectar con el servidor:");
            System.err.println("  - Verifique que el servidor este ejecutandose");
            System.err.println("  - Verifique que la aplicacion este desplegada");
            System.err.println("  - Verifique los nombres JNDI");
            System.err.println("\nDetalles del error: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Metodo principal del programa.
     * Punto de entrada de la aplicacion cliente.
     */
    public static void main(String[] args) {
        ClienteRemoto cliente = new ClienteRemoto();
        
        try {
            // Intentar conectar con el servidor
            cliente.conectar();
            
            // Si la conexion es exitosa, mostrar el menu principal
            cliente.ejecutarMenuPrincipal();
            
        } catch (NamingException e) {
            System.err.println("\nNo se pudo iniciar el cliente remoto.");
            System.err.println("Terminando la aplicacion...");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("\nError inesperado: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Muestra y ejecuta el menu principal de la aplicacion.
     * Este metodo contendra todas las opciones disponibles para el usuario.
     */
    private void ejecutarMenuPrincipal() {
        boolean continuar = true;
        
        // Bucle principal del menu
        while (continuar) {
            try {
                // Mostrar las opciones disponibles
                mostrarMenuPrincipal();
                
                // Leer la opcion del usuario
                System.out.print("\nSeleccione una opcion: ");
                String opcion = scanner.nextLine().trim();
                
                // Procesar la opcion seleccionada
                switch (opcion) {
                    case "1":
                        // Agregar un nuevo trabajador
                        agregarTrabajador();
                        break;
                        
                    case "2":
                        // Listar todos los trabajadores
                        listarTrabajadores();
                        break;
                        
                    case "3":
                        // Buscar trabajador por cedula
                        buscarPorCedula();
                        break;
                        
                    case "4":
                        // Buscar trabajadores por especialidad
                        buscarPorEspecialidad();
                        break;
                        
                    case "5":
                        // Ver estadisticas del sistema
                        verEstadisticas();
                        break;
                        
                    case "0":
                        // Salir del programa
                        System.out.println("\n========================================");
                        System.out.println("Gracias por usar el Sistema de Gestion");
                        System.out.println("Cerrando conexion...");
                        System.out.println("========================================\n");
                        continuar = false;
                        break;
                        
                    default:
                        System.out.println("\n[!] Opcion no valida. Por favor, intente nuevamente.");
                }
                
                // Pausar antes de continuar (excepto al salir)
                if (continuar) {
                    System.out.println("\nPresione ENTER para continuar...");
                    scanner.nextLine();
                }
                
            } catch (Exception e) {
                System.err.println("\n[ERROR] Error al procesar la opcion: " + e.getMessage());
                System.out.println("Presione ENTER para continuar...");
                scanner.nextLine();
            }
        }
    }
    
    /**
     * Muestra las opciones del menu principal.
     * Separa la logica de presentacion del menu de su ejecucion.
     */
    private void mostrarMenuPrincipal() {
        System.out.println("\n========================================");
        System.out.println("    SISTEMA DE GESTION DE IDENTIDADES");
        System.out.println("      Trabajadores del Sector Salud");
        System.out.println("========================================");
        System.out.println("\n--- MENU PRINCIPAL ---\n");
        System.out.println("1. Agregar nuevo trabajador");
        System.out.println("2. Listar todos los trabajadores");
        System.out.println("3. Buscar trabajador por cedula");
        System.out.println("4. Buscar trabajadores por especialidad");
        System.out.println("5. Ver estadisticas del sistema");
        System.out.println("\n0. Salir");
    }
    
    /**
     * Permite agregar un nuevo trabajador al sistema.
     * 
     * Este metodo:
     * 1. Recolecta todos los datos necesarios del usuario
     * 2. Valida los datos ingresados
     * 3. Crea un nuevo objeto TrabajadorSalud
     * 4. Lo envia al servicio remoto para su persistencia
     * 5. Maneja las reglas de negocio y errores
     */
    private void agregarTrabajador() {
        System.out.println("\n========================================");
        System.out.println("       AGREGAR NUEVO TRABAJADOR");
        System.out.println("========================================\n");
        System.out.println("Complete los siguientes datos:");
        System.out.println("(Los campos marcados con * son obligatorios)\n");
        
        try {
            // Variables para almacenar los datos del nuevo trabajador
            String cedula, nombre, apellido, especialidad;
            Integer matriculaProfesional;
            LocalDate fechaIngreso;
            boolean activo;
            
            // 1. RECOLECTAR CEDULA
            System.out.println("----------------------------------------");
            cedula = solicitarCedula();
            if (cedula == null) return; // El usuario cancelo
            
            // 2. RECOLECTAR NOMBRE
            System.out.println("----------------------------------------");
            nombre = solicitarTexto("Nombre *", 2, 50);
            if (nombre == null) return; // El usuario cancelo
            
            // 3. RECOLECTAR APELLIDO
            System.out.println("----------------------------------------");
            apellido = solicitarTexto("Apellido *", 2, 50);
            if (apellido == null) return; // El usuario cancelo
            
            // 4. RECOLECTAR ESPECIALIDAD
            System.out.println("----------------------------------------");
            especialidad = solicitarEspecialidad();
            if (especialidad == null) return; // El usuario cancelo
            
            // 5. RECOLECTAR MATRICULA PROFESIONAL
            System.out.println("----------------------------------------");
            matriculaProfesional = solicitarMatricula();
            if (matriculaProfesional == null) return; // El usuario cancelo
            
            // 6. RECOLECTAR FECHA DE INGRESO
            System.out.println("----------------------------------------");
            fechaIngreso = solicitarFecha();
            if (fechaIngreso == null) return; // El usuario cancelo
            
            // 7. RECOLECTAR ESTADO ACTIVO/INACTIVO
            System.out.println("----------------------------------------");
            activo = solicitarEstadoActivo();
            
            // Crear el objeto TrabajadorSalud con todos los datos recolectados
            TrabajadorSalud nuevoTrabajador = new TrabajadorSalud(
                cedula, nombre, apellido, especialidad,
                matriculaProfesional, fechaIngreso, activo
            );
            
            // Mostrar resumen antes de confirmar
            System.out.println("\n========================================");
            System.out.println("RESUMEN DEL NUEVO TRABAJADOR:");
            System.out.println("========================================");
            mostrarDetallesTrabajador(nuevoTrabajador);
            System.out.println("========================================");
            
            // Solicitar confirmacion
            System.out.print("\nDesea guardar este trabajador? (S/N): ");
            String confirmacion = scanner.nextLine().trim().toUpperCase();
            
            if ("S".equals(confirmacion) || "SI".equals(confirmacion)) {
                // Intentar agregar el trabajador usando el servicio remoto
                System.out.println("\nGuardando trabajador...");
                servicioRemoto.agregarTrabajador(nuevoTrabajador);
                
                // Si llegamos aqui, el trabajador se agrego exitosamente
                System.out.println("\n[OK] Trabajador agregado exitosamente!");
                System.out.println("  Cedula: " + cedula);
                System.out.println("  Nombre: " + nombre + " " + apellido);
                
            } else {
                System.out.println("\n[!] Operacion cancelada. No se guardo el trabajador.");
            }
            
        } catch (com.ejercicio1.business.BusinessException e) {
            // Error de validacion de negocio
            System.err.println("\n[ERROR] Error de validacion: " + e.getMessage());
            System.out.println("\nEl trabajador no pudo ser agregado debido a las reglas de negocio.");
            System.out.println("Por favor, verifique los datos e intente nuevamente.");
            
        } catch (Exception e) {
            // Otros errores (conexion, etc.)
            System.err.println("\n[ERROR] Error al agregar el trabajador: " + e.getMessage());
            System.err.println("Verifique la conexion con el servidor.");
        }
    }
    
    /**
     * Solicita y valida el numero de cedula.
     * @return La cedula ingresada o null si el usuario cancela
     */
    private String solicitarCedula() {
        while (true) {
            System.out.print("Cedula * (7-10 digitos, o 'cancelar' para volver): ");
            String entrada = scanner.nextLine().trim();
            
            if ("cancelar".equalsIgnoreCase(entrada)) {
                System.out.println("[!] Operacion cancelada.");
                return null;
            }
            
            if (entrada.matches("\\d{7,10}")) {
                return entrada;
            } else {
                System.out.println("  [!] La cedula debe contener entre 7 y 10 digitos numericos.");
            }
        }
    }
    
    /**
     * Solicita un texto con validacion de longitud.
     * @param campo Nombre del campo a solicitar
     * @param minLength Longitud minima
     * @param maxLength Longitud maxima
     * @return El texto ingresado o null si el usuario cancela
     */
    private String solicitarTexto(String campo, int minLength, int maxLength) {
        while (true) {
            System.out.print(campo + " (min " + minLength + " caracteres, o 'cancelar' para volver): ");
            String entrada = scanner.nextLine().trim();
            
            if ("cancelar".equalsIgnoreCase(entrada)) {
                System.out.println("[!] Operacion cancelada.");
                return null;
            }
            
            if (entrada.length() >= minLength && entrada.length() <= maxLength) {
                return entrada;
            } else {
                System.out.println("  [!] El " + campo.toLowerCase() + 
                                 " debe tener entre " + minLength + " y " + maxLength + " caracteres.");
            }
        }
    }
    
    /**
     * Solicita la especialidad mostrando opciones predefinidas.
     * @return La especialidad seleccionada o null si el usuario cancela
     */
    private String solicitarEspecialidad() {
        // Lista de especialidades disponibles
        String[] especialidades = {
            "Medicina General", "Pediatria", "Cardiologia", "Neurologia",
            "Ginecologia", "Traumatologia", "Oftalmologia", "Dermatologia",
            "Psiquiatria", "Anestesiologia", "Radiologia", "Enfermeria"
        };
        
        System.out.println("Especialidad * - Seleccione una opcion:");
        for (int i = 0; i < especialidades.length; i++) {
            System.out.println("  " + (i + 1) + ". " + especialidades[i]);
        }
        System.out.println("  13. Otra (ingresar manualmente)");
        System.out.println("  0. Cancelar");
        
        while (true) {
            System.out.print("\nSeleccione una opcion (1-13, o 0 para cancelar): ");
            String entrada = scanner.nextLine().trim();
            
            if ("0".equals(entrada)) {
                System.out.println("[!] Operacion cancelada.");
                return null;
            }
            
            try {
                int opcion = Integer.parseInt(entrada);
                if (opcion >= 1 && opcion <= 12) {
                    return especialidades[opcion - 1];
                } else if (opcion == 13) {
                    System.out.print("Ingrese la especialidad: ");
                    String otraEspecialidad = scanner.nextLine().trim();
                    if (!otraEspecialidad.isEmpty()) {
                        return otraEspecialidad;
                    }
                }
            } catch (NumberFormatException e) {
                // Entrada no numerica
            }
            
            System.out.println("  [!] Opcion invalida. Por favor, seleccione una opcion valida.");
        }
    }
    
    /**
     * Solicita la matricula profesional.
     * @return La matricula ingresada o null si el usuario cancela
     */
    private Integer solicitarMatricula() {
        while (true) {
            System.out.print("Matricula Profesional * (numero mayor a 1000, o 'cancelar' para volver): ");
            String entrada = scanner.nextLine().trim();
            
            if ("cancelar".equalsIgnoreCase(entrada)) {
                System.out.println("[!] Operacion cancelada.");
                return null;
            }
            
            try {
                Integer matricula = Integer.parseInt(entrada);
                if (matricula > 1000) {
                    return matricula;
                } else {
                    System.out.println("  [!] La matricula debe ser mayor a 1000.");
                }
            } catch (NumberFormatException e) {
                System.out.println("  [!] Debe ingresar un numero valido.");
            }
        }
    }
    
    /**
     * Solicita la fecha de ingreso.
     * @return La fecha ingresada o null si el usuario cancela
     */
    private LocalDate solicitarFecha() {
        LocalDate fechaMinima = LocalDate.of(1950, 1, 1);
        LocalDate fechaMaxima = LocalDate.now();
        
        System.out.println("Fecha de Ingreso * (formato DD/MM/AAAA)");
        System.out.println("  Rango valido: 01/01/1950 hasta hoy");
        
        while (true) {
            System.out.print("Fecha (o 'cancelar' para volver): ");
            String entrada = scanner.nextLine().trim();
            
            if ("cancelar".equalsIgnoreCase(entrada)) {
                System.out.println("[!] Operacion cancelada.");
                return null;
            }
            
            try {
                // Intentar parsear la fecha
                LocalDate fecha = LocalDate.parse(entrada, FORMATO_FECHA);
                
                // Validar el rango
                if (fecha.isBefore(fechaMinima)) {
                    System.out.println("  [!] La fecha no puede ser anterior a 1950.");
                } else if (fecha.isAfter(fechaMaxima)) {
                    System.out.println("  [!] La fecha no puede ser futura.");
                } else {
                    return fecha;
                }
            } catch (DateTimeParseException e) {
                System.out.println("  [!] Formato de fecha invalido. Use DD/MM/AAAA (ej: 15/03/2020)");
            }
        }
    }
    
    /**
     * Solicita el estado activo/inactivo del trabajador.
     * @return true si esta activo, false si esta inactivo
     */
    private boolean solicitarEstadoActivo() {
        System.out.println("Estado del trabajador:");
        System.out.println("  1. Activo (por defecto)");
        System.out.println("  2. Inactivo");
        
        System.out.print("\nSeleccione una opcion (1 o 2) [1]: ");
        String entrada = scanner.nextLine().trim();
        
        // Si presiona enter o selecciona 1, es activo
        return !"2".equals(entrada);
    }
    
    /**
     * Lista todos los trabajadores registrados en el sistema.
     * 
     * Este metodo:
     * 1. Llama al servicio remoto para obtener todos los trabajadores
     * 2. Formatea y muestra la informacion de cada trabajador
     * 3. Muestra un resumen al final
     */
    private void listarTrabajadores() {
        System.out.println("\n========================================");
        System.out.println("       LISTA DE TRABAJADORES");
        System.out.println("========================================\n");
        
        try {
            // Obtener la lista de trabajadores desde el servicio remoto
            // Usamos el servicio de negocio, no el DAO directamente
            // Esto respeta la arquitectura de capas
            List<TrabajadorSalud> trabajadores = servicioRemoto.obtenerTodos();
            
            // Verificar si hay trabajadores registrados
            if (trabajadores == null || trabajadores.isEmpty()) {
                System.out.println("[!] No hay trabajadores registrados en el sistema.");
                return;
            }
            
            // Mostrar el total de trabajadores
            System.out.println("Total de trabajadores: " + trabajadores.size());
            System.out.println("----------------------------------------\n");
            
            // Variable para contar trabajadores activos
            int contadorActivos = 0;
            
            // Iterar sobre cada trabajador y mostrar su informacion
            for (int i = 0; i < trabajadores.size(); i++) {
                TrabajadorSalud trabajador = trabajadores.get(i);
                
                // Mostrar numero de registro
                System.out.println("[" + (i + 1) + "] TRABAJADOR #" + (i + 1));
                System.out.println("----------------------------------------");
                
                // Mostrar informacion detallada del trabajador
                mostrarDetallesTrabajador(trabajador);
                
                // Contar trabajadores activos
                if (trabajador.isActivo()) {
                    contadorActivos++;
                }
                
                // Separador entre trabajadores (excepto el ultimo)
                if (i < trabajadores.size() - 1) {
                    System.out.println();
                }
            }
            
            // Mostrar resumen
            System.out.println("\n========================================");
            System.out.println("RESUMEN:");
            System.out.println("  - Total de trabajadores: " + trabajadores.size());
            System.out.println("  - Trabajadores activos: " + contadorActivos);
            System.out.println("  - Trabajadores inactivos: " + (trabajadores.size() - contadorActivos));
            System.out.println("========================================");
            
        } catch (Exception e) {
            System.err.println("\n[ERROR] Error al obtener la lista de trabajadores: " + e.getMessage());
            System.err.println("Verifique la conexion con el servidor.");
        }
    }
    
    /**
     * Metodo auxiliar para mostrar los detalles de un trabajador.
     * Centraliza el formato de presentacion de la informacion.
     * 
     * @param trabajador El trabajador cuyos detalles se mostraran
     */
    private void mostrarDetallesTrabajador(TrabajadorSalud trabajador) {
        // Informacion basica
        System.out.println("  Cedula: " + trabajador.getCedula());
        System.out.println("  Nombre: " + trabajador.getNombre() + " " + trabajador.getApellido());
        System.out.println("  Especialidad: " + trabajador.getEspecialidad());
        System.out.println("  Matricula Prof.: " + trabajador.getMatriculaProfesional());
        
        // Formatear y mostrar la fecha de ingreso
        if (trabajador.getFechaIngreso() != null) {
            String fechaFormateada = trabajador.getFechaIngreso().format(FORMATO_FECHA);
            System.out.println("  Fecha de Ingreso: " + fechaFormateada);
        } else {
            System.out.println("  Fecha de Ingreso: No especificada");
        }
        
        // Estado del trabajador con indicador visual
        String estado = trabajador.isActivo() ? "[ACTIVO]" : "[INACTIVO]";
        System.out.println("  Estado: " + estado);
    }
    
    /**
     * Busca un trabajador especifico por su numero de cedula.
     * 
     * Este metodo:
     * 1. Solicita al usuario el numero de cedula
     * 2. Valida el formato de la cedula
     * 3. Busca el trabajador usando el servicio remoto
     * 4. Muestra los resultados o un mensaje si no se encuentra
     */
    private void buscarPorCedula() {
        System.out.println("\n========================================");
        System.out.println("      BUSCAR TRABAJADOR POR CEDULA");
        System.out.println("========================================\n");
        
        try {
            // Solicitar la cedula al usuario
            System.out.print("Ingrese el numero de cedula a buscar: ");
            String cedula = scanner.nextLine().trim();
            
            // Validar que se ingreso una cedula
            if (cedula.isEmpty()) {
                System.out.println("\n[!] Debe ingresar un numero de cedula.");
                return;
            }
            
            // Validar formato basico de la cedula (solo numeros, 7-10 digitos)
            if (!cedula.matches("\\d{7,10}")) {
                System.out.println("\n[!] Formato de cedula invalido.");
                System.out.println("  La cedula debe contener entre 7 y 10 digitos numericos.");
                return;
            }
            
            // Mostrar mensaje de busqueda
            System.out.println("\nBuscando trabajador con cedula: " + cedula + "...");
            
            // Buscar el trabajador usando el servicio remoto
            TrabajadorSalud trabajador = servicioRemoto.buscarPorCedula(cedula);
            
            // Verificar si se encontro el trabajador
            if (trabajador == null) {
                System.out.println("\n[X] No se encontro ningun trabajador con la cedula: " + cedula);
                
                // Ofrecer opciones al usuario
                System.out.println("\nOpciones:");
                System.out.println("  1. Intentar con otra cedula");
                System.out.println("  2. Ver lista de todos los trabajadores");
                System.out.println("  0. Volver al menu principal");
                
                System.out.print("\nSeleccione una opcion: ");
                String opcion = scanner.nextLine().trim();
                
                switch (opcion) {
                    case "1":
                        buscarPorCedula(); // Llamada recursiva para intentar otra busqueda
                        break;
                    case "2":
                        listarTrabajadores();
                        break;
                    // case "0" o cualquier otra opcion vuelve al menu principal
                }
                
            } else {
                // Trabajador encontrado - mostrar sus detalles
                System.out.println("\n[OK] TRABAJADOR ENCONTRADO");
                System.out.println("========================================");
                mostrarDetallesTrabajador(trabajador);
                System.out.println("========================================");
                
                // Mostrar opciones adicionales
                System.out.println("\nOpciones:");
                System.out.println("  1. Buscar otro trabajador");
                System.out.println("  0. Volver al menu principal");
                
                System.out.print("\nSeleccione una opcion: ");
                String opcion = scanner.nextLine().trim();
                
                if ("1".equals(opcion)) {
                    buscarPorCedula(); // Llamada recursiva para otra busqueda
                }
            }
            
        } catch (Exception e) {
            System.err.println("\n[ERROR] Error al buscar el trabajador: " + e.getMessage());
            System.err.println("Verifique la conexion con el servidor.");
        }
    }
    
    /**
     * Busca trabajadores por especialidad.
     * 
     * Este metodo:
     * 1. Muestra las especialidades disponibles
     * 2. Permite al usuario seleccionar o ingresar una especialidad
     * 3. Busca todos los trabajadores con esa especialidad
     * 4. Muestra los resultados encontrados
     */
    private void buscarPorEspecialidad() {
        System.out.println("\n========================================");
        System.out.println("    BUSCAR TRABAJADORES POR ESPECIALIDAD");
        System.out.println("========================================\n");
        
        try {
            // Reutilizamos el metodo solicitarEspecialidad ya creado
            String especialidad = solicitarEspecialidad();
            
            if (especialidad == null) {
                return; // El usuario cancelo
            }
            
            System.out.println("\nBuscando trabajadores con especialidad: " + especialidad + "...");
            
            // Buscar usando el servicio remoto
            List<TrabajadorSalud> trabajadores = servicioRemoto.buscarPorEspecialidad(especialidad);
            
            if (trabajadores == null || trabajadores.isEmpty()) {
                System.out.println("\n[X] No se encontraron trabajadores con la especialidad: " + especialidad);
                
                // Ofrecer opciones
                System.out.println("\nOpciones:");
                System.out.println("  1. Buscar otra especialidad");
                System.out.println("  2. Ver todas las especialidades (listar todos)");
                System.out.println("  0. Volver al menu principal");
                
                System.out.print("\nSeleccione una opcion: ");
                String opcion = scanner.nextLine().trim();
                
                switch (opcion) {
                    case "1":
                        buscarPorEspecialidad(); // Buscar otra especialidad
                        break;
                    case "2":
                        listarTrabajadores(); // Ver todos
                        break;
                }
                
            } else {
                // Mostrar resultados encontrados
                System.out.println("\n[OK] Se encontraron " + trabajadores.size() + 
                                 " trabajador(es) con especialidad: " + especialidad);
                System.out.println("========================================\n");
                
                // Mostrar cada trabajador encontrado
                for (int i = 0; i < trabajadores.size(); i++) {
                    System.out.println("[" + (i + 1) + "] TRABAJADOR #" + (i + 1));
                    System.out.println("----------------------------------------");
                    mostrarDetallesTrabajador(trabajadores.get(i));
                    
                    if (i < trabajadores.size() - 1) {
                        System.out.println();
                    }
                }
                
                System.out.println("\n========================================");
                
                // Contar activos/inactivos en los resultados
                long activos = trabajadores.stream().filter(TrabajadorSalud::isActivo).count();
                System.out.println("RESUMEN DE RESULTADOS:");
                System.out.println("  - Total encontrados: " + trabajadores.size());
                System.out.println("  - Activos: " + activos);
                System.out.println("  - Inactivos: " + (trabajadores.size() - activos));
                System.out.println("========================================");
            }
            
        } catch (Exception e) {
            System.err.println("\n[ERROR] Error al buscar por especialidad: " + e.getMessage());
            System.err.println("Verifique la conexion con el servidor.");
        }
    }
    
    /**
     * Muestra las estadisticas del sistema.
     * 
     * Este metodo:
     * 1. Obtiene las estadisticas desde el servicio remoto
     * 2. Muestra un dashboard con informacion resumida del sistema
     * 3. Incluye totales, porcentajes y datos relevantes
     */
    private void verEstadisticas() {
        System.out.println("\n========================================");
        System.out.println("      ESTADISTICAS DEL SISTEMA");
        System.out.println("========================================\n");
        
        try {
            // Obtener estadisticas del servicio remoto
            com.ejercicio1.business.EstadisticasSistema stats = servicioRemoto.obtenerEstadisticas();
            
            if (stats == null) {
                System.out.println("[!] No se pudieron obtener las estadisticas.");
                return;
            }
            
            // Mostrar dashboard de estadisticas
            System.out.println("RESUMEN GENERAL");
            System.out.println("----------------------------------------");
            
            // Total de trabajadores
            System.out.println("- Total de trabajadores: " + stats.getTotalTrabajadores());
            
            // Trabajadores activos/inactivos
            System.out.println("- Trabajadores activos: " + stats.getTrabajadoresActivos());
            System.out.println("- Trabajadores inactivos: " + stats.getTrabajadoresInactivos());
            
            // Porcentaje de activos
            System.out.printf("- Porcentaje de activos: %.1f%%\n", stats.getPorcentajeActivos());
            
            // Especialidad mas comun
            System.out.println("- Especialidad mas comun: " + stats.getEspecialidadMasComun());
            
            System.out.println("\nANALISIS");
            System.out.println("----------------------------------------");
            
            // Analisis del estado del sistema
            if (stats.getTotalTrabajadores() == 0) {
                System.out.println("[!] El sistema no tiene trabajadores registrados.");
                System.out.println("  Considere agregar trabajadores al sistema.");
            } else {
                // Indicadores de salud del sistema
                double porcentajeActivos = stats.getPorcentajeActivos();
                
                if (porcentajeActivos >= 80) {
                    System.out.println("[OK] Excelente: Alta tasa de trabajadores activos (" + 
                                     String.format("%.1f%%", porcentajeActivos) + ")");
                } else if (porcentajeActivos >= 60) {
                    System.out.println("[!] Bueno: Tasa moderada de trabajadores activos (" + 
                                     String.format("%.1f%%", porcentajeActivos) + ")");
                } else {
                    System.out.println("[X] Atencion: Baja tasa de trabajadores activos (" + 
                                     String.format("%.1f%%", porcentajeActivos) + ")");
                }
                
                // Informacion sobre la especialidad dominante
                if (!"N/A".equals(stats.getEspecialidadMasComun())) {
                    System.out.println("- La especialidad \"" + stats.getEspecialidadMasComun() + 
                                     "\" es la mas frecuente en el sistema.");
                }
            }
            
            // Tambien podemos obtener datos directamente del DAO remoto
            System.out.println("\nDATOS EN TIEMPO REAL");
            System.out.println("----------------------------------------");
            int totalDAO = daoRemoto.contarTrabajadores();
            int activosDAO = daoRemoto.contarTrabajadoresActivos();
            System.out.println("- Trabajadores en memoria (DAO): " + totalDAO);
            System.out.println("- Trabajadores activos (DAO): " + activosDAO);
            
            // Verificar consistencia entre servicio y DAO
            if (totalDAO == stats.getTotalTrabajadores()) {
                System.out.println("[OK] Datos consistentes entre capas");
            } else {
                System.out.println("[!] Discrepancia detectada entre capas");
            }
            
            System.out.println("\n========================================");
            
        } catch (Exception e) {
            System.err.println("\n[ERROR] Error al obtener estadisticas: " + e.getMessage());
            System.err.println("Verifique la conexion con el servidor.");
        }
    }
}
