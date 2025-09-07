package com.ejercicio1.business;

import com.ejercicio1.dao.TrabajadorSaludDAOLocal;
import com.ejercicio1.entities.TrabajadorSalud;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test automatizado para la funcionalidad de listar trabajadores.
 * 
 * Este test verifica:
 * 1. Que el servicio devuelve correctamente la lista de trabajadores
 * 2. Que maneja correctamente listas vacías
 * 3. Que filtra correctamente por especialidad
 * 4. Que calcula correctamente las estadísticas
 * 
 * @author Sistema de Gestión de Identidades
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para Listar Trabajadores")
public class ListarTrabajadoresTest {
    
    // Mock del DAO - simularemos las respuestas del DAO
    @Mock
    private TrabajadorSaludDAOLocal trabajadorDAO;
    
    // Servicio bajo prueba - inyectamos el mock del DAO
    @InjectMocks
    private TrabajadorSaludService trabajadorService;
    
    // Datos de prueba
    private List<TrabajadorSalud> trabajadoresPrueba;
    private TrabajadorSalud trabajador1;
    private TrabajadorSalud trabajador2;
    private TrabajadorSalud trabajador3;
    
    /**
     * Configuración inicial antes de cada test.
     * Crea los datos de prueba que usaremos en los tests.
     */
    @BeforeEach
    void setUp() {
        // Crear trabajadores de prueba con datos válidos
        trabajador1 = new TrabajadorSalud(
            "12345678",           // Cédula
            "Juan",               // Nombre
            "Pérez",              // Apellido
            "Cardiología",        // Especialidad
            1500,                 // Matrícula profesional
            LocalDate.of(2020, 1, 15),  // Fecha de ingreso
            true                  // Activo
        );
        
        trabajador2 = new TrabajadorSalud(
            "87654321",
            "María",
            "González",
            "Pediatría",
            2000,
            LocalDate.of(2019, 6, 20),
            true
        );
        
        trabajador3 = new TrabajadorSalud(
            "11111111",
            "Carlos",
            "Rodríguez",
            "Cardiología",        // Misma especialidad que trabajador1
            3000,
            LocalDate.of(2018, 3, 10),
            false                 // Inactivo
        );
        
        // Lista con todos los trabajadores de prueba
        trabajadoresPrueba = new ArrayList<>(Arrays.asList(
            trabajador1, trabajador2, trabajador3
        ));
    }
    
    /**
     * Test 1: Verificar que se obtienen todos los trabajadores correctamente.
     */
    @Test
    @DisplayName("Debe obtener todos los trabajadores cuando existen en el sistema")
    void testObtenerTodosCuandoHayTrabajadores() {
        // GIVEN (Dado): El DAO devuelve una lista con 3 trabajadores
        when(trabajadorDAO.obtenerTodos()).thenReturn(trabajadoresPrueba);
        
        // WHEN (Cuando): Llamamos al método obtenerTodos del servicio
        List<TrabajadorSalud> resultado = trabajadorService.obtenerTodos();
        
        // THEN (Entonces): Verificamos los resultados
        assertNotNull(resultado, "La lista no debe ser null");
        assertEquals(3, resultado.size(), "Debe devolver 3 trabajadores");
        assertTrue(resultado.contains(trabajador1), "Debe contener al trabajador1");
        assertTrue(resultado.contains(trabajador2), "Debe contener al trabajador2");
        assertTrue(resultado.contains(trabajador3), "Debe contener al trabajador3");
        
        // Verificar que el DAO fue llamado exactamente una vez
        verify(trabajadorDAO, times(1)).obtenerTodos();
    }
    
    /**
     * Test 2: Verificar el comportamiento cuando no hay trabajadores.
     */
    @Test
    @DisplayName("Debe devolver lista vacía cuando no hay trabajadores")
    void testObtenerTodosCuandoNoHayTrabajadores() {
        // GIVEN: El DAO devuelve una lista vacía
        when(trabajadorDAO.obtenerTodos()).thenReturn(new ArrayList<>());
        
        // WHEN: Llamamos al método obtenerTodos
        List<TrabajadorSalud> resultado = trabajadorService.obtenerTodos();
        
        // THEN: Verificamos que la lista está vacía pero no es null
        assertNotNull(resultado, "La lista no debe ser null");
        assertTrue(resultado.isEmpty(), "La lista debe estar vacía");
        assertEquals(0, resultado.size(), "El tamaño debe ser 0");
        
        // Verificar que el DAO fue llamado
        verify(trabajadorDAO, times(1)).obtenerTodos();
    }
    
