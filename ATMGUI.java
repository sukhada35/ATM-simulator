import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class ATMGUI extends JFrame implements ActionListener {
    private JTextField pinField, amountField;
    private JButton loginBtn, checkBalanceBtn, depositBtn, withdrawBtn;
    private JTextArea outputArea;

    private double balance = 5000.00;
    private final String correctPin = "1234";

    public ATMGUI() {
        setTitle("Virtual ATM Simulator");
        setSize(400, 450);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel pinLabel = new JLabel("Enter PIN:");
        pinLabel.setBounds(30, 30, 100, 30);
        add(pinLabel);

        pinField = new JTextField();
        pinField.setBounds(140, 30, 200, 30);
        add(pinField);

        loginBtn = new JButton("Login");
        loginBtn.setBounds(140, 70, 100, 30);
        loginBtn.addActionListener(this);
        add(loginBtn);

        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setBounds(30, 120, 100, 30);
        add(amountLabel);

        amountField = new JTextField();
        amountField.setBounds(140, 120, 200, 30);
        amountField.setEnabled(false);
        add(amountField);

        checkBalanceBtn = new JButton("Check Balance");
        checkBalanceBtn.setBounds(30, 170, 150, 30);
        checkBalanceBtn.setEnabled(false);
        checkBalanceBtn.addActionListener(this);
        add(checkBalanceBtn);

        depositBtn = new JButton("Deposit");
        depositBtn.setBounds(200, 170, 140, 30);
        depositBtn.setEnabled(false);
        depositBtn.addActionListener(this);
        add(depositBtn);

        withdrawBtn = new JButton("Withdraw");
        withdrawBtn.setBounds(30, 220, 310, 30);
        withdrawBtn.setEnabled(false);
        withdrawBtn.addActionListener(this);
        add(withdrawBtn);

        outputArea = new JTextArea();
        outputArea.setBounds(30, 270, 310, 100);
        outputArea.setEditable(false);
        add(outputArea);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginBtn) {
            String enteredPin = pinField.getText();
            if (enteredPin.equals(correctPin)) {
                outputArea.setText("Login successful!");
                enableButtons();
            } else {
                outputArea.setText("Invalid PIN. Try again.");
            }
        } else if (e.getSource() == checkBalanceBtn) {
            outputArea.setText("Your balance is: ₹" + balance);
        } else if (e.getSource() == depositBtn) {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount < 0) throw new Exception("Cannot deposit negative amount.");
                balance += amount;
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
