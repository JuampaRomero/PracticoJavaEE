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

public class ClienteRemoto {
    
    private TrabajadorSaludServiceRemote servicioRemoto;
    private TrabajadorSaludDAORemote daoRemoto;
    
    private Scanner scanner;
    
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public ClienteRemoto() {
        this.scanner = new Scanner(System.in);
    }
    
    // Configuracion JNDI importante para conectar con WildFly
    public void conectar() throws NamingException {
        System.out.println("========================================");
        System.out.println("Iniciando conexion con el servidor...");
        System.out.println("========================================\n");
        
        Properties props = new Properties();
        
        props.put(Context.INITIAL_CONTEXT_FACTORY, 
                  "org.wildfly.naming.client.WildFlyInitialContextFactory");
        
        props.put(Context.PROVIDER_URL, "remote+http://localhost:8080");
        
        // props.put(Context.SECURITY_PRINCIPAL, "usuario");
        // props.put(Context.SECURITY_CREDENTIALS, "password");
        
        try {
            Context contexto = new InitialContext(props);
            
            // Patrones JNDI para WildFly:
            // ejb:/[war-name]/[bean-name]![interface]
            // java:global/[app-name]/[bean-name]![interface]
            
            System.out.println("Buscando EJB de servicio de negocio...");
            
            String jndiServicio = "ejb:/GestorIdentidades-1.0-SNAPSHOT/TrabajadorSaludService!" + 
                                  "com.ejercicio1.business.TrabajadorSaludServiceRemote";
            
            try {
                servicioRemoto = (TrabajadorSaludServiceRemote) contexto.lookup(jndiServicio);
                System.out.println("  [OK] Servicio encontrado con patron ejb:/");
            } catch (NamingException e) {
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
                System.out.println("  [!] No encontrado con patron ejb:/, intentando con java:global/");
                jndiDAO = "java:global/GestorIdentidades-1.0-SNAPSHOT/TrabajadorSaludDAO!" + 
                         "com.ejercicio1.dao.TrabajadorSaludDAORemote";
                daoRemoto = (TrabajadorSaludDAORemote) contexto.lookup(jndiDAO);
                System.out.println("  [OK] DAO encontrado con patron java:global/");
            }
            
            System.out.println("\n[OK] Conexion establecida exitosamente");
            System.out.println("[OK] EJBs remotos disponibles\n");
            
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
    
    public static void main(String[] args) {
        ClienteRemoto cliente = new ClienteRemoto();
        
        try {
            cliente.conectar();
            
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
    
    private void ejecutarMenuPrincipal() {
        boolean continuar = true;
        
        while (continuar) {
            try {
                mostrarMenuPrincipal();
                
                System.out.print("\nSeleccione una opcion: ");
                String opcion = scanner.nextLine().trim();
                
                switch (opcion) {
                    case "1":
                        agregarTrabajador();
                        break;
                        
                    case "2":
                        listarTrabajadores();
                        break;
                        
                    case "3":
                        buscarPorCedula();
                        break;
                        
                    case "4":
                        buscarPorEspecialidad();
                        break;
                        
                    case "5":
                        verEstadisticas();
                        break;
                        
                    case "0":
                        System.out.println("\n========================================");
                        System.out.println("Gracias por usar el Sistema de Gestion");
                        System.out.println("Cerrando conexion...");
                        System.out.println("========================================\n");
                        continuar = false;
                        break;
                        
                    default:
                        System.out.println("\n[!] Opcion no valida. Por favor, intente nuevamente.");
                }
                
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
    
    private void agregarTrabajador() {
        System.out.println("\n========================================");
        System.out.println("       AGREGAR NUEVO TRABAJADOR");
        System.out.println("========================================\n");
        System.out.println("Complete los siguientes datos:");
        System.out.println("(Los campos marcados con * son obligatorios)\n");
        
        try {
            String cedula, nombre, apellido, especialidad;
            Integer matriculaProfesional;
            LocalDate fechaIngreso;
            boolean activo;
            
            System.out.println("----------------------------------------");
            cedula = solicitarCedula();
            if (cedula == null) return;
            
            System.out.println("----------------------------------------");
            nombre = solicitarTexto("Nombre *", 2, 50);
            if (nombre == null) return;
            
            System.out.println("----------------------------------------");
            apellido = solicitarTexto("Apellido *", 2, 50);
            if (apellido == null) return;
            
            System.out.println("----------------------------------------");
            especialidad = solicitarEspecialidad();
            if (especialidad == null) return;
            
            System.out.println("----------------------------------------");
            matriculaProfesional = solicitarMatricula();
            if (matriculaProfesional == null) return;
            
            System.out.println("----------------------------------------");
            fechaIngreso = solicitarFecha();
            if (fechaIngreso == null) return;
            
            System.out.println("----------------------------------------");
            activo = solicitarEstadoActivo();
            
            TrabajadorSalud nuevoTrabajador = new TrabajadorSalud(
                cedula, nombre, apellido, especialidad,
                matriculaProfesional, fechaIngreso, activo
            );
            
            System.out.println("\n========================================");
            System.out.println("RESUMEN DEL NUEVO TRABAJADOR:");
            System.out.println("========================================");
            mostrarDetallesTrabajador(nuevoTrabajador);
            System.out.println("========================================");
            
            System.out.print("\nDesea guardar este trabajador? (S/N): ");
            String confirmacion = scanner.nextLine().trim().toUpperCase();
            
            if ("S".equals(confirmacion) || "SI".equals(confirmacion)) {
                System.out.println("\nGuardando trabajador...");
                servicioRemoto.agregarTrabajador(nuevoTrabajador);
                
                System.out.println("\n[OK] Trabajador agregado exitosamente!");
                System.out.println("  Cedula: " + cedula);
                System.out.println("  Nombre: " + nombre + " " + apellido);
                
            } else {
                System.out.println("\n[!] Operacion cancelada. No se guardo el trabajador.");
            }
            
        } catch (com.ejercicio1.business.BusinessException e) {
            System.err.println("\n[ERROR] Error de validacion: " + e.getMessage());
            System.out.println("\nEl trabajador no pudo ser agregado debido a las reglas de negocio.");
            System.out.println("Por favor, verifique los datos e intente nuevamente.");
            
        } catch (Exception e) {
            System.err.println("\n[ERROR] Error al agregar el trabajador: " + e.getMessage());
            System.err.println("Verifique la conexion con el servidor.");
        }
    }
    
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
    
    private String solicitarEspecialidad() {
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
            }
            
            System.out.println("  [!] Opcion invalida. Por favor, seleccione una opcion valida.");
        }
    }
    
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
                LocalDate fecha = LocalDate.parse(entrada, FORMATO_FECHA);
                
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
    
    private boolean solicitarEstadoActivo() {
        System.out.println("Estado del trabajador:");
        System.out.println("  1. Activo (por defecto)");
        System.out.println("  2. Inactivo");
        
        System.out.print("\nSeleccione una opcion (1 o 2) [1]: ");
        String entrada = scanner.nextLine().trim();
        
        return !"2".equals(entrada);
    }
    
    private void listarTrabajadores() {
        System.out.println("\n========================================");
        System.out.println("       LISTA DE TRABAJADORES");
        System.out.println("========================================\n");
        
        try {
            List<TrabajadorSalud> trabajadores = servicioRemoto.obtenerTodos();
            
            if (trabajadores == null || trabajadores.isEmpty()) {
                System.out.println("[!] No hay trabajadores registrados en el sistema.");
                return;
            }
            
            System.out.println("Total de trabajadores: " + trabajadores.size());
            System.out.println("----------------------------------------\n");
            
            int contadorActivos = 0;
            
            for (int i = 0; i < trabajadores.size(); i++) {
                TrabajadorSalud trabajador = trabajadores.get(i);
                
                System.out.println("[" + (i + 1) + "] TRABAJADOR #" + (i + 1));
                System.out.println("----------------------------------------");
                
                mostrarDetallesTrabajador(trabajador);
                
                if (trabajador.isActivo()) {
                    contadorActivos++;
                }
                
                if (i < trabajadores.size() - 1) {
                    System.out.println();
                }
            }
            
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
    
    private void mostrarDetallesTrabajador(TrabajadorSalud trabajador) {
        System.out.println("  Cedula: " + trabajador.getCedula());
        System.out.println("  Nombre: " + trabajador.getNombre() + " " + trabajador.getApellido());
        System.out.println("  Especialidad: " + trabajador.getEspecialidad());
        System.out.println("  Matricula Prof.: " + trabajador.getMatriculaProfesional());
        
        if (trabajador.getFechaIngreso() != null) {
            String fechaFormateada = trabajador.getFechaIngreso().format(FORMATO_FECHA);
            System.out.println("  Fecha de Ingreso: " + fechaFormateada);
        } else {
            System.out.println("  Fecha de Ingreso: No especificada");
        }
        
        String estado = trabajador.isActivo() ? "[ACTIVO]" : "[INACTIVO]";
        System.out.println("  Estado: " + estado);
    }
    
    private void buscarPorCedula() {
        System.out.println("\n========================================");
        System.out.println("      BUSCAR TRABAJADOR POR CEDULA");
        System.out.println("========================================\n");
        
        try {
            System.out.print("Ingrese el numero de cedula a buscar: ");
            String cedula = scanner.nextLine().trim();
            
            if (cedula.isEmpty()) {
                System.out.println("\n[!] Debe ingresar un numero de cedula.");
                return;
            }
            
            if (!cedula.matches("\\d{7,10}")) {
                System.out.println("\n[!] Formato de cedula invalido.");
                System.out.println("  La cedula debe contener entre 7 y 10 digitos numericos.");
                return;
            }
            
            System.out.println("\nBuscando trabajador con cedula: " + cedula + "...");
            
            TrabajadorSalud trabajador = servicioRemoto.buscarPorCedula(cedula);
            
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
                        buscarPorCedula();
                        break;
                    case "2":
                        listarTrabajadores();
                        break;
                }
                
            } else {
                System.out.println("\n[OK] TRABAJADOR ENCONTRADO");
                System.out.println("========================================");
                mostrarDetallesTrabajador(trabajador);
                System.out.println("========================================");
                
                System.out.println("\nOpciones:");
                System.out.println("  1. Buscar otro trabajador");
                System.out.println("  0. Volver al menu principal");
                
                System.out.print("\nSeleccione una opcion: ");
                String opcion = scanner.nextLine().trim();
                
                if ("1".equals(opcion)) {
                    buscarPorCedula();
                }
            }
            
        } catch (Exception e) {
            System.err.println("\n[ERROR] Error al buscar el trabajador: " + e.getMessage());
            System.err.println("Verifique la conexion con el servidor.");
        }
    }
    
    private void buscarPorEspecialidad() {
        System.out.println("\n========================================");
        System.out.println("    BUSCAR TRABAJADORES POR ESPECIALIDAD");
        System.out.println("========================================\n");
        
        try {
            String especialidad = solicitarEspecialidad();
            
            if (especialidad == null) {
                return;
            }
            
            System.out.println("\nBuscando trabajadores con especialidad: " + especialidad + "...");
            
            List<TrabajadorSalud> trabajadores = servicioRemoto.buscarPorEspecialidad(especialidad);
            
            if (trabajadores == null || trabajadores.isEmpty()) {
                System.out.println("\n[X] No se encontraron trabajadores con la especialidad: " + especialidad);
                
                System.out.println("\nOpciones:");
                System.out.println("  1. Buscar otra especialidad");
                System.out.println("  2. Ver todas las especialidades (listar todos)");
                System.out.println("  0. Volver al menu principal");
                
                System.out.print("\nSeleccione una opcion: ");
                String opcion = scanner.nextLine().trim();
                
                switch (opcion) {
                    case "1":
                        buscarPorEspecialidad();
                        break;
                    case "2":
                        listarTrabajadores();
                        break;
                }
                
            } else {
                System.out.println("\n[OK] Se encontraron " + trabajadores.size() + 
                                 " trabajador(es) con especialidad: " + especialidad);
                System.out.println("========================================\n");
                
                for (int i = 0; i < trabajadores.size(); i++) {
                    System.out.println("[" + (i + 1) + "] TRABAJADOR #" + (i + 1));
                    System.out.println("----------------------------------------");
                    mostrarDetallesTrabajador(trabajadores.get(i));
                    
                    if (i < trabajadores.size() - 1) {
                        System.out.println();
                    }
                }
                
                System.out.println("\n========================================");
                
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
    
    private void verEstadisticas() {
        System.out.println("\n========================================");
        System.out.println("      ESTADISTICAS DEL SISTEMA");
        System.out.println("========================================\n");
        
        try {
            com.ejercicio1.business.EstadisticasSistema stats = servicioRemoto.obtenerEstadisticas();
            
            if (stats == null) {
                System.out.println("[!] No se pudieron obtener las estadisticas.");
                return;
            }
            
            System.out.println("RESUMEN GENERAL");
            System.out.println("----------------------------------------");
            
            System.out.println("- Total de trabajadores: " + stats.getTotalTrabajadores());
            
            System.out.println("- Trabajadores activos: " + stats.getTrabajadoresActivos());
            System.out.println("- Trabajadores inactivos: " + stats.getTrabajadoresInactivos());
            
            System.out.printf("- Porcentaje de activos: %.1f%%\n", stats.getPorcentajeActivos());
            
            System.out.println("- Especialidad mas comun: " + stats.getEspecialidadMasComun());
            
            System.out.println("\nANALISIS");
            System.out.println("----------------------------------------");
            
            if (stats.getTotalTrabajadores() == 0) {
                System.out.println("[!] El sistema no tiene trabajadores registrados.");
                System.out.println("  Considere agregar trabajadores al sistema.");
            } else {
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
                
                if (!"N/A".equals(stats.getEspecialidadMasComun())) {
                    System.out.println("- La especialidad \"" + stats.getEspecialidadMasComun() + 
                                     "\" es la mas frecuente en el sistema.");
                }
            }
            
            System.out.println("\nDATOS EN TIEMPO REAL");
            System.out.println("----------------------------------------");
            int totalDAO = daoRemoto.contarTrabajadores();
            int activosDAO = daoRemoto.contarTrabajadoresActivos();
            System.out.println("- Trabajadores en memoria (DAO): " + totalDAO);
            System.out.println("- Trabajadores activos (DAO): " + activosDAO);
            
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
