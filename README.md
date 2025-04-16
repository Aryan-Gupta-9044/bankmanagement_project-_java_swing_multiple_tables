# Banking Application

A Java-based banking application with a GUI interface for managing banks, branches, customers, accounts, and transactions.

## Features

- Admin and Customer login systems
- Bank and Branch management
- Customer account management
- Transaction processing (deposits, withdrawals)
- Loan management
- Secure PIN encryption

## Requirements

- Java JDK 8 or higher
- MySQL Database
- MySQL Connector/J

## Database Setup

The application uses a MySQL database named `dbmsproject`. Here are the SQL queries to set up the database:

```sql
-- Create database
CREATE DATABASE dbmsproject;
USE dbmsproject;

-- Create Admin table
CREATE TABLE Admin (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(50) NOT NULL
);

-- Create Bank table
CREATE TABLE Bank (
    Bank_Code VARCHAR(10) PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    Street VARCHAR(100),
    City VARCHAR(50),
    State VARCHAR(50),
    ZipCode VARCHAR(10)
);

-- Create Branch table
CREATE TABLE Branch (
    Branch_Id VARCHAR(10) PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    Street VARCHAR(100),
    City VARCHAR(50),
    State VARCHAR(50),
    ZipCode VARCHAR(10),
    Bank_Code VARCHAR(10),
    FOREIGN KEY (Bank_Code) REFERENCES Bank(Bank_Code)
);

-- Create Customer table
CREATE TABLE Customer (
    Customer_Id INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    Encrypted_Pin VARCHAR(50) NOT NULL
);

-- Create Customer_Phone table
CREATE TABLE Customer_Phone (
    Customer_Id INT,
    Phone_Number VARCHAR(15),
    PRIMARY KEY (Customer_Id, Phone_Number),
    FOREIGN KEY (Customer_Id) REFERENCES Customer(Customer_Id)
);

-- Create Account table
CREATE TABLE Account (
    Account_No INT AUTO_INCREMENT PRIMARY KEY,
    Account_Type VARCHAR(20) NOT NULL,
    Balance DECIMAL(15,2) DEFAULT 0.00,
    Customer_Id INT,
    Branch_Id VARCHAR(10),
    Encrypted_Pin VARCHAR(50) NOT NULL,
    FOREIGN KEY (Customer_Id) REFERENCES Customer(Customer_Id),
    FOREIGN KEY (Branch_Id) REFERENCES Branch(Branch_Id)
);

-- Create Transactions table
CREATE TABLE Transactions (
    Transaction_Id VARCHAR(20) PRIMARY KEY,
    Transaction_Date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Amount DECIMAL(15,2) NOT NULL,
    Transaction_Type VARCHAR(20) NOT NULL,
    Account_No INT,
    Loan_Id INT,
    Customer_Id INT,
    FOREIGN KEY (Account_No) REFERENCES Account(Account_No),
    FOREIGN KEY (Customer_Id) REFERENCES Customer(Customer_Id)
);

-- Create Loan table
CREATE TABLE Loan (
    Loan_Id INT AUTO_INCREMENT PRIMARY KEY,
    Amount DECIMAL(15,2) NOT NULL,
    Account_No INT,
    Customer_Id INT,
    FOREIGN KEY (Account_No) REFERENCES Account(Account_No),
    FOREIGN KEY (Customer_Id) REFERENCES Customer(Customer_Id)
);
```

## Sample Data Insertion

```sql
-- Insert sample admin
INSERT INTO Admin VALUES ('admin', 'admin123');

-- Insert sample bank
INSERT INTO Bank VALUES ('B001', 'Main Bank', '123 Main St', 'New York', 'NY', '10001');

-- Insert sample branch
INSERT INTO Branch VALUES ('BR001', 'Downtown Branch', '456 Market St', 'New York', 'NY', '10002', 'B001');

-- Insert sample customer
INSERT INTO Customer (Name, Encrypted_Pin) VALUES ('John Doe', 'encrypted_pin_here');

-- Insert sample account
INSERT INTO Account (Account_Type, Customer_Id, Branch_Id, Encrypted_Pin) 
VALUES ('Savings', 1, 'BR001', 'encrypted_pin_here');
```

## Running the Application

1. Make sure MySQL is running and the database is set up
2. Compile the Java files:
   ```
   javac BankingAppGUI.java
   ```
3. Run the application:
   ```
   java BankingAppGUI
   ```

## Security Features

- PIN encryption for customer and account security
- Account ownership verification
- Transaction logging
- Secure admin authentication

## Contributing

Feel free to submit issues and enhancement requests. 