    /**
     * Test 3: Verificar la búsqueda por especialidad.
     */
    @Test
    @DisplayName("Debe filtrar correctamente por especialidad")
    void testBuscarPorEspecialidad() {
        // GIVEN: El DAO devuelve todos los trabajadores
        when(trabajadorDAO.obtenerTodos()).thenReturn(trabajadoresPrueba);
        
        // WHEN: Buscamos trabajadores con especialidad "Cardiología"
        List<TrabajadorSalud> cardiologos = trabajadorService.buscarPorEspecialidad("Cardiología");
        
        // THEN: Debe devolver solo los 2 cardiólogos
        assertNotNull(cardiologos, "La lista no debe ser null");
        assertEquals(2, cardiologos.size(), "Debe haber 2 cardiólogos");
        assertTrue(cardiologos.contains(trabajador1), "Debe incluir a Juan Pérez");
        assertTrue(cardiologos.contains(trabajador3), "Debe incluir a Carlos Rodríguez");
        assertFalse(cardiologos.contains(trabajador2), "No debe incluir a María (Pediatría)");
    }
    
    /**
     * Test 4: Verificar búsqueda con especialidad inexistente.
     */
    @Test
    @DisplayName("Debe devolver lista vacía para especialidad inexistente")
    void testBuscarPorEspecialidadInexistente() {
        // GIVEN: El DAO devuelve todos los trabajadores
        when(trabajadorDAO.obtenerTodos()).thenReturn(trabajadoresPrueba);
        
        // WHEN: Buscamos una especialidad que no existe
        List<TrabajadorSalud> resultado = trabajadorService.buscarPorEspecialidad("Neurología");
        
        // THEN: Debe devolver lista vacía
        assertNotNull(resultado, "La lista no debe ser null");
        assertTrue(resultado.isEmpty(), "La lista debe estar vacía");
    }
    
    /**
     * Test 5: Verificar que la búsqueda por especialidad es case-insensitive.
     */
    @Test
    @DisplayName("Debe buscar especialidad sin importar mayúsculas/minúsculas")
    void testBuscarPorEspecialidadCaseInsensitive() {
        // GIVEN: El DAO devuelve todos los trabajadores
        when(trabajadorDAO.obtenerTodos()).thenReturn(trabajadoresPrueba);
        
        // WHEN: Buscamos con diferentes combinaciones de mayúsculas
        List<TrabajadorSalud> resultado1 = trabajadorService.buscarPorEspecialidad("PEDIATRÍA");
        List<TrabajadorSalud> resultado2 = trabajadorService.buscarPorEspecialidad("pediatría");
        List<TrabajadorSalud> resultado3 = trabajadorService.buscarPorEspecialidad("PeDiAtRíA");
        
        // THEN: Todos deben encontrar el mismo trabajador
        assertEquals(1, resultado1.size(), "Debe encontrar 1 pediatra con MAYÚSCULAS");
        assertEquals(1, resultado2.size(), "Debe encontrar 1 pediatra con minúsculas");
        assertEquals(1, resultado3.size(), "Debe encontrar 1 pediatra con MiXtO");
        assertTrue(resultado1.contains(trabajador2), "Debe ser María González");
    }
    
