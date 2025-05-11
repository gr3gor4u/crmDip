package com.example.javacrm.service;

import com.example.javacrm.model.Insurance;
import com.example.javacrm.repository.InsuranceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InsuranceService {
    private final InsuranceRepository repository;

    @Autowired
    public InsuranceService(InsuranceRepository repository) {
        this.repository = repository;
    }

    public List<Insurance> findAll() {
        return repository.findAll();
    }

    public Optional<Insurance> findById(Long id) {
        return repository.findById(id);
    }

    public Insurance save(Insurance insurance) {
        return repository.save(insurance);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
} 

import com.example.javacrm.model.Insurance;
import com.example.javacrm.repository.InsuranceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InsuranceService {
    private final InsuranceRepository repository;

    @Autowired
    public InsuranceService(InsuranceRepository repository) {
        this.repository = repository;
    }

    public List<Insurance> findAll() {
        return repository.findAll();
    }

    public Optional<Insurance> findById(Long id) {
        return repository.findById(id);
    }

    public Insurance save(Insurance insurance) {
        return repository.save(insurance);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
} 