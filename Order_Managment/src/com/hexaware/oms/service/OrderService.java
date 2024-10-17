package com.hexaware.oms.service;

import java.util.List;

import com.hexaware.oms.enitity.*;
import com.hexaware.oms.exception.OrderNotFoundException;
import com.hexaware.oms.exception.UserNotFoundException;

public interface OrderService {
	boolean createUser(User user);
    boolean createProduct(User user, Product product);
    boolean createOrder(User user, List<Product> products);
    boolean cancelOrder(int userId, int orderId) throws UserNotFoundException, OrderNotFoundException;
    List<Product> getAllProducts();
    List<Product> getOrderByUser(User user);
}
