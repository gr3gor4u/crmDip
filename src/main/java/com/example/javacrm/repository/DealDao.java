package com.example.javacrm.repository;

import com.example.javacrm.model.Deal;
import com.example.javacrm.model.DealStatus;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DealDao {
    private final SessionFactory sessionFactory;

    @Autowired
    public DealDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Deal deal) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(deal);
    }

    public void update(Deal deal) {
        Session session = sessionFactory.getCurrentSession();
        session.update(deal);
    }

    public void delete(Deal deal) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(deal);
    }

    public Optional<Deal> findById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return Optional.ofNullable(session.get(Deal.class, id));
    }

    public List<Deal> findAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from Deal", Deal.class).list();
    }

    public List<Deal> findByStatus(DealStatus status) {
        Session session = sessionFactory.getCurrentSession();
        Query<Deal> query = session.createQuery("from Deal where status = :status", Deal.class);
        query.setParameter("status", status);
        return query.list();
    }

    public List<Deal> findByCustomerId(Long customerId) {
        Session session = sessionFactory.getCurrentSession();
        Query<Deal> query = session.createQuery("from Deal where customer.id = :customerId", Deal.class);
        query.setParameter("customerId", customerId);
        return query.list();
    }

    public List<Deal> findByCarId(Long carId) {
        Session session = sessionFactory.getCurrentSession();
        Query<Deal> query = session.createQuery("from Deal where car.id = :carId", Deal.class);
        query.setParameter("carId", carId);
        return query.list();
    }
} 

import com.example.javacrm.model.Deal;
import com.example.javacrm.model.DealStatus;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DealDao {
    private final SessionFactory sessionFactory;

    @Autowired
    public DealDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Deal deal) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(deal);
    }

    public void update(Deal deal) {
        Session session = sessionFactory.getCurrentSession();
        session.update(deal);
    }

    public void delete(Deal deal) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(deal);
    }

    public Optional<Deal> findById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return Optional.ofNullable(session.get(Deal.class, id));
    }

    public List<Deal> findAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from Deal", Deal.class).list();
    }

    public List<Deal> findByStatus(DealStatus status) {
        Session session = sessionFactory.getCurrentSession();
        Query<Deal> query = session.createQuery("from Deal where status = :status", Deal.class);
        query.setParameter("status", status);
        return query.list();
    }

    public List<Deal> findByCustomerId(Long customerId) {
        Session session = sessionFactory.getCurrentSession();
        Query<Deal> query = session.createQuery("from Deal where customer.id = :customerId", Deal.class);
        query.setParameter("customerId", customerId);
        return query.list();
    }

    public List<Deal> findByCarId(Long carId) {
        Session session = sessionFactory.getCurrentSession();
        Query<Deal> query = session.createQuery("from Deal where car.id = :carId", Deal.class);
        query.setParameter("carId", carId);
        return query.list();
    }
} 