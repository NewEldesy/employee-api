# Étape 1 : build
FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

# Télécharger les dépendances en premier (cache Docker)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -q

# Compiler et packager (sans tests ,ils tournent en CI)
COPY src/ src/
RUN ./mvnw package -DskipTests -q

# Étape 2 : image finale légère
FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
