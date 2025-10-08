package PrestadorSalud.messaging;

import PrestadorSalud.model.PrestadorSalud;
import PrestadorSalud.model.TipoPrestador;

public final class PrestadorSaludMessageMapper {

    private static final String DELIMITER = "|";

    private PrestadorSaludMessageMapper() {
    }

    public static String toMessagePayload(PrestadorSalud prestador) {
        if (prestador.getNombre() == null || prestador.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (prestador.getDireccion() == null || prestador.getDireccion().isBlank()) {
            throw new IllegalArgumentException("La dirección es obligatoria");
        }
        if (prestador.getTipo() == null) {
            throw new IllegalArgumentException("El tipo es obligatorio");
        }
        return String.join(DELIMITER,
                sanitize(prestador.getNombre()),
                sanitize(prestador.getDireccion()),
                prestador.getTipo().name());
    }

    public static PrestadorSalud fromMessagePayload(String payload) {
        if (payload == null || payload.isBlank()) {
            throw new IllegalArgumentException("El mensaje recibido está vacío");
        }
        String[] tokens = payload.split("\\|");
        if (tokens.length < 3) {
            throw new IllegalArgumentException("El mensaje recibido es inválido: faltan atributos");
        }
        PrestadorSalud prestador = new PrestadorSalud();
        prestador.setNombre(tokens[0].trim());
        prestador.setDireccion(tokens[1].trim());
        prestador.setTipo(TipoPrestador.valueOf(tokens[2].trim().toUpperCase()));
        return prestador;
    }

    private static String sanitize(String value) {
        String trimmed = value.trim();
        if (trimmed.contains(DELIMITER)) {
            throw new IllegalArgumentException("Los valores no pueden contener el caracter '|'");
        }
        return trimmed;
    }
}
