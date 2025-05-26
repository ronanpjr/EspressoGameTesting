# ☕ EspressoGameTesting

Bem-vindo ao EspressoGameTesting! Sua cafeteria de testes

### ✅ Pré-requisitos para rodar o projeto

- **JDK 11 ou superior**
- **Apache Tomcat 9 ou superior**
- **Apache Maven**
- **MySQL Server**
- **IDE (Eclipse, IntelliJ ou VS Code)**

## ⚙️ Passo a passo para rodar o projeto

### 1. Clone o repositório

Abra o prompt e execute esse comando:   
**git clone https://github.com/seu-usuario/EspressoGameTesting.git**  
**cd EspressoGameTesting**

### 2. Instale as dependencias via Maven
execute:  
**mvn clean install**

### 3. Configuração do Banco de Dados
Crie o banco executando o script SQL localizado em:  
db/MySQL/create.sql  
Você pode usar o terminal do MySQL:  
**mysql -u root -p < db/MySQL/create.sql**

### 4. Build e Deploy
- **1. Gerar o arquivo .war**
  execute:  
  **mvn package**
  O arquivo .war será gerado em target/EspressoGameTesting.war.
- **2. Deploy no Apache Tomcat**
  Copie o arquivo .war para a pasta webapps do seu Tomcat.  
  Inicie o servidor com startup.bat (Windows) ou startup.sh (Linux/macOS).  
  Acesse: http://localhost:8080/EspressoTesting
  

![image](https://github.com/user-attachments/assets/0659868c-347e-4d28-866a-2a47ba7c7a5e)
