/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmaster.service;

import com.sg.flooringmaster.service.FlooringMasterPersistenceException;
import com.sg.flooringmaster.dto.Order;
import com.sg.flooringmaster.dto.Product;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author pro
 */
public interface FlooringMasterServiceLayer {
    List<Order> getOrders(LocalDate dateChoice) throws InvalidOrderNumberException,
            FlooringMasterPersistenceException;
    List<Product> getAllProducts () throws FlooringMasterPersistenceException;

    Order calculateOrder(Order o) throws FlooringMasterPersistenceException,
            FlooringMasterDataValidationException, StateValidationException, ProductValidationException;

    Order getOrder(LocalDate dateChoice, int orderNumber) throws
            FlooringMasterPersistenceException, InvalidOrderNumberException;

    Order addOrder(Order o) throws FlooringMasterPersistenceException;

    Order compareOrders(Order savedOrder, Order editedOrder)
            throws FlooringMasterPersistenceException, StateValidationException,
            ProductValidationException;

    Order editOrder(Order updatedOrder) throws FlooringMasterPersistenceException,
            InvalidOrderNumberException;

    Order removeOrder(Order removedOrder) throws FlooringMasterPersistenceException,
            InvalidOrderNumberException;
}
