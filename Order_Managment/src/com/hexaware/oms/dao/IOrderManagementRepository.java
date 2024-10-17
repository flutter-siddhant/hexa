package com.hexaware.oms.dao;

import com.hexaware.oms.enitity.*;
import com.hexaware.oms.exception.OrderNotFoundException;
import com.hexaware.oms.exception.UserNotFoundException;

import java.util.*;

public interface IOrderManagementRepository {
	boolean createOrder(User user, List<Product> products);
    boolean cancelOrder(int userId, int orderId) throws UserNotFoundException, OrderNotFoundException;
    boolean createProduct(User user, Product product);
    boolean createUser(User user);
    List<Product> getAllProducts();
    List<Product> getOrderByUser(User user);
}
