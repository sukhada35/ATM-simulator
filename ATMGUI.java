import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class ATMGUI extends JFrame implements ActionListener {
    private JTextField accField, nameField, amountField;
    private JPasswordField pinField;
    private JButton loginBtn, registerBtn, checkBalanceBtn, depositBtn, withdrawBtn, logoutBtn;
    private JTextArea outputArea;

    private double balance;
    private String accNo;
    private String userName;
    private Connection conn;

    public ATMGUI() {
        setTitle("Virtual ATM Simulator");
        setSize(500, 580);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JLabel accLabel = new JLabel("Account No:");
        accLabel.setBounds(30, 30, 100, 30);
        add(accLabel);

        accField = new JTextField();
        accField.setBounds(140, 30, 300, 30);
        add(accField);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(30, 70, 100, 30);
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(140, 70, 300, 30);
        add(nameField);

        JLabel pinLabel = new JLabel("PIN:");
        pinLabel.setBounds(30, 110, 100, 30);
        add(pinLabel);

        pinField = new JPasswordField();
        pinField.setBounds(140, 110, 300, 30);
        add(pinField);

        loginBtn = new JButton("Login");
        loginBtn.setBounds(140, 150, 100, 30);
        loginBtn.addActionListener(this);
        add(loginBtn);

        registerBtn = new JButton("Register");
        registerBtn.setBounds(260, 150, 100, 30);
        registerBtn.addActionListener(this);
        add(registerBtn);

        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setBounds(30, 200, 100, 30);
        add(amountLabel);

        amountField = new JTextField();
        amountField.setBounds(140, 200, 300, 30);
        amountField.setEnabled(false);
        add(amountField);

        checkBalanceBtn = new JButton("Check Balance");
        checkBalanceBtn.setBounds(30, 250, 200, 30);
        checkBalanceBtn.setEnabled(false);
        checkBalanceBtn.addActionListener(this);
        add(checkBalanceBtn);

        depositBtn = new JButton("Deposit");
        depositBtn.setBounds(250, 250, 190, 30);
        depositBtn.setEnabled(false);
        depositBtn.addActionListener(this);
        add(depositBtn);

        withdrawBtn = new JButton("Withdraw");
        withdrawBtn.setBounds(30, 300, 410, 30);
        withdrawBtn.setEnabled(false);
        withdrawBtn.addActionListener(this);
        add(withdrawBtn);

        outputArea = new JTextArea();
        outputArea.setBounds(30, 350, 410, 170);
        outputArea.setEditable(false);
        add(outputArea);

        logoutBtn = new JButton("Logout");
        logoutBtn.setBounds(380, 30, 80, 30);
        logoutBtn.addActionListener(this);
        logoutBtn.setEnabled(false);
        add(logoutBtn);

        connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
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
                userName = rs.getString("name");
                return true;
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

    private boolean registerUser(String acc, String pin, String name) {
        try {
            PreparedStatement check = conn.prepareStatement("SELECT * FROM users WHERE account_number=?");
            check.setString(1, acc);
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                outputArea.setText("Account already exists!");
                return false;
            }

            PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (account_number, pin, name, balance) VALUES (?, ?, ?, 0)");
            stmt.setString(1, acc);
            stmt.setString(2, pin);
            stmt.setString(3, name);
            stmt.executeUpdate();
            outputArea.setText("Account registered successfully!");
            return true;
        } catch (SQLException e) {
            outputArea.setText("Registration failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String acc = accField.getText();
        String pin = new String(pinField.getPassword());
        String name = nameField.getText();

        if (e.getSource() == loginBtn) {
            if (authenticate(acc, pin)) {
                outputArea.setText("Welcome, " + userName + "!\nLogin successful.");
                enableButtons();
                logoutBtn.setEnabled(true); // Enable logout button after login
            } else {
                outputArea.setText("Invalid credentials.");
            }
        } else if (e.getSource() == registerBtn) {
            if (acc.isEmpty() || pin.isEmpty() || name.isEmpty()) {
                outputArea.setText("Please fill all fields to register.");
            } else {
                registerUser(acc, pin, name);
            }
        } else if (e.getSource() == checkBalanceBtn) {
            outputArea.setText("Hello " + userName + ",\nYour balance is: Rs. " + balance);
        } else if (e.getSource() == depositBtn) {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) throw new Exception("Enter a positive amount.");
                balance += amount;
                updateBalanceInDB();
                outputArea.setText("Rs. " + amount + " deposited.\nCurrent Balance: Rs. " + balance);
            } catch (Exception ex) {
                outputArea.setText("Invalid amount: " + ex.getMessage());
            }
        } else if (e.getSource() == withdrawBtn) {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) throw new Exception("Invalid withdrawal amount.");
                if (amount > balance) throw new Exception("Insufficient balance.");
                balance -= amount;
                updateBalanceInDB();
                outputArea.setText("Rs. " + amount + " withdrawn.\nCurrent Balance: Rs. " + balance);
            } catch (Exception ex) {
                outputArea.setText("Transaction failed: " + ex.getMessage());
            }
        } else if (e.getSource() == logoutBtn) {
            // Log out the user and reset everything
            accField.setText("");
            nameField.setText("");
            pinField.setText("");
            amountField.setText("");
            outputArea.setText("");
            balance = 0;
            accNo = "";
            userName = "";
            disableButtons();
            logoutBtn.setEnabled(false); // Disable logout button after logout
        }
    }

    private void enableButtons() {
        amountField.setEnabled(true);
        checkBalanceBtn.setEnabled(true);
        depositBtn.setEnabled(true);
        withdrawBtn.setEnabled(true);
    }

    private void disableButtons() {
        amountField.setEnabled(false);
        checkBalanceBtn.setEnabled(false);
        depositBtn.setEnabled(false);
        withdrawBtn.setEnabled(false);
    }

    public static void main(String[] args) {
        new ATMGUI().setVisible(true);
    }
}