# CRM Dealership

**CRM-система для автосалона**  
JavaFX + Spring Boot + PostgreSQL

## Описание

Это прототип CRM-системы для управления автосалоном.  
Возможности:
- Управление автомобилями и сделками
- Визуализация данных (графики, таблицы)
- Современный UI на JavaFX
- Бэкенд на Spring Boot
- Хранение данных в PostgreSQL

## Технологии

- Java 17/21
- JavaFX 17
- Spring Boot
- Hibernate (JPA)
- PostgreSQL
- Maven

## Быстрый старт

### 1. Клонируйте репозиторий

```bash
git clone git@github.com:gr3gor4u/crmDip.git
cd crmDip
```

### 2. Настройте базу данных

Создайте базу данных PostgreSQL с именем `dealership_crm`:

```sql
CREATE DATABASE dealership_crm;
```

Проверьте настройки подключения в `src/main/resources/application.properties`:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/dealership_crm
spring.datasource.username=ваш_пользователь
spring.datasource.password=ваш_пароль
```

### 3. Соберите проект

```bash
mvn clean install
```

### 4. Запустите приложение

#### Через Maven (рекомендуется для разработки):

```bash
mvn javafx:run
```

#### Или напрямую через JAR (убедитесь, что все JavaFX-модули в classpath):

```bash
java --module-path "путь_к_модулям_javafx" --add-modules javafx.controls,javafx.fxml,javafx.base,javafx.graphics -jar target/javaCrm-1.0-SNAPSHOT.jar
```

### 5. Использование

- Основное окно: управление автомобилями, сделками, просмотр статистики.
- UI стилизован с помощью CSS (`src/main/resources/styles/main.css`).

## Скриншоты

_Добавьте сюда скриншоты интерфейса, если есть._

## Структура проекта

- `src/main/java/com/example/javacrm/` — исходный код (модели, контроллеры, сервисы)
- `src/main/resources/fxml/` — FXML-файлы интерфейса
- `src/main/resources/styles/` — CSS-стили
- `src/main/resources/application.properties` — настройки приложения

## Контакты

Автор: [gr3gor4u](https://github.com/gr3gor4u) 