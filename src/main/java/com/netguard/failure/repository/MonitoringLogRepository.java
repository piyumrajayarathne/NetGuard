package com.netguard.failure.repository;

import com.netguard.failure.entity.MonitoringLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MonitoringLogRepository
        extends JpaRepository<MonitoringLogEntity, Long> {

    List<MonitoringLogEntity> findByDeviceId(Long deviceId);

    List<MonitoringLogEntity> findAllByOrderByTimestampDesc();
}
