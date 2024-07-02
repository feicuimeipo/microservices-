package org.apereo.cas.ticket.registry;

import org.apereo.cas.ticket.Ticket;
import org.apereo.cas.ticket.queue.AddTicketMessageQueueCommand;
import org.apereo.cas.ticket.queue.DeleteTicketMessageQueueCommand;
import org.apereo.cas.ticket.queue.DeleteTicketsMessageQueueCommand;
import org.apereo.cas.ticket.queue.TicketRegistryQueuePublisher;
import org.apereo.cas.ticket.queue.UpdateTicketMessageQueueCommand;
import org.apereo.cas.ticket.serialization.TicketSerializationManager;
import org.apereo.cas.util.PublisherIdentifier;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * This is {@link JmsTicketRegistry}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Slf4j
@RequiredArgsConstructor
public class JmsTicketRegistry extends DefaultTicketRegistry {

    private final TicketRegistryQueuePublisher ticketPublisher;

    private final PublisherIdentifier id;

    private final TicketSerializationManager ticketSerializationManager;

    @Override
    public void addTicketInternal(final @NonNull Ticket ticket) throws Exception {
        super.addTicketInternal(ticket);
        LOGGER.trace("Publishing add command for id [{}] and ticket [{}]", id, ticket.getId());
        val body = ticketSerializationManager.serializeTicket(ticket);
        val command = new AddTicketMessageQueueCommand(id, body, ticket.getClass().getName());
        ticketPublisher.publishMessageToQueue(command);
    }

    @Override
    public long deleteSingleTicket(final String ticketId) {
        val result = super.deleteSingleTicket(ticketId);
        LOGGER.trace("Publishing delete command for id [{}] and ticket [{}]", id, ticketId);
        ticketPublisher.publishMessageToQueue(new DeleteTicketMessageQueueCommand(id, ticketId));
        return result;
    }

    @Override
    public long deleteAll() {
        val result = super.deleteAll();
        ticketPublisher.publishMessageToQueue(new DeleteTicketsMessageQueueCommand(id));
        return result;
    }

    @Override
    public Ticket updateTicket(final Ticket ticket) throws Exception {
        val result = super.updateTicket(ticket);
        LOGGER.trace("Publishing update command for id [{}] and ticket [{}]", id, ticket.getId());
        val body = ticketSerializationManager.serializeTicket(ticket);
        val command = new UpdateTicketMessageQueueCommand(id, body, ticket.getClass().getName());
        ticketPublisher.publishMessageToQueue(command);
        return result;
    }
}
