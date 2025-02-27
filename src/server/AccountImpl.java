package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import common.Account;  // Import the Account interface
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountImpl extends UnicastRemoteObject implements Account {
    private String accountNumber;
    
    public AccountImpl(String accountNumber) throws RemoteException {
        super();
        this.accountNumber = accountNumber;
    }

    private double getBalanceFromDB() throws SQLException {
        String query = "SELECT balance FROM Accounts WHERE account_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        }
        return 0.0;
    }

    private void updateBalanceInDB(double newBalance) throws SQLException {
        String query = "UPDATE Accounts SET balance = ? WHERE account_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, newBalance);
            stmt.setString(2, accountNumber);
            stmt.executeUpdate();
        }
    }

    @Override
    public void deposit(double amount) throws RemoteException {
        try {
            double newBalance = getBalanceFromDB() + amount;
            updateBalanceInDB(newBalance);
            System.out.println("Deposited: " + amount + ", New Balance: " + newBalance);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean withdraw(double amount) throws RemoteException {
        try {
            double currentBalance = getBalanceFromDB();
            if (amount <= currentBalance) {
                double newBalance = currentBalance - amount;
                updateBalanceInDB(newBalance);
                System.out.println("Withdrawn: " + amount + ", New Balance: " + newBalance);
                return true;
            } else {
                System.out.println("Insufficient funds for withdrawal.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public double getBalance() throws RemoteException {
        try {
            return getBalanceFromDB();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    @Override
    public String getAccountNumber() throws RemoteException {
        return accountNumber;
    }
}
