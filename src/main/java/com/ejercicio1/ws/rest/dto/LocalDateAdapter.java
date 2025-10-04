package com.ejercicio1.ws.rest.dto;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;

/**
 * Adaptador JAXB para convertir LocalDate a String y viceversa
 * Necesario para la serialización XML en servicios SOAP
 */
public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

    @Override
    public LocalDate unmarshal(String v) throws Exception {
        return v != null ? LocalDate.parse(v) : null;
    }

    @Override
    public String marshal(LocalDate v) throws Exception {
        return v != null ? v.toString() : null;
    }
}