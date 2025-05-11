package com.example.javacrm.repository;

import com.example.javacrm.model.Car;
import com.example.javacrm.model.CarStatus;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CarDao {
    private final SessionFactory sessionFactory;

    @Autowired
    public CarDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Car car) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(car);
    }

    public void update(Car car) {
        Session session = sessionFactory.getCurrentSession();
        session.update(car);
    }

    public void delete(Car car) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(car);
    }

    public Optional<Car> findById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return Optional.ofNullable(session.get(Car.class, id));
    }

    public Optional<Car> findByVinNumber(String vinNumber) {
        Session session = sessionFactory.getCurrentSession();
        Query<Car> query = session.createQuery("from Car where vinNumber = :vin", Car.class);
        query.setParameter("vin", vinNumber);
        return query.uniqueResultOptional();
    }

    public List<Car> findAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from Car", Car.class).list();
    }

    public List<Car> findByStatus(CarStatus status) {
        Session session = sessionFactory.getCurrentSession();
        Query<Car> query = session.createQuery("from Car where status = :status", Car.class);
        query.setParameter("status", status);
        return query.list();
    }

    public boolean existsByVinNumber(String vinNumber) {
        return findByVinNumber(vinNumber).isPresent();
    }

    public List<String> getAllBrands() {
        return findAll().stream()
                .map(Car::getBrand)
                .distinct()
                .collect(Collectors.toList());
    }
} 

import com.example.javacrm.model.Car;
import com.example.javacrm.model.CarStatus;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CarDao {
    private final SessionFactory sessionFactory;

    @Autowired
    public CarDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Car car) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(car);
    }

    public void update(Car car) {
        Session session = sessionFactory.getCurrentSession();
        session.update(car);
    }

    public void delete(Car car) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(car);
    }

    public Optional<Car> findById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return Optional.ofNullable(session.get(Car.class, id));
    }

    public Optional<Car> findByVinNumber(String vinNumber) {
        Session session = sessionFactory.getCurrentSession();
        Query<Car> query = session.createQuery("from Car where vinNumber = :vin", Car.class);
        query.setParameter("vin", vinNumber);
        return query.uniqueResultOptional();
    }

    public List<Car> findAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from Car", Car.class).list();
    }

    public List<Car> findByStatus(CarStatus status) {
        Session session = sessionFactory.getCurrentSession();
        Query<Car> query = session.createQuery("from Car where status = :status", Car.class);
        query.setParameter("status", status);
        return query.list();
    }

    public boolean existsByVinNumber(String vinNumber) {
        return findByVinNumber(vinNumber).isPresent();
    }

    public List<String> getAllBrands() {
        return findAll().stream()
                .map(Car::getBrand)
                .distinct()
                .collect(Collectors.toList());
    }
} 