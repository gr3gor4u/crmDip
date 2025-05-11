package com.example.javacrm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "insurance")
public class Insurance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_osago")
    private Long id;

    @NotBlank(message = "ФИО не может быть пустым")
    @Column(name = "fio")
    private String fullName;

    @NotNull(message = "Дата не может быть пустой")
    @Column(name = "data")
    private LocalDateTime date;

    @NotBlank(message = "Серия и номер водительского удостоверения не могут быть пустыми")
    @Pattern(regexp = "^\\d{2}\\s\\d{2}\\s\\d{6}$", message = "Неверный формат водительского удостоверения")
    @Column(name = "seria_num_vod")
    private String driverLicense;

    @NotBlank(message = "Дополнительные водители не могут быть пустыми")
    @Column(name = "pipl_dop_k_avto")
    private String additionalDrivers;

    @NotNull(message = "Страховая премия не может быть пустой")
    @DecimalMin(value = "0.0", message = "Страховая премия должна быть положительной")
    @Column(name = "strah_premia")
    private BigDecimal insurancePremium;

    @NotNull(message = "Стоимость ОСАГО не может быть пустой")
    @DecimalMin(value = "0.0", message = "Стоимость ОСАГО должна быть положительной")
    @Column(name = "price_osago")
    private BigDecimal osagoPrice;

    @NotNull(message = "Автомобиль не может быть пустым")
    @OneToOne
    @JoinColumn(name = "vin_number")
    private Car car;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (date == null) {
            date = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "insurance")
public class Insurance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_osago")
    private Long id;

    @NotBlank(message = "ФИО не может быть пустым")
    @Column(name = "fio")
    private String fullName;

    @NotNull(message = "Дата не может быть пустой")
    @Column(name = "data")
    private LocalDateTime date;

    @NotBlank(message = "Серия и номер водительского удостоверения не могут быть пустыми")
    @Pattern(regexp = "^\\d{2}\\s\\d{2}\\s\\d{6}$", message = "Неверный формат водительского удостоверения")
    @Column(name = "seria_num_vod")
    private String driverLicense;

    @NotBlank(message = "Дополнительные водители не могут быть пустыми")
    @Column(name = "pipl_dop_k_avto")
    private String additionalDrivers;

    @NotNull(message = "Страховая премия не может быть пустой")
    @DecimalMin(value = "0.0", message = "Страховая премия должна быть положительной")
    @Column(name = "strah_premia")
    private BigDecimal insurancePremium;

    @NotNull(message = "Стоимость ОСАГО не может быть пустой")
    @DecimalMin(value = "0.0", message = "Стоимость ОСАГО должна быть положительной")
    @Column(name = "price_osago")
    private BigDecimal osagoPrice;

    @NotNull(message = "Автомобиль не может быть пустым")
    @OneToOne
    @JoinColumn(name = "vin_number")
    private Car car;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (date == null) {
            date = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 