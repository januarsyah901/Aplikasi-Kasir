package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Transaction {
    private int id;
    private int cashierId;
    private BigDecimal totalAmount;
    private LocalDateTime transactionDate;
    private List<TransactionDetail> details;

    public Transaction() {
        details = new ArrayList<>();
        totalAmount = BigDecimal.ZERO;
    }

    public Transaction(int id, int cashierId, BigDecimal totalAmount, LocalDateTime transactionDate) {
        this.id = id;
        this.cashierId = cashierId;
        this.totalAmount = totalAmount;
        this.transactionDate = transactionDate;
        this.details = new ArrayList<>();
    }

    public void addDetail(TransactionDetail detail) {
        details.add(detail);
        totalAmount = totalAmount.add(detail.getSubtotal());
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCashierId() {
        return cashierId;
    }

    public void setCashierId(int cashierId) {
        this.cashierId = cashierId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public List<TransactionDetail> getDetails() {
        return details;
    }

    public void setDetails(List<TransactionDetail> details) {
        this.details = details;
    }
}