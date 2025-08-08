package com.k8s.gpu.repository;

import com.k8s.gpu.entity.SystemAlerts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemAlertsRepository extends JpaRepository<SystemAlerts, String> {

    @Query("SELECT a.alertId, a.alertType, a.severity, a.targetType, " +
           "a.targetId, a.message, a.createdDate, a.status " +
           "FROM SystemAlerts a " +
           "WHERE a.status = 'ACTIVE' " +
           "ORDER BY a.severity DESC, a.createdDate DESC")
    List<Object[]> getActiveAlerts();
}