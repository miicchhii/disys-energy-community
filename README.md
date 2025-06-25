# disys-energy-community

## Start the Project

Follow these steps to start the entire application suite:

### 1. Start Docker Container
infrastructure/docker-compose.yml

### 2. Start Backend
backend/src/main/java/at/technikumwien/backend/BackendApplication.java

### 3. Start Frontend
frontend/src/main/java/at/technikumwien/frontend/EnergyApplication.java 
or go to the maven sidebar and run frontend/plugins/javafx/javafx:run

### 4. Start Rabbitmq Services

- **Usage Service**
  rabbitmq-services/usage-service/src/main/java/at/fhtechnikum/usage/UsageServiceApplication.java
- **Percentage Service**
  rabbitmq-services/percentage-service/src/main/java/at/fhtechnikum/percentage/PercentageServiceApplication.java
 
### 5. Start Clients
  
- **Producer Client**
  ProducerClient/src/main/java/at/technikumwien/producer/EnergyProducer.java
- **User Client**
  UserClient/src/main/java/at/technikumwien/user/EnergyUser.java

