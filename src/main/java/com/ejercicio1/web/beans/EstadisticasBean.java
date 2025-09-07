package com.ejercicio1.web.beans;

import com.ejercicio1.business.EstadisticasSistema;
import com.ejercicio1.business.TrabajadorSaludServiceLocal;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.donut.DonutChartDataSet;
import org.primefaces.model.charts.donut.DonutChartModel;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.optionconfig.title.Title;

import java.io.Serializable;
import java.util.*;

/**
 * Managed Bean para mostrar estadísticas del sistema
 */
@Named("estadisticasBean")
@RequestScoped
public class EstadisticasBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    private TrabajadorSaludServiceLocal trabajadorService;
    
    private EstadisticasSistema estadisticas;
    private DonutChartModel donutModel;
    private BarChartModel barModel;
    
    @PostConstruct
    public void init() {
        cargarEstadisticas();
        createDonutModel();
        createBarModel();
    }
    
    /**
     * Carga las estadísticas del sistema
     */
    private void cargarEstadisticas() {
        try {
            estadisticas = trabajadorService.obtenerEstadisticas();
        } catch (Exception e) {
            // Crear estadísticas vacías en caso de error
            estadisticas = new EstadisticasSistema();
            estadisticas.setTotalTrabajadores(0);
            estadisticas.setTrabajadoresActivos(0);
            estadisticas.setTrabajadoresInactivos(0);
            estadisticas.setTrabajadoresPorEspecialidad(new HashMap<>());
        }
    }
    
    /**
     * Crea el modelo para el gráfico de dona
     */
    private void createDonutModel() {
        donutModel = new DonutChartModel();
        ChartData data = new ChartData();
        
        DonutChartDataSet dataSet = new DonutChartDataSet();
        List<Number> values = new ArrayList<>();
        values.add(estadisticas.getTrabajadoresActivos());
        values.add(estadisticas.getTrabajadoresInactivos());
        dataSet.setData(values);
        
        List<String> labels = new ArrayList<>();
        labels.add("Activos");
        labels.add("Inactivos");
        
        List<String> bgColors = new ArrayList<>();
        bgColors.add("rgb(34, 197, 94)"); // Verde para activos
        bgColors.add("rgb(239, 68, 68)"); // Rojo para inactivos
        dataSet.setBackgroundColor(bgColors);
        
        data.addChartDataSet(dataSet);
        data.setLabels(labels);
        
        donutModel.setData(data);
    }
    
    /**
     * Crea el modelo para el gráfico de barras
     */
    private void createBarModel() {
        barModel = new BarChartModel();
        ChartData data = new ChartData();
        
        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Trabajadores por Especialidad");
        
        List<Number> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<String> bgColors = new ArrayList<>();
        
        // Colores para las barras
        String[] colores = {
            "rgba(59, 130, 246, 0.8)",  // Azul
            "rgba(34, 197, 94, 0.8)",   // Verde
            "rgba(245, 158, 11, 0.8)",  // Naranja
            "rgba(239, 68, 68, 0.8)",   // Rojo
            "rgba(168, 85, 247, 0.8)",  // Púrpura
            "rgba(14, 165, 233, 0.8)",  // Cyan
            "rgba(251, 146, 60, 0.8)",  // Naranja claro
            "rgba(236, 72, 153, 0.8)",  // Rosa
            "rgba(34, 211, 238, 0.8)",  // Turquesa
            "rgba(251, 191, 36, 0.8)"   // Amarillo
        };
        
        int colorIndex = 0;
        for (Map.Entry<String, Integer> entry : estadisticas.getTrabajadoresPorEspecialidad().entrySet()) {
            labels.add(entry.getKey());
            values.add(entry.getValue());
            bgColors.add(colores[colorIndex % colores.length]);
            colorIndex++;
        }
        
        barDataSet.setData(values);
        barDataSet.setBackgroundColor(bgColors);
        
        data.addChartDataSet(barDataSet);
        data.setLabels(labels);
        
        barModel.setData(data);
        
        // Opciones del gráfico
        BarChartOptions options = new BarChartOptions();
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Distribución por Especialidad");
        options.setTitle(title);
        
        barModel.setOptions(options);
    }
    
    /**
     * Calcula el porcentaje de trabajadores activos
     */
    public double getPorcentajeActivos() {
        if (estadisticas.getTotalTrabajadores() == 0) {
            return 0;
        }
        return (double) estadisticas.getTrabajadoresActivos() / estadisticas.getTotalTrabajadores() * 100;
    }
    
    /**
     * Calcula el porcentaje de trabajadores inactivos
     */
    public double getPorcentajeInactivos() {
        if (estadisticas.getTotalTrabajadores() == 0) {
            return 0;
        }
        return (double) estadisticas.getTrabajadoresInactivos() / estadisticas.getTotalTrabajadores() * 100;
    }
    
    /**
     * Obtiene la especialidad con más trabajadores
     */
    public String getEspecialidadMasComun() {
        if (estadisticas.getTrabajadoresPorEspecialidad().isEmpty()) {
            return "No hay datos";
        }
        
        return estadisticas.getTrabajadoresPorEspecialidad().entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No determinada");
    }
    
    /**
     * Obtiene el número de especialidades diferentes
     */
    public int getNumeroEspecialidades() {
        return estadisticas.getTrabajadoresPorEspecialidad().size();
    }
    
    // Getters
    public EstadisticasSistema getEstadisticas() {
        return estadisticas;
    }
    
    public DonutChartModel getDonutModel() {
        return donutModel;
    }
    
    public BarChartModel getBarModel() {
        return barModel;
    }
}
