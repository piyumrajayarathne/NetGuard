package com.netguard.failure.repository;

import com.netguard.failure.entity.AlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertRepository extends JpaRepository<AlertEntity, Long> {

    List<AlertEntity> findAllByOrderByTimestampDesc();

    List<AlertEntity> findBySeverityOrderByTimestampDesc(String severity);

    List<AlertEntity> findByDevice_IdOrderByTimestampDesc(Long deviceId);

    List<AlertEntity> findByEventOrderByTimestampDesc(String event);
}