# CRM Dealership

**CRM-система для автосалона**  
JavaFX + CSS + H2 Database

## Описание

Это прототип CRM-системы для управления автосалоном.

Возможности:
- Управление автомобилями, клиентами, сделками, страховками, оборудованием
- Визуализация данных (графики, таблицы)
- Современный UI на JavaFX
- Хранение данных в H2 Database (встроенная, не требует отдельной установки)

## Технологии

- Java 17+
- JavaFX 17+
- Hibernate (JPA)
- H2 Database (in-memory/file)
- Maven

## Быстрый старт

### 1. Клонируйте репозиторий

```bash
git clone git@github.com:gr3gor4u/crmDip.git
cd crmDip
```

### 2. Соберите проект

```bash
mvn clean install
```

### 3. Запустите приложение

#### Через Maven (рекомендуется для разработки):

```bash
mvn javafx:run
```

#### Или напрямую через JAR (убедитесь, что все JavaFX-модули в classpath):

```bash
java --module-path "путь_к_модулям_javafx" --add-modules javafx.controls,javafx.fxml,javafx.base,javafx.graphics -jar target/javaCrm-1.0-SNAPSHOT.jar
```

### 4. Использование

- Основное окно: управление автомобилями, клиентами, сделками, страховками, оборудованием
- UI стилизован с помощью CSS (`src/main/resources/css/styles.css`)
- Все данные хранятся в H2-файле (`crmdb.mv.db`) в корне проекта

## Структура проекта

- `src/main/java/com/example/javacrm/` — исходный код (модели, контроллеры, сервисы)
- `src/main/resources/fxml/` — FXML-файлы интерфейса
- `src/main/resources/css/` — CSS-стили
- `src/main/resources/schema.sql` — SQL-скрипты для инициализации БД

## Контакты

Автор: [gr3gor4u](https://github.com/gr3gor4u) 