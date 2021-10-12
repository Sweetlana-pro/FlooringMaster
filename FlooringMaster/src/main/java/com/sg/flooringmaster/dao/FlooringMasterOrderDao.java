/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmaster.dao;

import com.sg.flooringmaster.dto.Order;
import java.util.List;
import com.sg.flooringmaster.service.FlooringMasterPersistenceException;
import java.time.LocalDate;

/**
 *
 * @author pro
 */
public interface FlooringMasterOrderDao {
   

    List<Order> getOrders(LocalDate dateChoice) throws FlooringMasterPersistenceException;

    Order addOrder(Order o) throws FlooringMasterPersistenceException;

    Order editOrder(Order editedOrder) throws FlooringMasterPersistenceException;

    Order removeOrder(Order o) throws FlooringMasterPersistenceException;


    
}
