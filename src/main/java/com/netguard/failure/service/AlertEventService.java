package com.netguard.failure.service;

import com.netguard.failure.entity.AlertEventEntity;
import com.netguard.failure.model.AlertEvent;
import com.netguard.failure.repository.AlertEventRepository;
import org.springframework.stereotype.Service;

@Service
public class AlertEventService {

    private final AlertEventRepository repository;

    public AlertEventService(AlertEventRepository repository) {
        this.repository = repository;
    }

    public AlertEventEntity save(AlertEvent alert) {

        if (alert == null) {
            return null;
        }

        AlertEventEntity entity = new AlertEventEntity(
                alert.getDevice(),
                alert.getEvent(),
                alert.getSeverity(),
                alert.getReason(),
                alert.getTimestamp()
        );

        return repository.save(entity);
    }
}