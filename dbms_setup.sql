-- Database Creation
CREATE DATABASE IF NOT EXISTS dbmsproject; USE dbmsproject;

-- Drop existing tables
DROP TABLE IF EXISTS Transactions; DROP TABLE IF EXISTS Loan; DROP TABLE IF EXISTS Account; DROP TABLE IF EXISTS Customer_Phone; DROP TABLE IF EXISTS Customer; DROP TABLE IF EXISTS Branch; DROP TABLE IF EXISTS Bank; DROP TABLE IF EXISTS Admin;

-- Create Tables
CREATE TABLE Admin (username VARCHAR(50) PRIMARY KEY, password VARCHAR(50) NOT NULL);
CREATE TABLE Bank (Bank_Code VARCHAR(10) PRIMARY KEY, Name VARCHAR(100) NOT NULL, Street VARCHAR(100), City VARCHAR(50), State VARCHAR(50), ZipCode VARCHAR(10));
CREATE TABLE Branch (Branch_Id VARCHAR(10) PRIMARY KEY, Name VARCHAR(100) NOT NULL, Street VARCHAR(100), City VARCHAR(50), State VARCHAR(50), ZipCode VARCHAR(10), Bank_Code VARCHAR(10), FOREIGN KEY (Bank_Code) REFERENCES Bank(Bank_Code));
CREATE TABLE Customer (Customer_Id INT AUTO_INCREMENT PRIMARY KEY, Name VARCHAR(100) NOT NULL, Encrypted_Pin VARCHAR(50) NOT NULL);
CREATE TABLE Customer_Phone (Customer_Id INT, Phone_Number VARCHAR(15), PRIMARY KEY (Customer_Id, Phone_Number), FOREIGN KEY (Customer_Id) REFERENCES Customer(Customer_Id));
CREATE TABLE Account (Account_No INT AUTO_INCREMENT PRIMARY KEY, Account_Type VARCHAR(20) NOT NULL, Balance DECIMAL(15,2) DEFAULT 0.00, Customer_Id INT, Branch_Id VARCHAR(10), Encrypted_Pin VARCHAR(50) NOT NULL, FOREIGN KEY (Customer_Id) REFERENCES Customer(Customer_Id), FOREIGN KEY (Branch_Id) REFERENCES Branch(Branch_Id));
CREATE TABLE Transactions (Transaction_Id VARCHAR(20) PRIMARY KEY, Transaction_Date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, Amount DECIMAL(15,2) NOT NULL, Transaction_Type VARCHAR(20) NOT NULL, Account_No INT, Loan_Id INT, Customer_Id INT, FOREIGN KEY (Account_No) REFERENCES Account(Account_No), FOREIGN KEY (Customer_Id) REFERENCES Customer(Customer_Id));
CREATE TABLE Loan (Loan_Id INT AUTO_INCREMENT PRIMARY KEY, Amount DECIMAL(15,2) NOT NULL, Account_No INT, Customer_Id INT, FOREIGN KEY (Account_No) REFERENCES Account(Account_No), FOREIGN KEY (Customer_Id) REFERENCES Customer(Customer_Id));

-- Insert Sample Data
INSERT INTO Admin VALUES ('admin', 'admin123'), ('supervisor', 'super123'), ('manager', 'manager123');
INSERT INTO Bank VALUES ('B001', 'Main Bank', '123 Main St', 'New York', 'NY', '10001'), ('B002', 'City Bank', '456 Park Ave', 'Los Angeles', 'CA', '90001'), ('B003', 'Metro Bank', '789 Market St', 'Chicago', 'IL', '60601');
INSERT INTO Branch VALUES ('BR001', 'Downtown Branch', '456 Market St', 'New York', 'NY', '10002', 'B001'), ('BR002', 'Westside Branch', '789 Sunset Blvd', 'Los Angeles', 'CA', '90002', 'B002'), ('BR003', 'North Branch', '321 Lake Shore Dr', 'Chicago', 'IL', '60602', 'B003');
INSERT INTO Customer (Name, Encrypted_Pin) VALUES ('John Doe', '1235'), ('Jane Smith', '2346'), ('Robert Johnson', '3457');
INSERT INTO Customer_Phone VALUES (1, '555-0101'), (1, '555-0102'), (2, '555-0201'), (3, '555-0301');
INSERT INTO Account (Account_Type, Customer_Id, Branch_Id, Encrypted_Pin, Balance) VALUES ('Savings', 1, 'BR001', '1235', 5000.00), ('Checking', 1, 'BR001', '2346', 2500.00), ('Savings', 2, 'BR002', '3457', 7500.00), ('Checking', 3, 'BR003', '4568', 10000.00);
INSERT INTO Loan (Amount, Account_No, Customer_Id) VALUES (10000.00, 1, 1), (15000.00, 3, 2), (20000.00, 4, 3);
INSERT INTO Transactions (Transaction_Id, Amount, Transaction_Type, Account_No, Customer_Id) VALUES ('TXN001', 1000.00, 'Deposit', 1, 1), ('TXN002', 500.00, 'Withdrawal', 1, 1), ('TXN003', 2000.00, 'Deposit', 2, 1), ('TXN004', 1000.00, 'Withdrawal', 3, 2), ('TXN005', 3000.00, 'Deposit', 4, 3);

