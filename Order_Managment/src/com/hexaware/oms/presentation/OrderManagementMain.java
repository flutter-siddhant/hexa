
package com.hexaware.oms.presentation;

import com.hexaware.oms.enitity.Product;
import com.hexaware.oms.enitity.User;
import com.hexaware.oms.exception.OrderNotFoundException;
import com.hexaware.oms.exception.UserNotFoundException;
import com.hexaware.oms.service.OrderService;
import com.hexaware.oms.service.OrderServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OrderManagementMain {

    public static void main(String[] args) throws UserNotFoundException, OrderNotFoundException {
        OrderService orderService = new OrderServiceImpl();
        Scanner sc = new Scanner(System.in);
        
        while (true) {
            System.out.println("Menu:");
            System.out.println("1. Create User");
            System.out.println("2. Create Product");
            System.out.println("3. Cancel Order");
            System.out.println("4. Get All Products");
            System.out.println("5. Get Orders by User");
            System.out.println("6. Create Order");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            
            switch (choice) {
                case 1:
                    // Create User
                    System.out.print("Enter username: ");
                    String username = sc.next();
                    System.out.print("Enter password: ");
                    String password = sc.next();
                    System.out.print("Enter role (Admin/User): ");
                    String role = sc.next();
                    User user = new User(0, username, password, role);
                    orderService.createUser(user);
                    break;

                case 2:
                    // Create Product
                    // Assuming admin user is already created for now
                    User admin = new User(1, "admin", "adminpass", "Admin");
                    System.out.print("Enter product name: ");
                    String productName = sc.next();
                    System.out.print("Enter description: ");
                    String description = sc.next();
                    System.out.print("Enter price: ");
                    double price = sc.nextDouble();
                    System.out.print("Enter quantity in stock: ");
                    int quantityInStock = sc.nextInt();
                    System.out.print("Enter product type (Electronics/Clothing): ");
                    String type = sc.next();
                    Product product = new Product(0, productName, description, price, quantityInStock, type);
                    orderService.createProduct(admin, product);
                    break;

                case 3:
                    // Cancel Order
                    System.out.print("Enter user ID: ");
                    int userId = sc.nextInt();
                    System.out.print("Enter order ID: ");
                    int orderId = sc.nextInt();
                    orderService.cancelOrder(userId, orderId);
                    break;

                case 4:
                    // Get All Products
                    List<Product> allProducts = orderService.getAllProducts();
                    allProducts.forEach(p -> System.out.println(p.getProductName()));
                    break;

                case 5:
                    // Get Orders by User
                    User existingUser = new User(3, "user1", "password", "User");
                    List<Product> userOrders = orderService.getOrderByUser(existingUser);
                    userOrders.forEach(p -> System.out.println(p.getProductName()));
                    break;
                    
                case 6:
                    // Create Order
                    System.out.print("Enter user ID for order: ");
                    int orderUserId = sc.nextInt();
                    User orderUser = new User(orderUserId, "user" + orderUserId, "password", "User"); // Create user object

                    // Fetch all available products
                    List<Product> availableProductsForOrder = orderService.getAllProducts();
                    if (availableProductsForOrder.isEmpty()) {
                        System.out.println("No products available to order.");
                        break;
                    }

                    // Display products
                    System.out.println("Available Products:");
                    availableProductsForOrder.forEach(p -> 
                        System.out.println("Product ID: " + p.getProductId() + ", Name: " + p.getProductName() +
                                           ", Price: " + p.getPrice() + ", Stock: " + p.getQuantityInStock()));

                    List<Product> selectedProducts = new ArrayList<>();
                    System.out.println("Select products by entering Product ID (type 'done' when finished):");

                    while (true) {
                        String input = sc.next();
                        if (input.equalsIgnoreCase("done")) {
                            break;
                        }
                        int productId = Integer.parseInt(input);

                        // Check if product exists
                        boolean productFound = false;
                        for (Product p : availableProductsForOrder) {
                            if (p.getProductId() == productId) {
                                selectedProducts.add(p);
                                productFound = true;
                                break;
                            }
                        }
                        if (!productFound) {
                            System.out.println("Invalid Product ID, please try again.");
                        }
                    }

                    // Create order
                    if (!selectedProducts.isEmpty()) {
                        boolean isOrderCreated = orderService.createOrder(orderUser, selectedProducts);
                        if (isOrderCreated) {
                            System.out.println("Order created successfully.");
                        } else {
                            System.out.println("Failed to create order.");
                        }
                    } else {
                        System.out.println("No products selected. Order creation aborted.");
                    }
                    break;


                case 7:
                    // Exit
                	System.out.println("Exiting...");
                    System.exit(0);
            }
        }
    }
}


