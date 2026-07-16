package sev.amorlov.plant_nursery.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OrderCreatedEvent extends ApplicationEvent {
    private final Long orderId;
    private final String customerEmail;

    public OrderCreatedEvent(Object source, Long orderId, String customerEmail) {
        super(source);
        this.orderId = orderId;
        this.customerEmail = customerEmail;
    }
}