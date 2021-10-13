/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmaster.dao;

import com.sg.flooringmaster.dto.Order;
import com.sg.flooringmaster.dto.Product;
import com.sg.flooringmaster.dto.State;
import com.sg.flooringmaster.service.FlooringMasterPersistenceException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.Double.parseDouble;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author pro
 */
public class FlooringMasterOrderDaoFileImpl implements FlooringMasterOrderDao {
    private int lastOrderNumber;
    private static final String HEADER = "OrderNumber,CustomerName,State,TaxRate,"
            + "ProductType,Area,CostPerSquareFoot,LaborCostPerSquareFoot,"
            + "MaterialCost,LaborCost,Tax,Total";
    private static final String DELIMITER = ",";
    private String dataFolder = "orders/";

    public FlooringMasterOrderDaoFileImpl() {
    }

    public FlooringMasterOrderDaoFileImpl(String dataFolder) {
        this.dataFolder = dataFolder;
    }

    private void readLastOrderNumber() throws FlooringMasterPersistenceException {
        Scanner scanner;

        try {
            //Create Scanner to read file
            scanner = new Scanner(
                    new BufferedReader(
                            new FileReader(dataFolder + "LastOrderNumber.txt")));
        } catch (FileNotFoundException e) {
            //Throwing a general exception to the calling code
            throw new FlooringMasterPersistenceException(
                    "-_- Could not load order number into memory.", e);
        }

        int savedOrderNumber = Integer.parseInt(scanner.nextLine());

        this.lastOrderNumber = savedOrderNumber;

        scanner.close();

    }

    private void writeLastOrderNumber(int lastOrderNumber) throws FlooringMasterPersistenceException {
        PrintWriter out;

        try {
            out = new PrintWriter(new FileWriter(dataFolder + "LastOrderNumber.txt"));
        } catch (IOException e) {
            throw new FlooringMasterPersistenceException(
                    "Could not save order number.", e);
        }

        out.println(lastOrderNumber);

        out.flush();

        out.close();
    }

    @Override
    public List<Order> getOrders(LocalDate chosenDate) throws FlooringMasterPersistenceException {
        return loadOrders(chosenDate);
    }

    @Override
    public Order addOrder(Order o) throws FlooringMasterPersistenceException {
        //Checks input for commas
        o = cleanFields(o);
        //Getting the last used number, adding one, and saving it
        readLastOrderNumber();
        lastOrderNumber++;
        o.setOrderNumber(lastOrderNumber);
        writeLastOrderNumber(lastOrderNumber);

        List<Order> orders = loadOrders(o.getDate());
        orders.add(o);
        writeOrders(orders, o.getDate());

        return o;
    }

    @Override
    public Order editOrder(Order editedOrder)
            throws FlooringMasterPersistenceException {
        //Checks input for commas
        editedOrder = cleanFields(editedOrder);
        int orderNumber = editedOrder.getOrderNumber();

        List<Order> orders = loadOrders(editedOrder.getDate());
        Order chosenOrder = orders.stream()
                .filter(o -> o.getOrderNumber() == orderNumber)
                .findFirst().orElse(null);

        if (chosenOrder != null) {
            int index = orders.indexOf(chosenOrder);
            orders.set(index, editedOrder);
            writeOrders(orders, editedOrder.getDate());
            return editedOrder;
        } else {
            return null;
        }
    }

    @Override
    public Order removeOrder(Order chosenOrder)
            throws FlooringMasterPersistenceException {

        int orderNumber = chosenOrder.getOrderNumber();

        List<Order> orders = loadOrders(chosenOrder.getDate());
        Order removedOrder = orders.stream()
                .filter(o -> o.getOrderNumber() == orderNumber)
                .findFirst().orElse(null);

        if (removedOrder != null) {
            orders.remove(removedOrder);
            writeOrders(orders, chosenOrder.getDate());
            return removedOrder;
        } else {
            return null;
        }

    }

