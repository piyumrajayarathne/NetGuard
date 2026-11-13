package com.netguard.failure.repository;

import com.netguard.failure.entity.NetworkDeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NetworkDeviceRepository
        extends JpaRepository<NetworkDeviceEntity, Long> {
}
