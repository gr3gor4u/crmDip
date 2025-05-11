package com.example.javacrm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "cars",
       indexes = {
           @Index(name = "idx_brand_model", columnList = "brand,model"),
           @Index(name = "idx_status_price", columnList = "status,price")
       })
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "VIN-номер не может быть пустым")
    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "Неверный формат VIN-номера")
    @Column(name = "vin_number", unique = true)
    private String vinNumber;

    @NotBlank(message = "Модель не может быть пустой")
    @Size(min = 2, max = 100, message = "Длина модели должна быть от 2 до 100 символов")
    @Column(name = "model")
    private String model;

    @NotBlank(message = "Марка не может быть пустой")
    @Size(min = 2, max = 50, message = "Длина марки должна быть от 2 до 50 символов")
    @Column(name = "brand")
    private String brand;

    @NotNull(message = "Цена не может быть пустой")
    @DecimalMin(value = "0.0", message = "Цена должна быть положительной")
    @DecimalMax(value = "1000000000.0", message = "Цена не может превышать 1 миллиард")
    @Column(name = "price")
    private BigDecimal price;

    @NotBlank(message = "Тип кузова не может быть пустым")
    @Column(name = "body_type")
    private String bodyType;

    @NotBlank(message = "Цвет не может быть пустым")
    @Column(name = "color")
    private String color;

    @NotNull(message = "Объем двигателя не может быть пустым")
    @DecimalMin(value = "0.0", message = "Объем двигателя должен быть положительным")
    @DecimalMax(value = "10.0", message = "Объем двигателя не может превышать 10 литров")
    @Column(name = "engine_volume")
    private Double engineVolume;

    @NotNull(message = "Мощность не может быть пустой")
    @Min(value = 0, message = "Мощность должна быть положительной")
    @Max(value = 2000, message = "Мощность не может превышать 2000 л.с.")
    @Column(name = "horse_power")
    private Integer horsePower;

    @NotNull(message = "Год не может быть пустым")
    @Min(value = 1900, message = "Год должен быть не раньше 1900")
    @Max(value = 2100, message = "Год должен быть не позже 2100")
    @Column(name = "year")
    private Integer year;

    @NotNull(message = "Пробег не может быть пустым")
    @Min(value = 0, message = "Пробег должен быть положительным")
    @Max(value = 1000000, message = "Пробег не может превышать 1 000 000 км")
    @Column(name = "mileage")
    private Integer mileage;

    @Size(max = 1000, message = "Описание не может превышать 1000 символов")
    @Column(name = "description")
    private String description;

    @NotNull(message = "Статус не может быть пустым")
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CarStatus status;

    @Column(name = "last_service_date")
    private LocalDateTime lastServiceDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "car")
    private Deal deal;

    @OneToOne(mappedBy = "car")
    private Insurance insurance;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        validateVinNumber();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        validateVinNumber();
    }

    private void validateVinNumber() {
        if (vinNumber == null || vinNumber.length() != 17) {
            throw new IllegalArgumentException("VIN-номер должен содержать 17 символов");
        }

        // Проверка контрольной суммы VIN
        int[] weights = {8, 7, 6, 5, 4, 3, 2, 10, 0, 9, 8, 7, 6, 5, 4, 3, 2};
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            char c = vinNumber.charAt(i);
            int value;
            if (c >= '0' && c <= '9') {
                value = c - '0';
            } else if (c >= 'A' && c <= 'Z') {
                value = (c - 'A' + 1) % 10;
            } else {
                throw new IllegalArgumentException("VIN-номер содержит недопустимые символы");
            }
            sum += value * weights[i];
        }
        int checkDigit = sum % 11;
        if (checkDigit == 10) {
            if (vinNumber.charAt(8) != 'X') {
                throw new IllegalArgumentException("Неверная контрольная сумма VIN-номера");
            }
        } else if (vinNumber.charAt(8) - '0' != checkDigit) {
            throw new IllegalArgumentException("Неверная контрольная сумма VIN-номера");
        }
    }
} 
    private void validateVinNumber() {
        if (vinNumber == null || vinNumber.length() != 17) {
            throw new IllegalArgumentException("VIN-номер должен содержать 17 символов");
        }

        // Проверка контрольной суммы VIN
        int[] weights = {8, 7, 6, 5, 4, 3, 2, 10, 0, 9, 8, 7, 6, 5, 4, 3, 2};
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            char c = vinNumber.charAt(i);
            int value;
            if (c >= '0' && c <= '9') {
                value = c - '0';
            } else if (c >= 'A' && c <= 'Z') {
                value = (c - 'A' + 1) % 10;
            } else {
                throw new IllegalArgumentException("VIN-номер содержит недопустимые символы");
            }
            sum += value * weights[i];
        }
        int checkDigit = sum % 11;
        if (checkDigit == 10) {
            if (vinNumber.charAt(8) != 'X') {
                throw new IllegalArgumentException("Неверная контрольная сумма VIN-номера");
            }
        } else if (vinNumber.charAt(8) - '0' != checkDigit) {
            throw new IllegalArgumentException("Неверная контрольная сумма VIN-номера");
        }
    }
} 