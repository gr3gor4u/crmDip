package com.example.javacrm.service;

import com.example.javacrm.model.Deal;
import com.example.javacrm.model.DealEquipment;
import com.example.javacrm.model.Customer;
import com.example.javacrm.model.Car;
import com.example.javacrm.model.Insurance;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DealService {
    private final DatabaseService databaseService;
    private final CustomerService customerService;
    private final CarService carService;
    private final InsuranceService insuranceService;
    private final EquipmentService equipmentService;

    public DealService(DatabaseService databaseService, CustomerService customerService, CarService carService, 
                      InsuranceService insuranceService, EquipmentService equipmentService) {
        this.databaseService = databaseService;
        this.customerService = customerService;
        this.carService = carService;
        this.insuranceService = insuranceService;
        this.equipmentService = equipmentService;
    }

    public List<Deal> getAllDeals() {
        // TODO: Реализовать получение всех сделок из базы данных
        return new ArrayList<>();
    }

    public Deal getDealById(Long id) {
        String query = "SELECT * FROM deals WHERE id = ?";
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Deal deal = new Deal(
                    rs.getLong("id"),
                    rs.getLong("car_id"),
                    rs.getLong("customer_id"),
                    rs.getLong("insurance_id"),
                    rs.getDouble("total_price"),
                    rs.getString("status")
                );
                deal.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                
                // Load relationships
                Customer customer = customerService.getCustomerById(deal.getCustomerId());
                if (customer != null) {
                    deal.setCustomer(customer);
                }
                
                Car car = carService.getCarById(deal.getCarId());
                if (car != null) {
                    deal.setCar(car);
                }
                
                Insurance insurance = insuranceService.getInsuranceById(deal.getInsuranceId());
                if (insurance != null) {
                    deal.setInsurance(insurance);
                }
                
                // Load equipment
                List<DealEquipment> equipment = getDealEquipment(deal.getId());
                deal.setEquipment(equipment);
                
                return deal;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<DealEquipment> getDealEquipment(Long dealId) {
        List<DealEquipment> equipment = new ArrayList<>();
        String query = "SELECT * FROM deal_equipment WHERE deal_id = ?";
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setLong(1, dealId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                DealEquipment item = new DealEquipment(
                    rs.getLong("deal_id"),
                    rs.getLong("equipment_id"),
                    rs.getInt("quantity"),
                    rs.getDouble("price")
                );
                item.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                
                // Load equipment details
                var equipmentDetails = equipmentService.getEquipmentById(item.getEquipmentId());
                if (equipmentDetails != null) {
                    item.setEquipment(equipmentDetails);
                }
                
                equipment.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equipment;
    }

    public void addDeal(Deal deal) {
        // TODO: Реализовать добавление сделки
    }

    public void updateDeal(Deal deal) {
        // TODO: Реализовать обновление сделки
    }

    private void deleteDealEquipment(Long dealId) {
        String query = "DELETE FROM deal_equipment WHERE deal_id = ?";
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setLong(1, dealId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteDeal(Long id) {
        // TODO: Реализовать удаление сделки
    }

    public List<Deal> searchDeals(String customerId, String carId, String status) {
        List<Deal> deals = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM deals WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (customerId != null && !customerId.isEmpty()) {
            query.append(" AND customer_id = ?");
            params.add(Long.parseLong(customerId));
        }
        
        if (carId != null && !carId.isEmpty()) {
            query.append(" AND car_id = ?");
            params.add(Long.parseLong(carId));
        }
        
        if (status != null && !status.isEmpty()) {
            query.append(" AND status = ?");
            params.add(status);
        }
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Deal deal = new Deal(
                    rs.getLong("id"),
                    rs.getLong("car_id"),
                    rs.getLong("customer_id"),
                    rs.getLong("insurance_id"),
                    rs.getDouble("total_price"),
                    rs.getString("status")
                );
                deal.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                
                // Load relationships
                Customer customer = customerService.getCustomerById(deal.getCustomerId());
                if (customer != null) {
                    deal.setCustomer(customer);
                }
                
                Car car = carService.getCarById(deal.getCarId());
                if (car != null) {
                    deal.setCar(car);
                }
                
                Insurance insurance = insuranceService.getInsuranceById(deal.getInsuranceId());
                if (insurance != null) {
                    deal.setInsurance(insurance);
                }
                
                // Load equipment
                List<DealEquipment> equipment = getDealEquipment(deal.getId());
                deal.setEquipment(equipment);
                
                deals.add(deal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deals;
    }
} 