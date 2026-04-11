# Use uma imagem base do Maven com Amazon Corretto para construir a aplicação
FROM maven:3.9.6-amazoncorretto-21 AS build

# Define o diretório de trabalho
WORKDIR /app

# Copia o arquivo pom.xml e baixa as dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código fonte
COPY src ./src

# Constrói a aplicação
RUN mvn clean package -DskipTests

# Use uma imagem base do Amazon Corretto para executar a aplicação
FROM public.ecr.aws/amazoncorretto/amazoncorretto:21-al2023-jdk

# Define o diretório de trabalho
WORKDIR /app

# Copia o JAR construído da fase de build
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta 8080
EXPOSE 8080

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
