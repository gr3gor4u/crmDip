package com.example.javacrm.controller;

import com.example.javacrm.model.Insurance;
import com.example.javacrm.service.InsuranceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/insurance")
public class InsuranceController {
    private final InsuranceService service;

    @Autowired
    public InsuranceController(InsuranceService service) {
        this.service = service;
    }

    @GetMapping
    public List<Insurance> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Insurance> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Insurance create(@RequestBody Insurance insurance) {
        return service.save(insurance);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Insurance> update(@PathVariable Long id, @RequestBody Insurance insurance) {
        return service.findById(id)
                .map(existingInsurance -> {
                    insurance.setId(id);
                    return ResponseEntity.ok(service.save(insurance));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.findById(id)
                .map(insurance -> {
                    service.deleteById(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 

import com.example.javacrm.model.Insurance;
import com.example.javacrm.service.InsuranceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/insurance")
public class InsuranceController {
    private final InsuranceService service;

    @Autowired
    public InsuranceController(InsuranceService service) {
        this.service = service;
    }

    @GetMapping
    public List<Insurance> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Insurance> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Insurance create(@RequestBody Insurance insurance) {
        return service.save(insurance);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Insurance> update(@PathVariable Long id, @RequestBody Insurance insurance) {
        return service.findById(id)
                .map(existingInsurance -> {
                    insurance.setId(id);
                    return ResponseEntity.ok(service.save(insurance));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.findById(id)
                .map(insurance -> {
                    service.deleteById(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 