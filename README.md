### Процедура запуска автотестов

Перед выполнением команды git clone на устройстве должны быть установлены:
- IntelliJ IDEA
- JDK 11
- Gradle
- Git
- Docker Desktop

Шаги:
1. Скопировать репозиторий на своё устройство с помощью команды git clone.
2. Открыть проект в IntelliJ IDEA.
3. Запустить контейнеры командой docker compose up
4. Запустить приложение командой java -jar aqa-shop.jar
5. Запустить автотесты: ./gradlew clean test
6. Сформировать отчёт: ./gradlew allureReport
7. Проверить результаты: build — reports — allure-report — index.html