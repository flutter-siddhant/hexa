package com.hexaware.oms.service;

import com.hexaware.oms.exception.*;

import com.hexaware.oms.dao.IOrderManagementRepository;
import com.hexaware.oms.dao.OrderProcessor;
import com.hexaware.oms.enitity.Product;
import com.hexaware.oms.enitity.User;
import com.hexaware.oms.exception.OrderNotFoundException;
import com.hexaware.oms.exception.UserNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class OrderServiceImpl implements OrderService {

    private IOrderManagementRepository orderManagementRepository;

    public OrderServiceImpl() {
        this.orderManagementRepository = new OrderProcessor();  
    }

    @Override
    public boolean createUser(User user) {
        return orderManagementRepository.createUser(user);
    }

    @Override
    public boolean createProduct(User user, Product product) {
        return orderManagementRepository.createProduct(user, product);
    }

    @Override
    public boolean createOrder(User user, List<Product> products) {
        return orderManagementRepository.createOrder(user, products);
    }

    @Override
    public boolean cancelOrder(int userId, int orderId)throws UserNotFoundException, OrderNotFoundException {
        return orderManagementRepository.cancelOrder(userId, orderId);
    }

    @Override
    public List<Product> getAllProducts() {
        return orderManagementRepository.getAllProducts();
    }

    @Override
    public List<Product> getOrderByUser(User user) {
        return orderManagementRepository.getOrderByUser(user);
    }
}