    /**
     * Test 6: Verificar el cálculo de estadísticas.
     */
    @Test
    @DisplayName("Debe calcular correctamente las estadísticas del sistema")
    void testObtenerEstadisticas() {
        // GIVEN: Configuramos el mock para devolver los datos necesarios
        when(trabajadorDAO.obtenerTodos()).thenReturn(trabajadoresPrueba);
        when(trabajadorDAO.contarTrabajadoresActivos()).thenReturn(2); // Juan y María están activos
        
        // WHEN: Obtenemos las estadísticas
        EstadisticasSistema stats = trabajadorService.obtenerEstadisticas();
        
        // THEN: Verificamos que las estadísticas son correctas
        assertNotNull(stats, "Las estadísticas no deben ser null");
        assertEquals(3, stats.getTotalTrabajadores(), "Total debe ser 3");
        assertEquals(2, stats.getTrabajadoresActivos(), "Activos debe ser 2");
        assertEquals(1, stats.getTrabajadoresInactivos(), "Inactivos debe ser 1");
        assertEquals(66.67, stats.getPorcentajeActivos(), 0.01, "Porcentaje activos debe ser ~66.67%");
        assertEquals("Cardiología", stats.getEspecialidadMasComun(), 
                    "Cardiología debe ser la especialidad más común (2 trabajadores)");
    }
    
    /**
     * Test 7: Verificar estadísticas con sistema vacío.
     */
    @Test
    @DisplayName("Debe manejar estadísticas cuando no hay trabajadores")
    void testObtenerEstadisticasSinTrabajadores() {
        // GIVEN: El sistema no tiene trabajadores
        when(trabajadorDAO.obtenerTodos()).thenReturn(new ArrayList<>());
        when(trabajadorDAO.contarTrabajadoresActivos()).thenReturn(0);
        
        // WHEN: Obtenemos las estadísticas
        EstadisticasSistema stats = trabajadorService.obtenerEstadisticas();
        
        // THEN: Verificamos valores por defecto
        assertNotNull(stats, "Las estadísticas no deben ser null");
        assertEquals(0, stats.getTotalTrabajadores(), "Total debe ser 0");
        assertEquals(0, stats.getTrabajadoresActivos(), "Activos debe ser 0");
        assertEquals(0, stats.getTrabajadoresInactivos(), "Inactivos debe ser 0");
        assertEquals(0.0, stats.getPorcentajeActivos(), "Porcentaje debe ser 0%");
        assertEquals("N/A", stats.getEspecialidadMasComun(), "Especialidad debe ser N/A");
    }
    
    /**
     * Test 8: Verificar que el servicio maneja nulls del DAO correctamente.
     */
    @Test
    @DisplayName("Debe manejar respuesta null del DAO")
    void testManejarNullDelDAO() {
        // GIVEN: El DAO devuelve null (caso extremo)
        when(trabajadorDAO.obtenerTodos()).thenReturn(null);
        
        // WHEN: Intentamos buscar por especialidad
        List<TrabajadorSalud> resultado = trabajadorService.buscarPorEspecialidad("Cardiología");
        
        // THEN: Debe devolver lista vacía, no lanzar excepción
        assertNotNull(resultado, "Debe devolver lista vacía, no null");
        assertTrue(resultado.isEmpty(), "La lista debe estar vacía");
    }
    
    /**
     * Test 9: Test de integración - flujo completo de listado.
     */
    @Test
    @DisplayName("Test de integración: flujo completo de listado")
    void testFlujoCompletoListado() {
        // GIVEN: Sistema con trabajadores
        when(trabajadorDAO.obtenerTodos()).thenReturn(trabajadoresPrueba);
        when(trabajadorDAO.contarTrabajadoresActivos()).thenReturn(2);
        
        // WHEN: Ejecutamos varias operaciones de listado
        List<TrabajadorSalud> todos = trabajadorService.obtenerTodos();
        List<TrabajadorSalud> cardiologos = trabajadorService.buscarPorEspecialidad("Cardiología");
        List<TrabajadorSalud> pediatras = trabajadorService.buscarPorEspecialidad("Pediatría");
        EstadisticasSistema stats = trabajadorService.obtenerEstadisticas();
        
        // THEN: Verificamos consistencia en los resultados
        assertEquals(3, todos.size(), "Total debe ser 3");
        assertEquals(2, cardiologos.size(), "Debe haber 2 cardiólogos");
        assertEquals(1, pediatras.size(), "Debe haber 1 pediatra");
        assertEquals(todos.size(), stats.getTotalTrabajadores(), 
                    "Estadísticas deben coincidir con total");
        
        // Verificar interacciones con el mock
        verify(trabajadorDAO, atLeast(3)).obtenerTodos();
        verify(trabajadorDAO, times(1)).contarTrabajadoresActivos();
    }
}
