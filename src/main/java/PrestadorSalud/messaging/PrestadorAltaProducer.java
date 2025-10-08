package PrestadorSalud.messaging;

import PrestadorSalud.model.PrestadorSalud;
import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSRuntimeException;
import jakarta.jms.Queue;

@Stateless
public class PrestadorAltaProducer {

    @Inject
    private JMSContext jmsContext;

    @Resource(lookup = PrestadorAltaListener.QUEUE_JNDI)
    private Queue altaPrestadorQueue;

    public void enviarAlta(PrestadorSalud prestador) {
        try {
            String payload = PrestadorSaludMessageMapper.toMessagePayload(prestador);
            jmsContext.createProducer().send(altaPrestadorQueue, payload);
        } catch (JMSRuntimeException ex) {
            throw new IllegalStateException("No se pudo enviar el mensaje a la cola", ex);
        }
    }
}
