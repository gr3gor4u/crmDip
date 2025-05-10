package com.example.javacrm.repository;

import com.example.javacrm.model.Deal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DealRepository extends JpaRepository<Deal, Long> {
    List<Deal> findByCustomerId(Long customerId);
    List<Deal> findByCarId(Long carId);
    List<Deal> findByStatus(Deal.DealStatus status);
    List<Deal> findByTestDriveDateBetween(LocalDateTime start, LocalDateTime end);
    List<Deal> findByDealDateBetween(LocalDateTime start, LocalDateTime end);
} 