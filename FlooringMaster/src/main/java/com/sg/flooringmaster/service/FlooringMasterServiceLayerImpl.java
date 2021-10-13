/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmaster.service;


import com.sg.flooringmaster.dao.FlooringMasterOrderDao;
import com.sg.flooringmaster.dao.FlooringMasterProductDao;
import com.sg.flooringmaster.dao.FlooringMasterStateDao;
import com.sg.flooringmaster.dto.Order;
import com.sg.flooringmaster.dto.Product;
import com.sg.flooringmaster.dto.State;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
//import com.sg.flooringmaster.service.FlooringMasterPersistenceException;

/**
 *
 * @author pro
 */
public class FlooringMasterServiceLayerImpl implements FlooringMasterServiceLayer {
    FlooringMasterOrderDao orderDao;
    FlooringMasterProductDao productsDao;
    FlooringMasterStateDao stateDao;

    public FlooringMasterServiceLayerImpl(
            FlooringMasterOrderDao orderDao, FlooringMasterProductDao productDao, FlooringMasterStateDao stateDao) {
        this.orderDao = orderDao;
        this.productsDao = productDao;
        this.stateDao = stateDao;
    }
    
    @Override
    public List<Product> getAllProducts() throws FlooringMasterPersistenceException {
        return productsDao.getProducts();
    }

    @Override
    public List<Order> getOrders(LocalDate chosenDate) throws InvalidOrderNumberException,
            FlooringMasterPersistenceException, FlooringMasterPersistenceException, com.sg.flooringmaster.service.FlooringMasterPersistenceException {
        List<Order> ordersByDate = orderDao.getOrders(chosenDate);
        if (!ordersByDate.isEmpty()) {
            return ordersByDate;
        } else {
            throw new InvalidOrderNumberException("ERROR: No orders "
                    + "exist on that date.");
        }
    }

    @Override
    public Order getOrder(LocalDate chosenDate, int orderNumber) throws
            FlooringMasterPersistenceException, InvalidOrderNumberException, FlooringMasterPersistenceException, com.sg.flooringmaster.service.FlooringMasterPersistenceException {
        List<Order> orders = getOrders(chosenDate);
        Order chosenOrder = orders.stream()
                .filter(o -> o.getOrderNumber() == orderNumber)
                .findFirst().orElse(null);
        if (chosenOrder != null) {
            return chosenOrder;
        } else {
            throw new InvalidOrderNumberException("ERROR: No orders with that number "
                    + "exist on that date.");
        }
    }

    @Override
    public Order calculateOrder(Order o) throws FlooringMasterPersistenceException,
            FlooringMasterDataValidationException, StateValidationException, ProductValidationException {

        validateOrder(o);
        calculateTax(o);
        calculateMaterial(o);
        calculateTotal(o);

        return o;

    }
    
     private void calculateMaterial(Order o) throws FlooringMasterPersistenceException,
            ProductValidationException {
        //Set product information in order.
        Product chosenProduct = productsDao.getProduct(o.getProductType());
        if (chosenProduct == null) {
            throw new ProductValidationException("ERROR: We do not sell that "
                    + "product.");
        }
        o.setProductType(chosenProduct.getProductType());
        o.setMaterialCostPerSquareFoot(chosenProduct.getMaterialCostPerSquareFoot());
        o.setLaborCostPerSquareFoot(chosenProduct.getLaborCostPerSquareFoot());
    }

    private void calculateTax(Order o) throws FlooringMasterPersistenceException,
            StateValidationException {
        //Set state information in order.
        State chosenState = stateDao.getState(o.getStateAbbr());
        if (chosenState == null) {
            throw new StateValidationException("SORRY: we do not serve this state");
                    
        }
        o.setStateAbbr(chosenState.getStateAbbr());
        o.setTaxRate(chosenState.getTaxRate());
    }

