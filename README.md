# 💳 Virtual ATM Simulator 💻

Welcome to the **Virtual ATM Simulator**, a Java-based desktop application with a user-friendly Swing GUI and MySQL integration that simulates real-world ATM functionalities like login, registration, deposit, withdrawal, and balance inquiry!

---

## 🧠 Features

- 🔐 **User Authentication** (Login with Account Number & PIN)
- 📝 **User Registration** (Name, Account Number, PIN)
- 💰 **Check Balance** anytime
- ➕ **Deposit Money** to your account
- ➖ **Withdraw Money** with balance validation
- 🔗 **MySQL Database Integration** for real-time data storage and retrieval
- ✅ **Instant UI Feedback** via `JTextArea`

---

## 🛠️ Tech Stack

| Technology | Description |
|------------|-------------|
| **Java (Swing)** | Frontend GUI |
| **MySQL** | Backend Database |
| **JDBC** | Java Database Connectivity |
| **JDK 17+** | Java Development Kit |

---

## 🔐 Database Setup

1. **Create a database named** `atm_db`
2. **Create the following table**:

```sql
CREATE TABLE users (
  account_number VARCHAR(20) PRIMARY KEY,
  pin VARCHAR(10) NOT NULL,
  name VARCHAR(100) NOT NULL,
  balance DOUBLE DEFAULT 0
);
