package com.example.javacrm.repository;

import com.example.javacrm.model.Customer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CustomerDao {
    private final SessionFactory sessionFactory;

    @Autowired
    public CustomerDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Customer customer) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(customer);
    }

    public void update(Customer customer) {
        Session session = sessionFactory.getCurrentSession();
        session.update(customer);
    }

    public void delete(Customer customer) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(customer);
    }

    public Optional<Customer> findById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return Optional.ofNullable(session.get(Customer.class, id));
    }

    public List<Customer> findAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from Customer", Customer.class).list();
    }

    public Optional<Customer> findByEmail(String email) {
        Session session = sessionFactory.getCurrentSession();
        Query<Customer> query = session.createQuery("from Customer where email = :email", Customer.class);
        query.setParameter("email", email);
        return query.uniqueResultOptional();
    }

    public Optional<Customer> findByPhone(String phone) {
        Session session = sessionFactory.getCurrentSession();
        Query<Customer> query = session.createQuery("from Customer where phone = :phone", Customer.class);
        query.setParameter("phone", phone);
        return query.uniqueResultOptional();
    }
} 

import com.example.javacrm.model.Customer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CustomerDao {
    private final SessionFactory sessionFactory;

    @Autowired
    public CustomerDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Customer customer) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(customer);
    }

    public void update(Customer customer) {
        Session session = sessionFactory.getCurrentSession();
        session.update(customer);
    }

    public void delete(Customer customer) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(customer);
    }

    public Optional<Customer> findById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return Optional.ofNullable(session.get(Customer.class, id));
    }

    public List<Customer> findAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from Customer", Customer.class).list();
    }

    public Optional<Customer> findByEmail(String email) {
        Session session = sessionFactory.getCurrentSession();
        Query<Customer> query = session.createQuery("from Customer where email = :email", Customer.class);
        query.setParameter("email", email);
        return query.uniqueResultOptional();
    }

    public Optional<Customer> findByPhone(String phone) {
        Session session = sessionFactory.getCurrentSession();
        Query<Customer> query = session.createQuery("from Customer where phone = :phone", Customer.class);
        query.setParameter("phone", phone);
        return query.uniqueResultOptional();
    }
} 