package org.apereo.cas.web.flow.actions;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.webflow.action.EventFactorySupport;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import java.util.function.Consumer;

/**
 * This is {@link ConsumerExecutionAction}.
 *
 * @author Misagh Moayyed
 * @since 6.4.0
 */
@RequiredArgsConstructor
@Accessors(chain = true)
public class ConsumerExecutionAction implements Action {
    /**
     * Consumer action that does nothing and returns null, effectively being a no-op.
     */
    public static final Action NONE = new ConsumerExecutionAction(ctx -> {
    });

    private final Consumer<RequestContext> task;

    @Setter
    private String eventId;

    @Override
    public Event execute(final RequestContext requestContext) {
        this.task.accept(requestContext);
        return StringUtils.isNotBlank(this.eventId) ? new EventFactorySupport().event(this, this.eventId) : null;
    }

    @Override
    public String toString() {
        return "Inline Consumer Action, returning " + this.eventId;
    }
}
