package com.example.javacrm.service;

import com.example.javacrm.model.AdditionalEquipment;
import com.example.javacrm.repository.AdditionalEquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AdditionalEquipmentService {
    private final AdditionalEquipmentRepository repository;

    @Autowired
    public AdditionalEquipmentService(AdditionalEquipmentRepository repository) {
        this.repository = repository;
    }

    public List<AdditionalEquipment> findAll() {
        return repository.findAll();
    }

    public Optional<AdditionalEquipment> findById(BigDecimal price) {
        return repository.findById(price);
    }

    public AdditionalEquipment save(AdditionalEquipment equipment) {
        return repository.save(equipment);
    }

    public void deleteById(BigDecimal price) {
        repository.deleteById(price);
    }
} 

import com.example.javacrm.model.AdditionalEquipment;
import com.example.javacrm.repository.AdditionalEquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AdditionalEquipmentService {
    private final AdditionalEquipmentRepository repository;

    @Autowired
    public AdditionalEquipmentService(AdditionalEquipmentRepository repository) {
        this.repository = repository;
    }

    public List<AdditionalEquipment> findAll() {
        return repository.findAll();
    }

    public Optional<AdditionalEquipment> findById(BigDecimal price) {
        return repository.findById(price);
    }

    public AdditionalEquipment save(AdditionalEquipment equipment) {
        return repository.save(equipment);
    }

    public void deleteById(BigDecimal price) {
        repository.deleteById(price);
    }
} 