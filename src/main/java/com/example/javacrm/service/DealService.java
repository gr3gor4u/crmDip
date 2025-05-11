package com.example.javacrm.service;

import com.example.javacrm.model.Deal;
import com.example.javacrm.model.DealStatus;
import com.example.javacrm.repository.DealDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DealService {
    private final DealDao dealDao;

    @Autowired
    public DealService(DealDao dealDao) {
        this.dealDao = dealDao;
    }

    @Transactional
    public void saveDeal(Deal deal) {
        dealDao.save(deal);
    }

    @Transactional
    public void updateDeal(Deal deal) {
        dealDao.update(deal);
    }

    @Transactional
    public void deleteDeal(Deal deal) {
        dealDao.delete(deal);
    }

    @Transactional(readOnly = true)
    public Optional<Deal> getDealById(Long id) {
        return dealDao.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Deal> getAllDeals() {
        return dealDao.findAll();
    }

    @Transactional(readOnly = true)
    public List<Deal> getDealsByStatus(DealStatus status) {
        return dealDao.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Deal> getDealsByCustomerId(Long customerId) {
        return dealDao.findByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public List<Deal> getDealsByCarId(Long carId) {
        return dealDao.findByCarId(carId);
    }
} 

import com.example.javacrm.model.Deal;
import com.example.javacrm.model.DealStatus;
import com.example.javacrm.repository.DealDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DealService {
    private final DealDao dealDao;

    @Autowired
    public DealService(DealDao dealDao) {
        this.dealDao = dealDao;
    }

    @Transactional
    public void saveDeal(Deal deal) {
        dealDao.save(deal);
    }

    @Transactional
    public void updateDeal(Deal deal) {
        dealDao.update(deal);
    }

    @Transactional
    public void deleteDeal(Deal deal) {
        dealDao.delete(deal);
    }

    @Transactional(readOnly = true)
    public Optional<Deal> getDealById(Long id) {
        return dealDao.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Deal> getAllDeals() {
        return dealDao.findAll();
    }

    @Transactional(readOnly = true)
    public List<Deal> getDealsByStatus(DealStatus status) {
        return dealDao.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Deal> getDealsByCustomerId(Long customerId) {
        return dealDao.findByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public List<Deal> getDealsByCarId(Long carId) {
        return dealDao.findByCarId(carId);
    }
} 