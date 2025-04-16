# Installation Guide

## Prerequisites

1. Java Development Kit (JDK) 8 or higher
2. MySQL Server 8.0 or higher
3. MySQL Connector/J

## Installation Steps

### 1. Install Java
- Download and install JDK from [Oracle's website](https://www.oracle.com/java/technologies/downloads/)
- Set JAVA_HOME environment variable
- Add Java to your system PATH

### 2. Install MySQL
- Download and install MySQL Server from [MySQL's website](https://dev.mysql.com/downloads/mysql/)
- During installation, note down the root password
- Make sure MySQL service is running

### 3. Set Up Database
1. Open MySQL Command Line Client or MySQL Workbench
2. Run the database setup script:
   ```bash
   mysql -u root -p < database_setup.sql
   ```
3. Enter your MySQL root password when prompted

### 4. Install MySQL Connector/J
1. Download MySQL Connector/J from [MySQL's website](https://dev.mysql.com/downloads/connector/j/)
2. Extract the downloaded file
3. Copy the JAR file (mysql-connector-java-8.0.33.jar) to your project directory

### 5. Run the Application
1. Open Command Prompt in the project directory
2. Run the build script:
   ```bash
   build.bat
   ```
   Or compile and run manually:
   ```bash
   javac -cp mysql-connector-java-8.0.33.jar BankingAppGUI.java
   java -cp .;mysql-connector-java-8.0.33.jar BankingAppGUI
   ```

## Troubleshooting

### Common Issues

1. **Class Not Found Error**
   - Make sure MySQL Connector/J is in the classpath
   - Check if the JAR file name matches exactly

2. **Database Connection Error**
   - Verify MySQL service is running
   - Check if the database credentials in BankingAppGUI.java are correct
   - Ensure the database and tables are created properly

3. **Compilation Error**
   - Verify JDK is installed correctly
   - Check if JAVA_HOME is set properly

### Getting Help

If you encounter any issues:
1. Check the error messages carefully
2. Refer to the documentation in README.md
3. Contact the development team 