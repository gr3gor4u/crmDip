package com.example.javacrm.repository;

import com.example.javacrm.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByEmailContainingOrPhoneContaining(String email, String phone);
    List<Customer> findByTagsContaining(String tag);
} 