
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class BankingAppGUI extends JFrame {
    private Connection conn;
    private static final int INTEGER_ENCRYPTION_KEY = 42;

    public BankingAppGUI() {
        setTitle("Banking Application");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        connectDatabase();
        mainLoginUI();
    }
    private JPanel createBackgroundPanelWithButtons(JPanel buttonPanel) {
        // Load the original background image
        ImageIcon originalIcon = new ImageIcon("C:\\Users\\aryan\\IdeaProjects\\dbms\\Bank_Project_Background_Image.png");

        // Get the screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // Get the original image dimensions
        int originalWidth = originalIcon.getIconWidth();
        int originalHeight = originalIcon.getIconHeight();

        // Calculate scaling ratios for width and height
        double widthRatio = (double) screenSize.width / originalWidth;
        double heightRatio = (double) screenSize.height / originalHeight;

        // Use the smaller ratio to maintain the aspect ratio and avoid distortion
        double scaleRatio = Math.min(widthRatio, heightRatio);

        // Scale the image based on the calculated ratio
        Image scaledImage = originalIcon.getImage().getScaledInstance(
                (int) (originalWidth * scaleRatio),
                (int) (originalHeight * scaleRatio),
                Image.SCALE_SMOOTH
        );

        // Convert the scaled image to an ImageIcon
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        // Create a JLabel to hold the background image
        JLabel backgroundLabel = new JLabel(scaledIcon);
        backgroundLabel.setLayout(new GridBagLayout());

        // Place the button panel on the left-center with top and bottom padding
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.insets = new Insets(350, 250, 50, 20); // Padding from top, left, bottom, and right

        // Add the button panel to the background label
        backgroundLabel.add(buttonPanel, gbc);

        // Create the background panel with BorderLayout
        JPanel backgroundPanel = new JPanel(new BorderLayout());

        // Set the background color to white to create padding effect around the image
        backgroundPanel.setBackground(Color.BLACK);

        // Add the background label to the center of the background panel
        backgroundPanel.add(backgroundLabel, BorderLayout.CENTER);

        return backgroundPanel;
    }


    private JButton createButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 18)); // Larger font
        btn.setPreferredSize(new Dimension(250, 50));  // Wider and taller
        btn.setFocusPainted(false);
        btn.addActionListener(action);
        return btn;
    }


    private void connectDatabase() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbmsproject", "root", "1234567890");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void adminLogin() {
        String user = JOptionPane.showInputDialog(this, "Enter Admin Username:");
        String pass = JOptionPane.showInputDialog(this, "Enter Admin Password:");

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Admin WHERE username=? AND password=?");
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                adminPanel();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void mainLoginUI() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setOpaque(false);
        panel.add(createButton("Admin Login", e -> adminLogin()));
        panel.add(createButton("Customer Login", e -> customerLogin()));
        panel.add(createButton("Add Customer", e -> addCustomer()));
        panel.add(createButton("Exit", e -> System.exit(0)));

        setContentPane(createBackgroundPanelWithButtons(panel));
        revalidate();
        setVisible(true);
    }
    private void adminPanel() {
        JPanel panel = new JPanel(new GridLayout(9, 1, 10, 10));
        panel.setOpaque(false);
        panel.add(createButton("Add Bank", e -> addBank()));
        panel.add(createButton("View Banks", e -> viewTable("Bank")));
        panel.add(createButton("Add Branch", e -> addBranch()));
        panel.add(createButton("View Branches", e -> viewTable("Branch")));
        panel.add(createButton("View Customers", e -> viewTable("Customer")));
        panel.add(createButton("View Accounts", e -> viewTable("Account")));
        panel.add(createButton("View Transactions", e -> viewTable("Transactions")));
        panel.add(createButton("View Loans", e -> viewTable("Loan")));  // New button for View Loans
        panel.add(createButton("Main Menu", e -> mainLoginUI()));

        setContentPane(createBackgroundPanelWithButtons(panel));
        revalidate();
        setVisible(true);
    }


    private void customerLogin() {
        String custId = verifyCustomerLogin();
        if (custId == null) {
            JOptionPane.showMessageDialog(this, "Login failed.");
            return;
        }

        JPanel panel = new JPanel(new GridLayout(10, 1, 10, 10));
        panel.setOpaque(false);
        panel.add(createButton("Deposit", e -> deposit(custId)));
        panel.add(createButton("Withdraw", e -> withdraw(custId)));
        panel.add(createButton("Check Balance", e -> checkBalance(custId)));
        panel.add(createButton("View Passbook", e -> viewPassbook(custId)));
        panel.add(createButton("Add Customer Phone", e -> addCustomerPhone(custId)));
        panel.add(createButton("Add Account", e -> addAccount(custId)));
        panel.add(createButton("Add Loan", e -> addLoan(custId)));
        panel.add(createButton("Repay Loan", e -> repayLoan(custId)));
        panel.add(createButton("Main Menu", e -> mainLoginUI()));
        panel.add(createButton("Exit", e -> System.exit(0)));

        setContentPane(createBackgroundPanelWithButtons(panel));
        revalidate();
        setVisible(true);
    }


    private void viewPassbook(String custId) {
        String accNo = JOptionPane.showInputDialog(this, "Enter Account Number:");
        String pin = JOptionPane.showInputDialog(this, "Enter PIN:");

        if (!verifyAccountPin(accNo, pin)) {
            JOptionPane.showMessageDialog(this, "Invalid PIN or account number.");
            return;
        }

        if (!verifyAccountOwnership(custId, accNo)) {
            JOptionPane.showMessageDialog(this, "Unauthorized access or invalid account.");
            return;
        }

        StringBuilder sb = new StringBuilder();

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT Transaction_Date, Transaction_Type, Amount FROM Transactions WHERE Account_No = ? ORDER BY Transaction_Date DESC"
            );
            ps.setString(1, accNo);
            ResultSet rs = ps.executeQuery();

            sb.append("=== Transaction History ===\n");
            boolean hasTx = false;
            while (rs.next()) {
                hasTx = true;
                sb.append("Date: ").append(rs.getTimestamp("Transaction_Date"))
                        .append(", Type: ").append(rs.getString("Transaction_Type"))
                        .append(", Amount: ").append(rs.getDouble("Amount"))
                        .append("\n");
            }
            if (!hasTx) sb.append("No transactions found.\n");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching transactions.");
            e.printStackTrace();
            return;
        }

        try {
            PreparedStatement psLoan = conn.prepareStatement(
                    "SELECT Loan_Id, Amount FROM Loan WHERE Account_No = ?"
            );
            psLoan.setString(1, accNo);
            ResultSet rsLoan = psLoan.executeQuery();

            sb.append("\n=== Loan Details ===\n");
            boolean hasLoans = false;
            while (rsLoan.next()) {
                hasLoans = true;
                sb.append("Loan ID: ").append(rsLoan.getInt("Loan_Id"))
                        .append(", Amount: ").append(rsLoan.getDouble("Amount"))
                        .append("\n");
            }
            if (!hasLoans) sb.append("No loans found.\n");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching loan details.");
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(this, sb.toString());
    }

    private void addBranch() {
        String id = JOptionPane.showInputDialog(this, "Enter Branch ID:");
        String name = JOptionPane.showInputDialog(this, "Enter Branch Name:");
        String street = JOptionPane.showInputDialog(this, "Enter Street:");
        String city = JOptionPane.showInputDialog(this, "Enter City:");
        String state = JOptionPane.showInputDialog(this, "Enter State:");
        String zip = JOptionPane.showInputDialog(this, "Enter ZipCode:");
        String bankCode = JOptionPane.showInputDialog(this, "Enter Bank Code:");

        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO Branch VALUES (?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, id);
            ps.setString(2, name);
            ps.setString(3, street);
            ps.setString(4, city);
            ps.setString(5, state);
            ps.setString(6, zip);
            ps.setString(7, bankCode);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Branch added.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to add branch.");
            e.printStackTrace();
        }
    }
    private boolean verifyAccountOwnership(String custId, String accNum) {
        try (Connection conn = getConnection()) {
            String query = "SELECT * FROM account WHERE Account_No = ? AND Customer_Id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, accNum); // ✅ Account_No first
            stmt.setString(2, custId); // ✅ Customer_Id second
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // no need for rs.getInt(1)
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    private void addBank() {
        String code = JOptionPane.showInputDialog(this, "Enter Bank Code:");
        String name = JOptionPane.showInputDialog(this, "Enter Bank Name:");
        String street = JOptionPane.showInputDialog(this, "Enter Street:");
        String city = JOptionPane.showInputDialog(this, "Enter City:");
        String state = JOptionPane.showInputDialog(this, "Enter State:");
        String zip = JOptionPane.showInputDialog(this, "Enter ZipCode:");

        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO Bank VALUES (?, ?, ?, ?, ?, ?)");
            ps.setString(1, code);
            ps.setString(2, name);
            ps.setString(3, street);
            ps.setString(4, city);
            ps.setString(5, state);
            ps.setString(6, zip);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Bank added.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to add bank.");
            e.printStackTrace();
        }
    }
    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/dbmsproject";
        String username = "root";
        String password = "1234567890";
        return DriverManager.getConnection(url, username, password);
    }
    private void repayLoan(String custId) {
        String loanIdStr = JOptionPane.showInputDialog(this, "Enter Loan ID to Repay:");
        String repayAmtStr = JOptionPane.showInputDialog(this, "Enter Amount to Repay:");

        try {
            int loanId = Integer.parseInt(loanIdStr);
            double repayAmt = Double.parseDouble(repayAmtStr);

            // Get the loan details from the Loan table (amount to repay and account number)
            PreparedStatement loanStmt = conn.prepareStatement(
                    "SELECT Amount, Account_No FROM Loan WHERE Loan_Id = ? AND Customer_Id = ?"
            );
            loanStmt.setInt(1, loanId);
            loanStmt.setString(2, custId);
            ResultSet loanRs = loanStmt.executeQuery();

            if (!loanRs.next()) {
                JOptionPane.showMessageDialog(this, "Loan not found or unauthorized access.");
                return;
            }

            double remainingAmt = loanRs.getDouble("Amount");
            String accNo = loanRs.getString("Account_No");

            // Verify account ownership from the Loan table
            if (!verifyAccountOwnership(custId, accNo)) {
                JOptionPane.showMessageDialog(this, "Unauthorized access to this account.");
                return;
            }

            // Verify PIN
            if (!verifyPin(accNo)) {
                JOptionPane.showMessageDialog(this, "PIN verification failed.");
                return;
            }

            // Get the loan balance (the Amount field in the Loan table)
            double balance = remainingAmt;

            // Check if there's sufficient loan balance
            if (balance < repayAmt) {
                JOptionPane.showMessageDialog(this, "Insufficient balance to repay loan.");
                return;
            }

            // Check if the repayment amount does not exceed the remaining loan amount
            if (repayAmt > remainingAmt) {
                JOptionPane.showMessageDialog(this, "Repayment exceeds remaining loan amount.");
                return;
            }

            // Begin transaction
            conn.setAutoCommit(false);

            // Update the loan amount: Deduct repayment amount from the loan balance
            double newLoanAmount = remainingAmt - repayAmt;
            PreparedStatement updateLoan = conn.prepareStatement(
                    "UPDATE Loan SET Amount = ? WHERE Loan_Id = ?"
            );
            updateLoan.setDouble(1, newLoanAmount);
            updateLoan.setInt(2, loanId);
            int updateLoanRows = updateLoan.executeUpdate();

            // Commit transaction
            conn.commit();

            // Store the transaction in the transactions table
            String txnId = "TXN" + System.currentTimeMillis(); // Generate unique Transaction ID
            PreparedStatement txnPs = conn.prepareStatement(
                    "INSERT INTO transactions (Transaction_Id, Transaction_Date, Amount, Transaction_Type, Account_No, Loan_Id, Customer_Id) VALUES (?, NOW(), ?, 'Loan Payment', ?, ?, ?)"
            );
            txnPs.setString(1, txnId);
            txnPs.setDouble(2, repayAmt);
            txnPs.setString(3, accNo);
            txnPs.setInt(4, loanId);
            txnPs.setInt(5, Integer.parseInt(custId));
            txnPs.executeUpdate();

            // Show success message
            JOptionPane.showMessageDialog(this, "Loan repaid successfully! Remaining loan amount: " + newLoanAmount);

        } catch (Exception e) {
            try {
                conn.rollback(); // Rollback on failure
            } catch (SQLException se) {
                se.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Loan repayment failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                conn.setAutoCommit(true); // Restore autocommit
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void addCustomer() {
        String name = JOptionPane.showInputDialog(this, "Enter Customer Name:");
        if (name == null || name.trim().isEmpty()) return;

        String pin = JOptionPane.showInputDialog(this, "Set a 4-digit Customer PIN:");
        if (pin == null || !pin.matches("\\d{4}")) {
            JOptionPane.showMessageDialog(this, "Invalid PIN. Must be 4 digits.");
            return;
        }

        String encryptedPin = encryptData(pin);

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO Customer (Name, Encrypted_Pin) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, name);
            ps.setString(2, encryptedPin);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                JOptionPane.showMessageDialog(this, "Customer added.\nCustomer ID: " + generatedId);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding customer.");
            e.printStackTrace();
        }
    }

    private String encryptData(String input) {
        StringBuilder encrypted = new StringBuilder();
        for (char c : input.toCharArray()) {
            encrypted.append((char) (c + 1));
        }
        return encrypted.toString();
    }

    private String decryptData(String input) {
        StringBuilder decrypted = new StringBuilder();
        for (char c : input.toCharArray()) {
            decrypted.append((char) (c - 1));
        }
        return decrypted.toString();
    }

    private int encryptInt(int value) {
        return value + INTEGER_ENCRYPTION_KEY;
    }

    private int decryptInt(int value) {
        return value - INTEGER_ENCRYPTION_KEY;
    }
    private String verifyCustomerLogin() {
        String custId = JOptionPane.showInputDialog(this, "Enter Customer ID:");
        String inputPin = JOptionPane.showInputDialog(this, "Enter Customer PIN:");
        if (custId == null || inputPin == null) return null;

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT Encrypted_Pin FROM Customer WHERE Customer_Id = ?");
            ps.setString(1, custId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String encryptedPin = rs.getString("Encrypted_Pin");
                String decryptedPin = decryptData(encryptedPin);
                if (inputPin.equals(decryptedPin)) {
                    return custId;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(this, "Login failed.");
        return null;
    }


    private void addCustomerPhone(String custId) {
        String phone = JOptionPane.showInputDialog(this, "Enter Phone Number:");

        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO Customer_Phone VALUES (?, ?)");
            ps.setString(1, custId);
            ps.setString(2, phone);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Phone added for customer.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to add phone.");
            e.printStackTrace();
        }
    }
    private void addLoan(String custId) {
        String accNo = JOptionPane.showInputDialog(this, "Enter Account Number:");

        // Optional: check account ownership like in withdraw
        if (!verifyAccountOwnership(custId, accNo)) {
            JOptionPane.showMessageDialog(this, "Unauthorized access or invalid account.");
            return;
        }

        String amtStr = JOptionPane.showInputDialog(this, "Enter Loan Amount:");

        try {
            double amt = Double.parseDouble(amtStr);
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO Loan (Amount, Account_No, Customer_Id) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setDouble(1, amt);
            ps.setString(2, accNo);
            ps.setString(3, custId);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int loanId = rs.getInt(1);
                    JOptionPane.showMessageDialog(this, "Loan added successfully!\nLoan ID: " + loanId);

                    // Insert the transaction record in the transactions table
                    String txnId = "TXN" + System.currentTimeMillis(); // Generate unique Transaction ID
                    PreparedStatement txnPs = conn.prepareStatement(
                            "INSERT INTO transactions (Transaction_Id, Transaction_Date, Amount, Transaction_Type, Account_No, Loan_Id, Customer_Id) VALUES (?, NOW(), ?, 'Loan', ?, ?, ?)"
                    );
                    txnPs.setString(1, txnId);
                    txnPs.setDouble(2, amt);
                    txnPs.setString(3, accNo);
                    txnPs.setInt(4, loanId);
                    txnPs.setInt(5, Integer.parseInt(custId));
                    txnPs.executeUpdate();
                } else {
                    JOptionPane.showMessageDialog(this, "Loan added, but failed to retrieve Loan ID.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Loan addition failed.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding loan.");
            e.printStackTrace();
        }
    }

    private void addAccount(String custId) {
        String type = JOptionPane.showInputDialog(this, "Enter Account Type:");
        String branchId = JOptionPane.showInputDialog(this, "Enter Branch ID:");
        String pin = JOptionPane.showInputDialog(this, "Set a 4-digit PIN:");

        if (pin == null || pin.length() != 4 || !pin.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Invalid PIN. Must be 4 digits.");
            return;
        }

        try {
            String encryptedPin = encryptData(pin);

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO Account (Account_Type, Balance, Customer_Id, Branch_Id, Encrypted_Pin) VALUES (?, 0, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, type);
            ps.setString(2, custId);
            ps.setString(3, branchId);
            ps.setString(4, encryptedPin);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int accNo = rs.getInt(1);
                JOptionPane.showMessageDialog(this, "Account created.\nAccount No: " + accNo);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Account creation failed.");
            e.printStackTrace();
        }
    }


    private boolean verifyPin(String accNo) {
        String inputPin = JOptionPane.showInputDialog(this, "Enter PIN:");
        if (inputPin == null) return false;

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT Encrypted_Pin FROM Account WHERE Account_No = ?");
            ps.setString(1, accNo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String encryptedPin = rs.getString("Encrypted_Pin");
                String decryptedPin = decryptData(encryptedPin);
                return inputPin.equals(decryptedPin);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    private void deposit(String custId) {
        String accNo = JOptionPane.showInputDialog(this, "Enter Account Number:");
        String pin = JOptionPane.showInputDialog(this, "Enter PIN:");

        if (!verifyAccountPin(accNo, pin)) {
            JOptionPane.showMessageDialog(this, "Invalid PIN or account number.");
            return;
        }

        if (!verifyAccountOwnership(custId, accNo)) {
            JOptionPane.showMessageDialog(this, "Unauthorized access or invalid account.");
            return;
        }

        String amtStr = JOptionPane.showInputDialog(this, "Enter Amount to Deposit:");
        try {
            double amt = Double.parseDouble(amtStr);
            PreparedStatement ps = conn.prepareStatement("UPDATE Account SET Balance = Balance + ? WHERE Account_No = ?");
            ps.setDouble(1, amt);
            ps.setString(2, accNo);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                insertTransaction(accNo, "Deposit", amt);
                JOptionPane.showMessageDialog(this, "Deposit Successful.");
            } else {
                JOptionPane.showMessageDialog(this, "Account not found.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Deposit failed.");
            e.printStackTrace();
        }
    }

    private boolean verifyAccountPin(String accNo, String enteredPin) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT Encrypted_Pin FROM Account WHERE Account_No = ?");
            ps.setString(1, accNo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String encryptedPin = rs.getString("Encrypted_Pin");
                String decryptedPin = decryptData(encryptedPin);
                return decryptedPin.equals(enteredPin);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void withdraw(String custId) {
        String accNo = JOptionPane.showInputDialog(this, "Enter Account Number:");
        if (!verifyAccountOwnership(custId, accNo)) {
            JOptionPane.showMessageDialog(this, "Unauthorized access or invalid account.");
            return;
        }

        if (!verifyPin(accNo)) {
            JOptionPane.showMessageDialog(this, "PIN verification failed.");
            return;
        }

        String amtStr = JOptionPane.showInputDialog(this, "Enter Amount to Withdraw:");

        try {
            double amt = Double.parseDouble(amtStr);
            PreparedStatement check = conn.prepareStatement("SELECT Balance FROM Account WHERE Account_No = ?");
            check.setString(1, accNo);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                double bal = rs.getDouble("Balance");
                if (bal >= amt) {
                    PreparedStatement ps = conn.prepareStatement("UPDATE Account SET Balance = Balance - ? WHERE Account_No = ?");
                    ps.setDouble(1, amt);
                    ps.setString(2, accNo);
                    ps.executeUpdate();

                    insertTransaction(accNo, "Withdrawal", amt);
                    JOptionPane.showMessageDialog(this, "Withdrawal Successful.");
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient Balance.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Account not found.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Withdrawal failed.");
            e.printStackTrace();
        }
    }

    private void checkBalance(String custId) {
        String accNo = JOptionPane.showInputDialog(this, "Enter Account Number:");
        if (!verifyAccountOwnership(custId, accNo)) {
            JOptionPane.showMessageDialog(this, "Unauthorized access or invalid account.");
            return;
        }

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT Balance FROM Account WHERE Account_No = ?");
            ps.setString(1, accNo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("Balance");
                JOptionPane.showMessageDialog(this, "Current Balance: ₹" + balance);
            } else {
                JOptionPane.showMessageDialog(this, "Account not found.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error checking balance.");
            e.printStackTrace();
        }
    }


    private void insertTransaction(String accNo, String type, double amt) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Transactions (Transaction_Id, Amount, Transaction_Type, Account_No, Customer_Id) VALUES (?, ?, ?, ?, (SELECT Customer_Id FROM Account WHERE Account_No = ?))"
        );
        ps.setString(1, "TXN" + System.currentTimeMillis());
        ps.setDouble(2, amt);
        ps.setString(3, type);
        ps.setString(4, accNo);
        ps.setString(5, accNo);
        ps.executeUpdate();
    }

    private void viewTable(String table) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + table);

            StringBuilder data = new StringBuilder();
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    data.append(meta.getColumnName(i)).append(": ").append(rs.getString(i)).append("   ");
                }
                data.append("\n");
            }

            JTextArea textArea = new JTextArea(data.toString());
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));
            JOptionPane.showMessageDialog(this, scrollPane, table + " Records", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error displaying " + table + " data.");
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BankingAppGUI().setVisible(true));
    }

}


