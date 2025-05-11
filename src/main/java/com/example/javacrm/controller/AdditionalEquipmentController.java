package com.example.javacrm.controller;

import com.example.javacrm.model.AdditionalEquipment;
import com.example.javacrm.service.AdditionalEquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/additional-equipment")
public class AdditionalEquipmentController {
    private final AdditionalEquipmentService service;

    @Autowired
    public AdditionalEquipmentController(AdditionalEquipmentService service) {
        this.service = service;
    }

    @GetMapping
    public List<AdditionalEquipment> getAll() {
        return service.findAll();
    }

    @GetMapping("/{price}")
    public ResponseEntity<AdditionalEquipment> getById(@PathVariable BigDecimal price) {
        return service.findById(price)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public AdditionalEquipment create(@RequestBody AdditionalEquipment equipment) {
        return service.save(equipment);
    }

    @PutMapping("/{price}")
    public ResponseEntity<AdditionalEquipment> update(@PathVariable BigDecimal price, @RequestBody AdditionalEquipment equipment) {
        return service.findById(price)
                .map(existingEquipment -> {
                    equipment.setPrice(price);
                    return ResponseEntity.ok(service.save(equipment));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{price}")
    public ResponseEntity<Void> delete(@PathVariable BigDecimal price) {
        return service.findById(price)
                .map(equipment -> {
                    service.deleteById(price);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 

import com.example.javacrm.model.AdditionalEquipment;
import com.example.javacrm.service.AdditionalEquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/additional-equipment")
public class AdditionalEquipmentController {
    private final AdditionalEquipmentService service;

    @Autowired
    public AdditionalEquipmentController(AdditionalEquipmentService service) {
        this.service = service;
    }

    @GetMapping
    public List<AdditionalEquipment> getAll() {
        return service.findAll();
    }

    @GetMapping("/{price}")
    public ResponseEntity<AdditionalEquipment> getById(@PathVariable BigDecimal price) {
        return service.findById(price)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public AdditionalEquipment create(@RequestBody AdditionalEquipment equipment) {
        return service.save(equipment);
    }

    @PutMapping("/{price}")
    public ResponseEntity<AdditionalEquipment> update(@PathVariable BigDecimal price, @RequestBody AdditionalEquipment equipment) {
        return service.findById(price)
                .map(existingEquipment -> {
                    equipment.setPrice(price);
                    return ResponseEntity.ok(service.save(equipment));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{price}")
    public ResponseEntity<Void> delete(@PathVariable BigDecimal price) {
        return service.findById(price)
                .map(equipment -> {
                    service.deleteById(price);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 