-- Common Queries
-- View all customers with their accounts
SELECT c.Customer_Id, c.Name, a.Account_No, a.Account_Type, a.Balance FROM Customer c JOIN Account a ON c.Customer_Id = a.Customer_Id;

-- View all transactions for a specific account
SELECT t.Transaction_Id, t.Transaction_Date, t.Amount, t.Transaction_Type FROM Transactions t WHERE t.Account_No = 1;

-- View all loans for a customer
SELECT l.Loan_Id, l.Amount, a.Account_No FROM Loan l JOIN Account a ON l.Account_No = a.Account_No WHERE l.Customer_Id = 1;

-- View customer's total balance across all accounts
SELECT c.Customer_Id, c.Name, SUM(a.Balance) as Total_Balance FROM Customer c JOIN Account a ON c.Customer_Id = a.Customer_Id GROUP BY c.Customer_Id, c.Name;

-- View all branches of a bank
SELECT b.Branch_Id, b.Name, b.City FROM Branch b WHERE b.Bank_Code = 'B001';

-- View all phone numbers for a customer
SELECT c.Customer_Id, c.Name, cp.Phone_Number FROM Customer c JOIN Customer_Phone cp ON c.Customer_Id = cp.Customer_Id WHERE c.Customer_Id = 1;

-- View transaction history with account details
SELECT t.Transaction_Id, t.Transaction_Date, t.Amount, t.Transaction_Type, a.Account_No, a.Account_Type FROM Transactions t JOIN Account a ON t.Account_No = a.Account_No ORDER BY t.Transaction_Date DESC;

-- View loan details with customer information
SELECT l.Loan_Id, l.Amount, c.Customer_Id, c.Name, a.Account_No FROM Loan l JOIN Customer c ON l.Customer_Id = c.Customer_Id JOIN Account a ON l.Account_No = a.Account_No;

-- Utility Queries
-- Backup all tables
CREATE TABLE Admin_backup AS SELECT * FROM Admin; CREATE TABLE Bank_backup AS SELECT * FROM Bank; CREATE TABLE Branch_backup AS SELECT * FROM Branch; CREATE TABLE Customer_backup AS SELECT * FROM Customer; CREATE TABLE Customer_Phone_backup AS SELECT * FROM Customer_Phone; CREATE TABLE Account_backup AS SELECT * FROM Account; CREATE TABLE Transactions_backup AS SELECT * FROM Transactions; CREATE TABLE Loan_backup AS SELECT * FROM Loan;

-- Reset auto-increment counters
ALTER TABLE Customer AUTO_INCREMENT = 1; ALTER TABLE Account AUTO_INCREMENT = 1; ALTER TABLE Loan AUTO_INCREMENT = 1;

-- Clear all data from tables (in correct order)
DELETE FROM Transactions; DELETE FROM Loan; DELETE FROM Account; DELETE FROM Customer_Phone; DELETE FROM Customer; DELETE FROM Branch; DELETE FROM Bank; DELETE FROM Admin;

-- Check table sizes
SELECT table_name, table_rows FROM information_schema.tables WHERE table_schema = 'dbmsproject';

-- Check foreign key constraints
SELECT TABLE_NAME, COLUMN_NAME, CONSTRAINT_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME FROM information_schema.KEY_COLUMN_USAGE WHERE TABLE_SCHEMA = 'dbmsproject' AND REFERENCED_TABLE_NAME IS NOT NULL;

-- Optimize all tables
OPTIMIZE TABLE Admin, Bank, Branch, Customer, Customer_Phone, Account, Transactions, Loan;

-- Check table status
SHOW TABLE STATUS FROM dbmsproject; 