    private Order cleanFields(Order order) {
        //Dao does not know what the other layers are doing so its clearing delimiters
        String cleanCustomerName = order.getCustomerName().replace(DELIMITER, "");
        String cleanStateAbbr = order.getStateAbbr().replace(DELIMITER, "");
        String cleanProductType = order.getProductType().replace(DELIMITER, "");

        order.setCustomerName(cleanCustomerName);
        order.setStateAbbr(cleanStateAbbr);
        order.setProductType(cleanProductType);

        return order;
    }

    private List<Order> loadOrders(LocalDate chosenDate) throws FlooringMasterPersistenceException {
        //Loads one file for a specific date
        Scanner scanner;
        String fileDate = chosenDate.format(DateTimeFormatter.ofPattern("MMddyyyy"));

        File f = new File(String.format(dataFolder + "Orders_%s.txt", fileDate));

        List<Order> orders = new ArrayList<>();

        if (f.isFile()) {
            try {
                scanner = new Scanner(
                        new BufferedReader(
                                new FileReader(f)));
            } catch (FileNotFoundException e) {
                throw new FlooringMasterPersistenceException(
                        "-_- Could not load order data into memory.", e);
            }
            String currentLine;
            String[] currentTokens;
            scanner.nextLine();//Skips scanning document headers
            while (scanner.hasNextLine()) {
                currentLine = scanner.nextLine();
                currentTokens = currentLine.split(DELIMITER);
                if (currentTokens.length == 12) {
                    Order currentOrder = new Order();
                    currentOrder.setDate(LocalDate.parse(fileDate,
                            DateTimeFormatter.ofPattern("MMddyyyy")));
                    currentOrder.setOrderNumber(Integer.parseInt(currentTokens[0]));
                    currentOrder.setCustomerName(currentTokens[1]);
                    currentOrder.setStateAbbr(currentTokens[2]);
                    currentOrder.setTaxRate(new BigDecimal(currentTokens[3]));
                    currentOrder.setProductType(currentTokens[4]);
                    currentOrder.setArea(new BigDecimal(currentTokens[5]));
                    currentOrder.setMaterialCostPerSquareFoot(new BigDecimal(currentTokens[6]));
                    currentOrder.setLaborCostPerSquareFoot(new BigDecimal(currentTokens[7]));
                    currentOrder.setMaterialCost(new BigDecimal(currentTokens[8]));
                    currentOrder.setLaborCost(new BigDecimal(currentTokens[9]));
                    currentOrder.setTax(new BigDecimal(currentTokens[10]));
                    currentOrder.setTotal(new BigDecimal(currentTokens[11]));
                    orders.add(currentOrder);
                } else {
                    //Ignore line.
                }
            }
            scanner.close();
            return orders;
        } else {
            return orders;
        }
    }

    private void writeOrders(List<Order> orders, LocalDate orderDate)
            throws FlooringMasterPersistenceException {

        PrintWriter out;

        String fileDate = orderDate.format(DateTimeFormatter.
                ofPattern("MMddyyyy"));

        File f = new File(String.format(dataFolder + "Orders_%s.txt", fileDate));

        try {
            out = new PrintWriter(new FileWriter(f));
        } catch (IOException e) {
            throw new FlooringMasterPersistenceException(
                    "Could not save order data.", e);
        }

        // Write out the Order objects to the file.
        out.println(HEADER);
        for (Order currentOrder : orders) {
            // Write the Order objects to the file
            out.println(currentOrder.getOrderNumber() + DELIMITER
                    + currentOrder.getCustomerName() + DELIMITER
                    + currentOrder.getStateAbbr() + DELIMITER
                    + currentOrder.getTaxRate() + DELIMITER
                    + currentOrder.getProductType() + DELIMITER
                    + currentOrder.getArea() + DELIMITER
                    + currentOrder.getMaterialCostPerSquareFoot() + DELIMITER
                    + currentOrder.getLaborCostPerSquareFoot() + DELIMITER
                    + currentOrder.getMaterialCost() + DELIMITER
                    + currentOrder.getLaborCost() + DELIMITER
                    + currentOrder.getTax() + DELIMITER
                    + currentOrder.getTotal());
            
            out.flush();
        }
        out.close();
    }
}
