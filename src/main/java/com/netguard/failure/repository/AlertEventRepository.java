package com.netguard.failure.repository;

import com.netguard.failure.entity.AlertEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertEventRepository
        extends JpaRepository<AlertEventEntity, Long> {
}
