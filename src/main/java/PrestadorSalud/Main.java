package PrestadorSalud;

import PrestadorSalud.model.PrestadorSalud;
import PrestadorSalud.model.TipoPrestador;
import PrestadorSalud.repository.PrestadorSaludRepository;
import PrestadorSalud.service.PrestadorSaludService;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final String API_BASE = System.getProperty("prestador.api.base", "http://localhost:8080/PrestadorSalud/api");

    public static void main(String[] args) {
        PrestadorSaludRepository repository = new PrestadorSaludRepository();
        PrestadorSaludService service = new PrestadorSaludService(repository);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("--- Prestadores de Salud ---");
            System.out.println("1. Agregar prestador");
            System.out.println("2. Listar prestadores");
            System.out.println("3. Buscar por nombre");
            System.out.println("0. Salir");
            System.out.print("Opción: ");
            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    System.out.print("Nombre: ");
                    String nombre = scanner.nextLine();
                    System.out.print("Dirección: ");
                    String direccion = scanner.nextLine();
                    System.out.print("Tipo (LABORATORIO/HOSPITAL): ");
                    TipoPrestador tipo = TipoPrestador.valueOf(scanner.nextLine().toUpperCase());
                    PrestadorSalud nuevo = new PrestadorSalud(nombre, direccion, tipo);
                    try {
                        enviarAltaAsincronica(nuevo);
                        System.out.println("El alta fue encolada y se procesará en breve.");
                    } catch (Exception e) {
                        System.err.println("No se pudo enviar el alta: " + e.getMessage());
                    }
                    break;
                case "2":
                    List<PrestadorSalud> todos = service.obtenerPrestadores();
                    todos.forEach(System.out::println);
                    break;
                case "3":
                    System.out.print("Nombre a buscar: ");
                    String nombreBuscar = scanner.nextLine();
                    List<PrestadorSalud> encontrados = service.buscarPorNombre(nombreBuscar);
                    encontrados.forEach(System.out::println);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    private static void enviarAltaAsincronica(PrestadorSalud prestador) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String json = String.format("{\"nombre\":\"%s\",\"direccion\":\"%s\",\"tipo\":\"%s\"}",
                escape(prestador.getNombre()),
                escape(prestador.getDireccion()),
                prestador.getTipo().name());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE + "/prestadores"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new IOException("Respuesta inesperada del servidor: " + response.body());
        }
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
