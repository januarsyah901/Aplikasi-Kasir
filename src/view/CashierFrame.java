package view;

import controller.AuthController;
import controller.ProductController;
import controller.TransactionController;
import model.Product;
import model.Transaction;
import model.TransactionDetail;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class CashierFrame extends JFrame {

    private JTable productTable;
    private DefaultTableModel productTableModel;

    private JTable cartTable;
    private DefaultTableModel cartTableModel;

    private JLabel totalLabel;
    private JComboBox<Product> productComboBox;
    private JSpinner quantitySpinner;
    private Transaction currentTransaction;

    public CashierFrame() {
        setTitle("POS System - Cashier Panel");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        currentTransaction = new Transaction();
        currentTransaction.setCashierId(AuthController.getCurrentUser().getId());

        initComponents();
        loadProducts();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left side of header (welcome + navigation buttons)
        JPanel headerLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JLabel welcomeLabel = new JLabel("Welcome, " + AuthController.getCurrentUser().getUsername() + " (Cashier)");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLeftPanel.add(welcomeLabel);

        JButton viewTransactionsButton = new JButton("View Transactions");
        viewTransactionsButton.addActionListener(e -> showTransactionHistory());
        headerLeftPanel.add(viewTransactionsButton);

        // Right side of header (logout button)
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());

        headerPanel.add(headerLeftPanel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left panel (available products)
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Available Products"));

        String[] productColumns = {"ID", "Product Name", "Price", "Stock"};
        productTableModel = new DefaultTableModel(productColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(productTableModel);
        JScrollPane productScrollPane = new JScrollPane(productTable);
        leftPanel.add(productScrollPane, BorderLayout.CENTER);

        // Add to cart panel
        JPanel addToCartPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Product dropdown
        gbc.gridx = 0;
        gbc.gridy = 0;
        addToCartPanel.add(new JLabel("Select Product:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        productComboBox = new JComboBox<>();
        addToCartPanel.add(productComboBox, gbc);

        // Quantity spinner
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        addToCartPanel.add(new JLabel("Quantity:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 100, 1);
        quantitySpinner = new JSpinner(spinnerModel);
        addToCartPanel.add(quantitySpinner, gbc);

        // Add to cart button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton addToCartButton = new JButton("Add to Cart");
        addToCartButton.addActionListener(e -> addToCart());
        addToCartPanel.add(addToCartButton, gbc);

        leftPanel.add(addToCartPanel, BorderLayout.SOUTH);

        // Right panel (shopping cart)
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Shopping Cart"));

        String[] cartColumns = {"Product", "Price", "Quantity", "Subtotal"};
        cartTableModel = new DefaultTableModel(cartColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(cartTableModel);
        JScrollPane cartScrollPane = new JScrollPane(cartTable);
        rightPanel.add(cartScrollPane, BorderLayout.CENTER);

        // Cart actions panel
        JPanel cartActionsPanel = new JPanel(new BorderLayout(10, 0));

        // Total panel
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalPanel.add(new JLabel("Total: "));
        totalLabel = new JLabel("Rp 0");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalPanel.add(totalLabel);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton removeItemButton = new JButton("Remove Selected");
        removeItemButton.addActionListener(e -> removeFromCart());

        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.addActionListener(e -> checkout());

        buttonsPanel.add(removeItemButton);
        buttonsPanel.add(checkoutButton);

        cartActionsPanel.add(totalPanel, BorderLayout.WEST);
        cartActionsPanel.add(buttonsPanel, BorderLayout.EAST);

        rightPanel.add(cartActionsPanel, BorderLayout.SOUTH);

        contentPanel.add(leftPanel);
        contentPanel.add(rightPanel);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void loadProducts() {
        // Clear product table
        productTableModel.setRowCount(0);
        productComboBox.removeAllItems();

        // Get all products
        List<Product> products = ProductController.getAllProducts();

        // Add rows to table
        for (Product product : products) {
            if (product.getStock() > 0) {
                Object[] row = {
                        product.getId(),
                        product.getName(),
                        "Rp " + product.getPrice().toString(),
                        product.getStock()
                };
                productTableModel.addRow(row);
                productComboBox.addItem(product);
            }
        }
    }

    private void addToCart() {
        Product selectedProduct = (Product) productComboBox.getSelectedItem();

        if (selectedProduct == null) {
            JOptionPane.showMessageDialog(this, "Please select a product",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int quantity = (int) quantitySpinner.getValue();

        if (quantity <= 0) {
            JOptionPane.showMessageDialog(this, "Quantity must be greater than 0",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (quantity > selectedProduct.getStock()) {
            JOptionPane.showMessageDialog(this, "Not enough stock available",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create transaction detail
        TransactionDetail detail = new TransactionDetail();
        detail.setProductId(selectedProduct.getId());
        detail.setProductName(selectedProduct.getName());
        detail.setQuantity(quantity);
        detail.setPrice(selectedProduct.getPrice());

        // Add to transaction
        currentTransaction.addDetail(detail);

        // Update cart table
        updateCartTable();

        // Reset input
        quantitySpinner.setValue(1);
    }

    private void removeFromCart() {
        int selectedRow = cartTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to remove",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        currentTransaction.getDetails().remove(selectedRow);
        updateCartTable();
    }

    private void updateCartTable() {
        cartTableModel.setRowCount(0);

        BigDecimal total = BigDecimal.ZERO;

        for (TransactionDetail detail : currentTransaction.getDetails()) {
            BigDecimal subtotal = detail.getPrice().multiply(BigDecimal.valueOf(detail.getQuantity()));
            total = total.add(subtotal);

            Object[] row = {
                    detail.getProductName(),
                    "Rp " + detail.getPrice().toString(),
                    detail.getQuantity(),
                    "Rp " + subtotal.toString()
            };

            cartTableModel.addRow(row);
        }

        currentTransaction.setTotalAmount(total);
        totalLabel.setText("Rp " + total.toString());
    }

    private void checkout() {
        if (currentTransaction.getDetails().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Total: Rp " + currentTransaction.getTotalAmount() + "\nProceed with checkout?",
                "Checkout Confirmation", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            int transactionId = TransactionController.saveTransaction(currentTransaction);

            if (transactionId > 0) {
                JOptionPane.showMessageDialog(this,
                        "Transaction completed successfully!\nTransaction ID: " + transactionId,
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                // Reset transaction and reload products
                currentTransaction = new Transaction();
                currentTransaction.setCashierId(AuthController.getCurrentUser().getId());
                cartTableModel.setRowCount(0);
                totalLabel.setText("Rp 0");
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(this, "Transaction failed",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showTransactionHistory() {
        new TransactionHistoryFrame().setVisible(true);
    }

    private void logout() {
        AuthController.logout();
        new LoginFrame().setVisible(true);
        this.dispose();
    }
}