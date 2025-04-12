import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class ATMGUI extends JFrame implements ActionListener {
    private JTextField accField, amountField;
    private JPasswordField pinField;
    private JButton loginBtn, checkBalanceBtn, depositBtn, withdrawBtn, registerBtn;
    private JTextArea outputArea;

    private double balance;
    private String accNo;

    private Connection conn;

    public ATMGUI() {
        setTitle("Virtual ATM Simulator");
        setSize(450, 550);  // Increased size for Register button
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JLabel accLabel = new JLabel("Account No:");
        accLabel.setBounds(30, 30, 100, 30);
        add(accLabel);

        accField = new JTextField();
        accField.setBounds(140, 30, 250, 30);
        add(accField);

        JLabel pinLabel = new JLabel("PIN:");
        pinLabel.setBounds(30, 70, 100, 30);
        add(pinLabel);

        pinField = new JPasswordField();
        pinField.setBounds(140, 70, 250, 30);
        add(pinField);

        loginBtn = new JButton("Login");
        loginBtn.setBounds(140, 110, 100, 30);
        loginBtn.addActionListener(this);
        add(loginBtn);

        registerBtn = new JButton("Register");
        registerBtn.setBounds(250, 110, 100, 30);
        registerBtn.addActionListener(this);  // Register Button Action
        add(registerBtn);

        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setBounds(30, 160, 100, 30);
        add(amountLabel);

        amountField = new JTextField();
        amountField.setBounds(140, 160, 250, 30);
        amountField.setEnabled(false);
        add(amountField);

        checkBalanceBtn = new JButton("Check Balance");
        checkBalanceBtn.setBounds(30, 210, 170, 30);
        checkBalanceBtn.setEnabled(false);
        checkBalanceBtn.addActionListener(this);
        add(checkBalanceBtn);

        depositBtn = new JButton("Deposit");
        depositBtn.setBounds(220, 210, 170, 30);
        depositBtn.setEnabled(false);
        depositBtn.addActionListener(this);
        add(depositBtn);

        withdrawBtn = new JButton("Withdraw");
        withdrawBtn.setBounds(30, 260, 360, 30);
        withdrawBtn.setEnabled(false);
        withdrawBtn.addActionListener(this);
        add(withdrawBtn);

        outputArea = new JTextArea();
        outputArea.setBounds(30, 310, 360, 120);
        outputArea.setEditable(false);
        add(outputArea);

        connectToDatabase(); // JDBC Connection
    }

    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // ✅ Load JDBC driver
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_db", "root", "root");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
        }
    }

    private boolean authenticate(String acc, String pin) {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE account_number=? AND pin=?");
            stmt.setString(1, acc);
            stmt.setString(2, pin);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                balance = rs.getDouble("balance");
                accNo = acc;
                return true;
            }
        } catch (SQLException e) {
            outputArea.setText("Error: " + e.getMessage());
        }
        return false;
    }

    private boolean registerNewUser(String acc, String pin) {
        try {
            // Check if account number already exists
            PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM users WHERE account_number=?");
            checkStmt.setString(1, acc);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                outputArea.setText("Account number already exists.");
                return false; // Account already exists
            }

            // Insert new user into the database
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (account_number, pin, balance) VALUES (?, ?, ?)");
            stmt.setString(1, acc);
            stmt.setString(2, pin);
            stmt.setDouble(3, 0.0); // New user with zero balance
            int rows = stmt.executeUpdate();
            
            if (rows > 0) {
                outputArea.setText("Registration successful! You can now log in.");
                return true; // Registration successful
            } else {
                outputArea.setText("Registration failed.");
                return false;
            }
        } catch (SQLException e) {
            outputArea.setText("Error: " + e.getMessage());
        }
        return false;
    }

    private void updateBalanceInDB() {
        try {
            PreparedStatement stmt = conn.prepareStatement("UPDATE users SET balance=? WHERE account_number=?");
            stmt.setDouble(1, balance);
            stmt.setString(2, accNo);
            stmt.executeUpdate();
        } catch (SQLException e) {
            outputArea.setText("DB Update Error: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginBtn) {
            String acc = accField.getText();
            String pin = new String(pinField.getPassword()); // 🔐 Get password securely
            if (authenticate(acc, pin)) {
                outputArea.setText("Login successful!");
                enableButtons();
            } else {
                outputArea.setText("Invalid credentials.");
            }
        } else if (e.getSource() == registerBtn) {
            String acc = accField.getText();
            String pin = new String(pinField.getPassword());
            if (registerNewUser(acc, pin)) {
                enableButtons(); // Enable the buttons after successful registration
            }
        } else if (e.getSource() == checkBalanceBtn) {
            outputArea.setText("Your balance is: ₹" + balance);
        } else if (e.getSource() == depositBtn) {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount < 0) throw new Exception("Cannot deposit negative amount.");
                balance += amount;
                updateBalanceInDB();
                outputArea.setText("₹" + amount + " deposited.\nCurrent Balance: ₹" + balance);
            } catch (Exception ex) {
                outputArea.setText("Invalid amount: " + ex.getMessage());
            }
        } else if (e.getSource() == withdrawBtn) {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount < 0) throw new Exception("Invalid withdrawal amount.");
                if (amount > balance) throw new Exception("Insufficient balance.");
                balance -= amount;
                updateBalanceInDB();
                outputArea.setText("₹" + amount + " withdrawn.\nCurrent Balance: ₹" + balance);
            } catch (Exception ex) {
                outputArea.setText("Transaction failed: " + ex.getMessage());
            }
        }
    }

    private void enableButtons() {
        amountField.setEnabled(true);
        checkBalanceBtn.setEnabled(true);
        depositBtn.setEnabled(true);
        withdrawBtn.setEnabled(true);
    }

    public static void main(String[] args) {
        new ATMGUI().setVisible(true);
    }
}
