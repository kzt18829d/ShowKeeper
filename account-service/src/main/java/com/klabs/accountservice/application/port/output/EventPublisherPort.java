package com.klabs.accountservice.application.port.output;

import com.klabs.accountservice.domain.event.DomainEvent;

import java.util.List;

public interface EventPublisherPort {

    void publish(DomainEvent event);

    void publishBatch(List<DomainEvent> events);

}