    private void calculateTotal(Order o) {
        o.setMaterialCost(o.getMaterialCostPerSquareFoot().multiply(o.getArea())
                .setScale(2, RoundingMode.HALF_UP));
        o.setLaborCost(o.getLaborCostPerSquareFoot().multiply(o.getArea())
                .setScale(2, RoundingMode.HALF_UP));
        o.setTax(o.getTaxRate().divide(new BigDecimal("100.00"))
                .multiply((o.getMaterialCost().add(o.getLaborCost())))
                .setScale(2, RoundingMode.HALF_UP));
        o.setTotal(o.getMaterialCost().add(o.getLaborCost()).add(o.getTax()));
    }

    private void validateOrder(Order o) throws FlooringMasterDataValidationException {
        String message = "";
        if (o.getCustomerName().trim().isEmpty() || o.getCustomerName() == null) {
            message += "Customer name is required.\n";
        }
        if (o.getStateAbbr().trim().isEmpty() || o.getStateAbbr() == null) {
            message += "State is required.\n";
        }
        if (o.getProductType().trim().isEmpty() || o.getProductType() == null) {
            message += "Product type is required.\n";
        }
        if (o.getArea().compareTo(BigDecimal.ZERO) == 0 || o.getArea() == null) {
            message += "Area square footage is required.";
        }
        if (!message.isEmpty()) {
            throw new FlooringMasterDataValidationException(message);
        }
    }

    @Override
    public Order addOrder(Order o) throws FlooringMasterPersistenceException {
        orderDao.addOrder(o);
        //auditDao.writeAuditEntry("Order #"
               // + o.getOrderNumber() + " for date "
               // + o.getDate() + " ADDED.");
        return o;
    }

    @Override
    public Order compareOrders(Order savedOrder, Order editedOrder)
            throws FlooringMasterPersistenceException, StateValidationException,
            ProductValidationException {

        //This will only update the already saved order's fields
        if (editedOrder.getCustomerName() == null
                || editedOrder.getCustomerName().trim().equals("")) {
            //No change
        } else {
            savedOrder.setCustomerName(editedOrder.getCustomerName());
        }

        if (editedOrder.getStateAbbr() == null
                || editedOrder.getStateAbbr().trim().equals("")) {
        } else {
            savedOrder.setStateAbbr(editedOrder.getStateAbbr());
            calculateTax(savedOrder);
        }

        if (editedOrder.getProductType() == null
                || editedOrder.getProductType().equals("")) {
        } else {
            savedOrder.setProductType(editedOrder.getProductType());
            calculateMaterial(savedOrder);
        }

        if (editedOrder.getArea() == null
                || (editedOrder.getArea().compareTo(BigDecimal.ZERO)) == 0) {
        } else {
            savedOrder.setArea(editedOrder.getArea());
        }

        calculateTotal(savedOrder);

        return savedOrder;
    }

    @Override
    public Order editOrder(Order updatedOrder) throws FlooringMasterPersistenceException,
            InvalidOrderNumberException {
        updatedOrder = orderDao.editOrder(updatedOrder);
        /*if (updatedOrder != null) {
            auditDao.writeAuditEntry("Order #"
                    + updatedOrder.getOrderNumber() + " for date "
                    + updatedOrder.getDate() + " EDITED.");
            return updatedOrder;
        } else {
            throw new InvalidOrderNumberException("ERROR: No orders with that number "
                    + "exist on that date.");
        }*/
        return updatedOrder;
    }

    @Override
    public Order removeOrder(Order removedOrder) throws FlooringMasterPersistenceException,
            InvalidOrderNumberException {
        removedOrder = orderDao.removeOrder(removedOrder);
        /*if (removedOrder != null) {
            auditDao.writeAuditEntry("Order #"
                    + removedOrder.getOrderNumber() + " for date "
                    + removedOrder.getDate() + " REMOVED.");
            return removedOrder;
        } else {
            throw new InvalidOrderNumberException("ERROR: No orders with that number "
                    + "exist on that date.");
        }*/
        return removedOrder;
    }

    

    

}
