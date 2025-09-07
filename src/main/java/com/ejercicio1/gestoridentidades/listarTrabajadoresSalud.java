package com.ejercicio1.gestoridentidades;

import jakarta.ws.rs.*;

@Path("/hello-world")
public class listarTrabajadoresSalud {
    @GET
    @Produces("text/plain")
    public String hello() {
        return "Hello, World!";
    }
}