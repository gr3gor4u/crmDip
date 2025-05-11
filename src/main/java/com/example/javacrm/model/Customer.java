package com.example.javacrm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

@Data
@Entity
@Table(name = "customers",
       indexes = {
           @Index(name = "idx_lower_email", columnList = "email")
       })
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_chelovek")
    private Long id;

    @NotBlank(message = "ФИО не может быть пустым")
    @Column(name = "fio")
    private String fullName;

    @NotBlank(message = "Место рождения не может быть пустым")
    @Column(name = "mesto_rozhd")
    private String birthPlace;

    @NotBlank(message = "Паспортные данные не могут быть пустыми")
    @Pattern(regexp = "^\\d{4}\\s\\d{6}$", message = "Неверный формат паспортных данных (серия номер)")
    @Column(name = "pasport_data")
    private String passportData;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Неверный формат email")
    @Column(name = "email")
    private String email;

    @NotBlank(message = "Телефон не может быть пустым")
    @Pattern(regexp = "^\\+?[1-9]\\d{10,14}$", message = "Неверный формат телефона")
    @Column(nullable = false)
    private String phone;

    @ElementCollection
    @CollectionTable(name = "customer_tags", joinColumns = @JoinColumn(name = "customer_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "customer")
    private List<Deal> deals;

    @OneToMany(mappedBy = "customer")
    private List<Insurance> insurances;

    @OneToMany(mappedBy = "customer")
    private List<TradeInCar> tradeInCars;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getBirthPlace() { return birthPlace; }
    public void setBirthPlace(String birthPlace) { this.birthPlace = birthPlace; }

    public String getPassportData() { return passportData; }
    public void setPassportData(String passportData) { this.passportData = passportData; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
} 
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getBirthPlace() { return birthPlace; }
    public void setBirthPlace(String birthPlace) { this.birthPlace = birthPlace; }

    public String getPassportData() { return passportData; }
    public void setPassportData(String passportData) { this.passportData = passportData; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
} 