# Этап 1: Сборка
FROM gradle:8-jdk AS build

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем все файлы проекта в контейнер
COPY src/main .

# Выполняем сборку приложения
RUN gradle build --no-daemon

# Этап 2: Запуск
FROM openjdk:21-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем только собранный jar-файл из предыдущего этапа
COPY --from=build /app/build/libs/*.jar app.jar

# Открываем порт, на котором будет работать приложение
EXPOSE 8095

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]