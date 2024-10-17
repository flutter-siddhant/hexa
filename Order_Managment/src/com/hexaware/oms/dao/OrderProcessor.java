package com.hexaware.oms.dao;

import java.util.*;

import com.hexaware.oms.enitity.*;
import com.hexaware.oms.exception.*;
import com.mysql.cj.x.protobuf.MysqlxCrud.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderProcessor implements IOrderManagementRepository {

    private Connection conn;

    public OrderProcessor() {
        this.conn = DBUtil.getDBConnection();
    }

    // Method to create a user and store it in the database
    @Override
    public boolean createUser(User user) {
        String query = "INSERT INTO User (username, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to create a product in the database
    @Override
    public boolean createProduct(User user, Product product) {
        
        if (!"Admin".equalsIgnoreCase(user.getRole())) {
            System.out.println("Only admin users can create products.");
            return false;
        }

        String query = "INSERT INTO Product (productName, description, price, quantityInStock, type) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, product.getProductName());
            stmt.setString(2, product.getDescription());
            stmt.setDouble(3, product.getPrice());
            stmt.setInt(4, product.getQuantityInStock());
            stmt.setString(5, product.getType());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

//    // Method to create an order
//    @Override
//    public boolean createOrder(User user, List<Product> products) {
//        // First, ensure the user exists
//        if (!isUserExists(user.getUserId())) {
//            createUser(user); // If user doesn't exist, create them
//        }
//
//        String orderQuery = "INSERT INTO Order (userId) VALUES (?)";
//        String orderProductQuery = "INSERT INTO OrderDetails (orderId, productId) VALUES (?, ?)";
//
//        try (PreparedStatement orderStmt = conn.prepareStatement(orderQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
//            // Create the order
//            orderStmt.setInt(1, user.getUserId());
//            orderStmt.executeUpdate();
//
//            ResultSet generatedKeys = orderStmt.getGeneratedKeys();
//            if (generatedKeys.next()) {
//                int orderId = generatedKeys.getInt(1);
//
//                // Link products to the order
//                try (PreparedStatement orderProductStmt = conn.prepareStatement(orderProductQuery)) {
//                    for (Product product : products) {
//                        orderProductStmt.setInt(1, orderId);
//                        orderProductStmt.setInt(2, product.getProductId());
//                        orderProductStmt.addBatch();
//                    }
//                    orderProductStmt.executeBatch();
//                }
//            }
//
//            return true;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//    @Override
//    public boolean createOrder(User user, List<Product> products) {
//        // First, ensure the user exists
//        if (!isUserExists(user.getUserId())) {
//            createUser(user); // If user doesn't exist, create them
//        }
//
//        String orderQuery = "INSERT INTO Order (userId) VALUES (?)";
//        String orderProductQuery = "INSERT INTO OrderDetails (orderId, productId) VALUES (?, ?)";
//
//        try (PreparedStatement orderStmt = conn.prepareStatement(orderQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
//            // Create the order
//            orderStmt.setInt(1, user.getUserId());
//            orderStmt.executeUpdate();
//
//            ResultSet generatedKeys = orderStmt.getGeneratedKeys();
//            if (generatedKeys.next()) {
//                int orderId = generatedKeys.getInt(1);
//
//                // Link products to the order
//                try (PreparedStatement orderProductStmt = conn.prepareStatement(orderProductQuery)) {
//                    for (Product product : products) {
//                        orderProductStmt.setInt(1, orderId);
//                        orderProductStmt.setInt(2, product.getProductId());
//                        orderProductStmt.addBatch();
//                    }
//                    orderProductStmt.executeBatch();
//                }
//            }
//
//            return true;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
    
    @Override
    public boolean createOrder(User user, List<Product> products) {
        // First, ensure the user exists
        if (!isUserExists(user.getUserId())) {
            createUser(user); // If user doesn't exist, create them
        }

        // Enclose `Order` in backticks to avoid SQL syntax issues
        String orderQuery = "INSERT INTO `Order` (userId) VALUES (?)";
        String orderProductQuery = "INSERT INTO OrderDetails (orderId, productId) VALUES (?, ?)";

        try (PreparedStatement orderStmt = conn.prepareStatement(orderQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
            // Create the order
            orderStmt.setInt(1, user.getUserId());
            orderStmt.executeUpdate();

            ResultSet generatedKeys = orderStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int orderId = generatedKeys.getInt(1);

                // Link products to the order
                try (PreparedStatement orderProductStmt = conn.prepareStatement(orderProductQuery)) {
                    for (Product product : products) {
                        orderProductStmt.setInt(1, orderId);
                        orderProductStmt.setInt(2, product.getProductId());
                        orderProductStmt.addBatch();
                    }
                    orderProductStmt.executeBatch();
                }
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean cancelOrder(int userId, int orderId) {
        String deleteOrderDetailsQuery = "DELETE FROM OrderDetails WHERE orderId = ?";
        String deleteOrderQuery = "DELETE FROM `Order` WHERE userId = ? AND orderId = ?";
        
        try {
            conn.setAutoCommit(false);  // Begin transaction
            
            try (PreparedStatement stmt1 = conn.prepareStatement(deleteOrderDetailsQuery)) {
                stmt1.setInt(1, orderId);
                stmt1.executeUpdate();
            }

            try (PreparedStatement stmt2 = conn.prepareStatement(deleteOrderQuery)) {
                stmt2.setInt(1, userId);
                stmt2.setInt(2, orderId);
                int rowsAffected = stmt2.executeUpdate();

                if (rowsAffected > 0) {
                    conn.commit();  // Commit transaction
                    return true;
                } else {
                    conn.rollback();  // Rollback if no rows are affected
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();  // Rollback in case of an exception
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } 
//        finally {
//            try {
//                conn.setAutoCommit(true);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        String query = "SELECT * FROM Product";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("productId"));
                product.setProductName(rs.getString("productName"));
                product.setDescription(rs.getString("description"));
                product.setPrice(rs.getDouble("price"));
                product.setQuantityInStock(rs.getInt("quantityInStock"));
                product.setType(rs.getString("type"));
                productList.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }

    @Override
    public List<Product> getOrderByUser(User user) {
        List<Product> productList = new ArrayList<>();
        String query = "SELECT \n"
        		+ "    p.productId,\n"
        		+ "    p.productName,\n"
        		+ "    p.description,\n"
        		+ "    p.price,\n"
        		+ "    p.quantityInStock,\n"
        		+ "    p.type,\n"
        		+ "    od.quantity\n"
        		+ "FROM \n"
        		+ "    `Order` o\n"
        		+ "JOIN \n"
        		+ "    OrderDetails od ON o.orderId = od.orderId\n"
        		+ "JOIN \n"
        		+ "    Product p ON od.productId = p.productId\n"
        		+ "JOIN \n"
        		+ "    `User` u ON o.userId = u.userId\n"
        		+ "WHERE \n"
        		+ "    u.userId = ?;\n"
        		+ "";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, user.getUserId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("productId"));
                product.setProductName(rs.getString("productName"));
                product.setDescription(rs.getString("description"));
                product.setPrice(rs.getDouble("price"));
                product.setQuantityInStock(rs.getInt("quantityInStock"));
                product.setType(rs.getString("type"));
                productList.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }
    
    private boolean isUserExists(int userId) {
        String query = "SELECT * FROM User WHERE userId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

