package controller;

import model.Product;
import model.Transaction;
import model.TransactionDetail;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionController {

    public static int saveTransaction(Transaction transaction) {
        Connection conn = null;
        PreparedStatement stmtTrans = null;
        PreparedStatement stmtDetail = null;
        PreparedStatement stmtStock = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Save transaction header
            String sqlTrans = "INSERT INTO transactions (cashier_id, total_amount) VALUES (?, ?)";
            stmtTrans = conn.prepareStatement(sqlTrans, Statement.RETURN_GENERATED_KEYS);
            stmtTrans.setInt(1, transaction.getCashierId());
            stmtTrans.setBigDecimal(2, transaction.getTotalAmount());
            stmtTrans.executeUpdate();

            ResultSet rs = stmtTrans.getGeneratedKeys();
            int transactionId = -1;
            if (rs.next()) {
                transactionId = rs.getInt(1);
                transaction.setId(transactionId);
            }

            // Save transaction details
            String sqlDetail = "INSERT INTO transaction_details (transaction_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
            stmtDetail = conn.prepareStatement(sqlDetail);

            // Update stock
            String sqlStock = "UPDATE products SET stock = stock - ? WHERE id = ?";
            stmtStock = conn.prepareStatement(sqlStock);

            for (TransactionDetail detail : transaction.getDetails()) {
                // Save detail
                stmtDetail.setInt(1, transactionId);
                stmtDetail.setInt(2, detail.getProductId());
                stmtDetail.setInt(3, detail.getQuantity());
                stmtDetail.setBigDecimal(4, detail.getPrice());
                stmtDetail.executeUpdate();

                // Update product stock
                stmtStock.setInt(1, detail.getQuantity());
                stmtStock.setInt(2, detail.getProductId());
                stmtStock.executeUpdate();
            }

            conn.commit();
            return transactionId;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return -1;
        } finally {
            try {
                if (stmtTrans != null) stmtTrans.close();
                if (stmtDetail != null) stmtDetail.close();
                if (stmtStock != null) stmtStock.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<Transaction> getTransactionsHistory() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, u.username FROM transactions t " +
                "INNER JOIN users u ON t.cashier_id = u.id " +
                "ORDER BY t.transaction_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setId(rs.getInt("id"));
                transaction.setCashierId(rs.getInt("cashier_id"));
                transaction.setTotalAmount(rs.getBigDecimal("total_amount"));
                transaction.setTransactionDate(rs.getTimestamp("transaction_date").toLocalDateTime());

                // Get transaction details
                String sqlDetails = "SELECT td.*, p.name FROM transaction_details td " +
                        "INNER JOIN products p ON td.product_id = p.id " +
                        "WHERE td.transaction_id = ?";

                try (PreparedStatement pstmt = conn.prepareStatement(sqlDetails)) {
                    pstmt.setInt(1, transaction.getId());
                    ResultSet rsDetails = pstmt.executeQuery();

                    while (rsDetails.next()) {
                        TransactionDetail detail = new TransactionDetail();
                        detail.setId(rsDetails.getInt("id"));
                        detail.setTransactionId(rsDetails.getInt("transaction_id"));
                        detail.setProductId(rsDetails.getInt("product_id"));
                        detail.setProductName(rsDetails.getString("name"));
                        detail.setQuantity(rsDetails.getInt("quantity"));
                        detail.setPrice(rsDetails.getBigDecimal("price"));

                        transaction.getDetails().add(detail);
                    }
                }

                transactions.add(transaction);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    public static List<Transaction> getTransactionsForCashier(int cashierId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE cashier_id = ? ORDER BY transaction_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cashierId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setId(rs.getInt("id"));
                transaction.setCashierId(rs.getInt("cashier_id"));
                transaction.setTotalAmount(rs.getBigDecimal("total_amount"));
                transaction.setTransactionDate(rs.getTimestamp("transaction_date").toLocalDateTime());

                // Get transaction details
                String sqlDetails = "SELECT td.*, p.name FROM transaction_details td " +
                        "INNER JOIN products p ON td.product_id = p.id " +
                        "WHERE td.transaction_id = ?";

                try (PreparedStatement pstmt = conn.prepareStatement(sqlDetails)) {
                    pstmt.setInt(1, transaction.getId());
                    ResultSet rsDetails = pstmt.executeQuery();

                    while (rsDetails.next()) {
                        TransactionDetail detail = new TransactionDetail();
                        detail.setId(rsDetails.getInt("id"));
                        detail.setTransactionId(rsDetails.getInt("transaction_id"));
                        detail.setProductId(rsDetails.getInt("product_id"));
                        detail.setProductName(rsDetails.getString("name"));
                        detail.setQuantity(rsDetails.getInt("quantity"));
                        detail.setPrice(rsDetails.getBigDecimal("price"));

                        transaction.getDetails().add(detail);
                    }
                }

                transactions.add(transaction);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }
}