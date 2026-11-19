# QA API Testing Framework

Этот проект — демонстрационный фреймворк для автоматизации тестирования, созданный в рамках подготовки к техническим собеседованиям. Он покрывает ключевые аспекты тестирования бэкенда: работу с REST API, асинхронность (Kafka), базы данных и анализ логов.

## 🛠️ Стек технологий
*   **Java 17**
*   **Maven** (управление зависимостями)
*   **RestAssured** (тестирование API)
*   **TestNG** (тестовый фреймворк)
*   **Kafka Client** (работа с брокером сообщений)
*   **JDBC / PostgreSQL** (проверка данных в БД)
*   **WireMock** (мок-сервер для негативных тестов)
*   **OpenSearch** (поиск и анализ логов)

## 📂 Структура проекта
*   `base/` — базовый класс `BaseTest` для настройки RestAssured.
*   `tests/` — набор автотестов, разделенных по функциональности.
*   `tools/` — утилиты для настройки WireMock.
*   `resources/` — JSON-схемы для валидации контрактов.

## 🚀 Как запустить локально

Для запуска тестов вам понадобится **Docker** (для инфраструктуры) и **JDK 17**.

### 1. Поднять инфраструктуру (Docker)
Выполните команды в терминале:

```bash
# Kafka
docker run -d --name kafka -p 9092:9092 apache/kafka

# PostgreSQL
docker run -d --name my-postgres -p 5432:5432 -e POSTGRES_PASSWORD=mysecretpassword -e POSTGRES_DB=mydb postgres:16

# WireMock
docker run -d --name wiremock -p 8080:8080 wiremock/wiremock:latest