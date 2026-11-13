package com.netguard.failure.repository;

import com.netguard.failure.entity.NetworkDeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NetworkDeviceRepository
        extends JpaRepository<NetworkDeviceEntity, Long> {

    Optional<NetworkDeviceEntity> findByAddress(String address);
}