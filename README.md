# Домашнее задание №5

## Тема: Spring boot

Необходимо обновить сервис, который вы разработали в первом задании согласно следующим требованиям и ограничениям

### Требования:
- Spring Boot 3.2.0 с использованием необходимых стартеров
- Обновить тесты
- Аспекты аудита и логирования вынести в стартер, сделать отдельным модулем. Один стартер должен автоматически подключаться, второй через аннотацию @EnableXXX
- Swagger -> SpringDoc

### Запуск
Убедитесь, что у вас установлены следующие компоненты:

- Oracle Java 17
- Apache Maven 3.9.8

Также необходимо установить в локальный репозиторий два стартера:
- [Logging Starter](https://github.com/sabitovka/logging-starter) - Стартер для логирования
- [Audit Starter](https://github.com/sabitovka/audit-starter) - Стартер для аудита действий пользователя 

1. Клонируйте репозиторий и перейдите в папку проекта
   ```shell
   git clone https://github.com/sabitovka/ylab-java-v.git && cd ylab-java-v
   ```
2. Поднимите контейнер c базой данных PostgreSQL
   ```shell
   docker-compose up -d
   ```
3. Скомпилируйте проект с помощью Maven
   ```shell
   mvn clean compile
   ```
4. Запустите приложение с помощью Maven
   ```shell
   mvn -f .\habit-tracker-core\pom.xml spring-boot:run
   ```

### Доступ к Swagger UI
Swagger UI доступен по ссылке `http://<host>:<port>/swagger-ui/`

По-умолчанию в системе зарегистрировано 3 пользователя. Вы можете выполнить вход со следующими параметрами или зарегистрировать нового пользователя

| Имя   | Логин             | Пароль        |
|-------|-------------------|---------------|
| admin | admin@ylab.ru     | admin123      |
| ivan  | ivan@example.com  | ivanpassword  |
| maria | maria@example.com | mariapassword |
