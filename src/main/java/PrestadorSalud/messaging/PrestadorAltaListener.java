package PrestadorSalud.messaging;

import PrestadorSalud.model.PrestadorSalud;
import PrestadorSalud.service.PrestadorSaludServiceLocal;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSDestinationDefinition;
import jakarta.jms.JMSDestinationDefinitions;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

@JMSDestinationDefinitions({
        @JMSDestinationDefinition(
                name = PrestadorAltaListener.QUEUE_JNDI,
                interfaceName = "jakarta.jms.Queue",
                destinationName = PrestadorAltaListener.QUEUE_NAME
        )
})
@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = PrestadorAltaListener.QUEUE_JNDI),
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue"),
                @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
        }
)
public class PrestadorAltaListener implements MessageListener {

    public static final String QUEUE_NAME = "queue_alta_prestador";
    public static final String QUEUE_JNDI = "java:/jms/queue/" + QUEUE_NAME;

    @EJB
    private PrestadorSaludServiceLocal service;

    @Override
    public void onMessage(Message message) {
        if (!(message instanceof TextMessage textMessage)) {
            System.err.println("[PrestadorAltaListener] Mensaje ignorado: tipo no soportado " + message);
            return;
        }
        try {
            String payload = textMessage.getText();
            PrestadorSalud prestador = PrestadorSaludMessageMapper.fromMessagePayload(payload);
            service.addPrestador(prestador);
            System.out.printf("[PrestadorAltaListener] Prestador '%s' procesado%n", prestador.getNombre());
        } catch (Exception e) {
            System.err.println("[PrestadorAltaListener] Error al procesar mensaje: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}
