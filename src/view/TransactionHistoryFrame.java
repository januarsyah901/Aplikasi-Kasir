package view;

import controller.TransactionController;
import model.Transaction;
import model.TransactionDetail;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TransactionHistoryFrame extends JFrame {

    private JTable transactionTable;
    private DefaultTableModel transactionTableModel;
    private JTable detailTable;
    private DefaultTableModel detailTableModel;

    public TransactionHistoryFrame() {
        setTitle("Transaction History");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        loadTransactions();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Transaction History", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Split pane for tables
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(250);

        // Transactions table
        JPanel transactionsPanel = new JPanel(new BorderLayout());
        transactionsPanel.setBorder(BorderFactory.createTitledBorder("Transactions"));

        String[] transactionColumns = {"ID", "Cashier", "Date", "Total Amount"};
        transactionTableModel = new DefaultTableModel(transactionColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transactionTable = new JTable(transactionTableModel);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showTransactionDetails();
            }
        });

        JScrollPane transactionScrollPane = new JScrollPane(transactionTable);
        transactionsPanel.add(transactionScrollPane, BorderLayout.CENTER);

        // Transaction details table
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Transaction Details"));

        String[] detailColumns = {"Product", "Price", "Quantity", "Subtotal"};
        detailTableModel = new DefaultTableModel(detailColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        detailTable = new JTable(detailTableModel);

        JScrollPane detailScrollPane = new JScrollPane(detailTable);
        detailsPanel.add(detailScrollPane, BorderLayout.CENTER);

        splitPane.add(transactionsPanel);
        splitPane.add(detailsPanel);

        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadTransactions());

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadTransactions() {
        transactionTableModel.setRowCount(0);
        detailTableModel.setRowCount(0);

        List<Transaction> transactions = TransactionController.getTransactionsHistory();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Transaction transaction : transactions) {
            Object[] row = {
                    transaction.getId(),
                    "User ID: " + transaction.getCashierId(),
                    transaction.getTransactionDate().format(formatter),
                    "Rp " + transaction.getTotalAmount().toString()
            };
            transactionTableModel.addRow(row);
        }
    }

    private void showTransactionDetails() {
        detailTableModel.setRowCount(0);

        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow != -1) {
            int transactionId = (int) transactionTableModel.getValueAt(selectedRow, 0);

            List<Transaction> transactions = TransactionController.getTransactionsHistory();
            for (Transaction transaction : transactions) {
                if (transaction.getId() == transactionId) {
                    for (TransactionDetail detail : transaction.getDetails()) {
                        Object[] row = {
                                detail.getProductName(),
                                "Rp " + detail.getPrice().toString(),
                                detail.getQuantity(),
                                "Rp " + detail.getSubtotal().toString()
                        };
                        detailTableModel.addRow(row);
                    }
                    break;
                }
            }
        }
    }
}