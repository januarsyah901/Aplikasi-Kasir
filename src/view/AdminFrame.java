package view;

import controller.AuthController;
import controller.ProductController;
import model.Product;
import util.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminFrame extends JFrame {

    private JTable productTable;
    private DefaultTableModel tableModel;

    public AdminFrame() {
        setTitle("POS System - Admin Panel");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        loadProducts();
    }

    private void initComponents() {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel welcomeLabel = new JLabel("Welcome, " + AuthController.getCurrentUser().getUsername() + " (Admin)");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());

        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnNames = {"ID", "Product Name", "Price", "Stock", "Created At"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(productTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addProductButton = new JButton("Add New Product");
        addProductButton.addActionListener(e -> showAddProductDialog());

        JButton editProductButton = new JButton("Edit Selected Product");
        editProductButton.addActionListener(e -> showEditProductDialog());

        JButton deleteProductButton = new JButton("Delete Selected");
        deleteProductButton.addActionListener(e -> deleteSelectedProduct());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadProducts());

        buttonsPanel.add(addProductButton);
        buttonsPanel.add(editProductButton);
        buttonsPanel.add(deleteProductButton);
        buttonsPanel.add(refreshButton);

        tablePanel.add(buttonsPanel, BorderLayout.SOUTH);

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void loadProducts() {
        // Clear table
        tableModel.setRowCount(0);

        // Get all products
        List<Product> products = ProductController.getAllProducts();

        // Add rows to table
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (Product product : products) {
            Object[] row = {
                    product.getId(),
                    product.getName(),
                    "Rp " + product.getPrice().toString(),
                    product.getStock(),
                    product.getCreatedAt().format(formatter)
            };
            tableModel.addRow(row);
        }
    }

    private void showAddProductDialog() {
        JTextField nameField = new JTextField(20);
        JTextField priceField = new JTextField(20);
        JTextField stockField = new JTextField(20);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Product Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Price (Rp):"));
        panel.add(priceField);
        panel.add(new JLabel("Stock:"));
        panel.add(stockField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add New Product",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String priceStr = priceField.getText().trim();
            String stockStr = stockField.getText().trim();

            if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                BigDecimal price = new BigDecimal(priceStr);
                int stock = Integer.parseInt(stockStr);

                if (price.compareTo(BigDecimal.ZERO) < 0 || stock < 0) {
                    JOptionPane.showMessageDialog(this, "Price and stock cannot be negative",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = ProductController.addProduct(name, price, stock);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Product added successfully",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadProducts();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add product",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid price or stock format",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditProductDialog() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to edit",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        Product product = ProductController.getProductById(productId);

        if (product == null) {
            JOptionPane.showMessageDialog(this, "Could not load product information",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField nameField = new JTextField(product.getName(), 20);
        JTextField priceField = new JTextField(product.getPrice().toString(), 20);
        JTextField stockField = new JTextField(String.valueOf(product.getStock()), 20);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Product Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Price (Rp):"));
        panel.add(priceField);
        panel.add(new JLabel("Stock:"));
        panel.add(stockField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Edit Product",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String priceStr = priceField.getText().trim();
            String stockStr = stockField.getText().trim();

            if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                BigDecimal price = new BigDecimal(priceStr);
                int stock = Integer.parseInt(stockStr);

                if (price.compareTo(BigDecimal.ZERO) < 0 || stock < 0) {
                    JOptionPane.showMessageDialog(this, "Price and stock cannot be negative",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = ProductController.updateProduct(productId, name, price, stock);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Product updated successfully",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadProducts();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update product",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid price or stock format",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        String productName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the product: " + productName + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = ProductController.deleteProduct(productId);

            if (success) {
                JOptionPane.showMessageDialog(this, "Product deleted successfully",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete product. It may be referenced in transactions.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void logout() {
        AuthController.logout();
        new LoginFrame().setVisible(true);
        this.dispose();
    }
}