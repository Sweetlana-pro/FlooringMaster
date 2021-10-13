/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmaster.controller;


import com.sg.flooringmaster.dto.Order;
import com.sg.flooringmaster.dto.Product;
import com.sg.flooringmaster.service.FlooringMasterServiceLayer;
import com.sg.flooringmaster.ui.FlooringMasterView;
import com.sg.flooringmaster.service.FlooringMasterPersistenceException;
import com.sg.flooringmaster.service.FlooringMasterDataValidationException;
import com.sg.flooringmaster.service.InvalidOrderNumberException;
import com.sg.flooringmaster.service.ProductValidationException;
import com.sg.flooringmaster.service.StateValidationException;
import com.sg.flooringmaster.ui.UserIO;
import com.sg.flooringmaster.ui.UserIOConsoleImpl;
import java.time.LocalDate;
import java.util.List;
/**
 *
 * @author pro
 */
public class FlooringMasterController {
    private UserIO io = new UserIOConsoleImpl ();
    private FlooringMasterView view;
    private FlooringMasterServiceLayer service;

    public FlooringMasterController(FlooringMasterServiceLayer service, FlooringMasterView view) {
        this.service = service;
        this.view = view;
    }

    public void run() {

        boolean keepGoing = true;
        int menuSelection = 0;
        try {
            while (keepGoing) {

                menuSelection = getMenuSelection();

                switch (menuSelection) {
                    case 1:
                        getOrdersByDate();
                        break;
                    case 2:
                        listProdType();
                        addOrder();
                        break;
                    case 3:
                        editOrder();
                        break;
                    case 4:
                        removeOrder();
                        break;
                    case 5:
                        io.print("EXPORT DATA");
                        break;
                    case 6:
                        keepGoing = false;
                        break;
                    default:
                        unknownCommand();
                }

            }
            exitMessage();
        } catch (FlooringMasterPersistenceException e) {
            view.displayErrorMessage(e.getMessage());
        }
    }

    private int getMenuSelection() {
        return view.printMenuAndGetSelection();
    }
    private void listProdType() throws FlooringMasterPersistenceException {
        view.displayProductsTypeBanner();
        List<Product> products = service.getAllProducts();
        view.displayProductsList(products);

    }

    private void getOrdersByDate() throws FlooringMasterPersistenceException, FlooringMasterPersistenceException {
        LocalDate dateChoice = view.inputDate();
        view.displayDateBanner(dateChoice);
        try {
            view.displayDateOrders(service.getOrders(dateChoice));
            view.displayContinue();
        } catch (InvalidOrderNumberException e) {
            view.displayErrorMessage(e.getMessage());
        }
    }

    private void addOrder() throws FlooringMasterPersistenceException, FlooringMasterPersistenceException {
        try {
            Order o = service.calculateOrder(view.getOrder());
            view.displayOrder(o);
            String response = view.askSave();
            if (response.equalsIgnoreCase("Y")) {
                service.addOrder(o);
                view.displayAddOrderSuccess(true, o);
            } else if (response.equalsIgnoreCase("N")) {
                view.displayAddOrderSuccess(false, o);
            } else {
                unknownCommand();
            }
        } catch (FlooringMasterDataValidationException
                | StateValidationException | ProductValidationException e) {
            view.displayErrorMessage(e.getMessage());
        }
    }

    private void editOrder() throws FlooringMasterPersistenceException {
        view.displayEditOrderBanner();
        try {
            LocalDate dateChoice = view.inputDate();
            int orderNumber = view.inputOrderNumber();
            Order savedOrder = service.getOrder(dateChoice, orderNumber);
            Order editedOrder = view.editOrderInfo(savedOrder);
            Order updatedOrder = service.compareOrders(savedOrder, editedOrder);
            view.displayEditOrderBanner();
            view.displayOrder(updatedOrder);
            String response = view.askSave();
            if (response.equalsIgnoreCase("Y")) {
                service.editOrder(updatedOrder);
                view.displayEditOrderSuccess(true, updatedOrder);
            } else if (response.equalsIgnoreCase("N")) {
                view.displayEditOrderSuccess(false, updatedOrder);
            } else {
                unknownCommand();
            }
        } catch (InvalidOrderNumberException
                | ProductValidationException | StateValidationException e) {
            view.displayErrorMessage(e.getMessage());
        }
    }

    private void removeOrder() throws FlooringMasterPersistenceException {
        view.displayRemoveOrderBanner();
        LocalDate dateChoice = view.inputDate();
        view.displayDateBanner(dateChoice);
        try {
            view.displayDateOrders(service.getOrders(dateChoice));
            int orderNumber = view.inputOrderNumber();
            Order o = service.getOrder(dateChoice, orderNumber);
            view.displayRemoveOrderBanner();
            view.displayOrder(o);
            String response = view.askRemove();
            if (response.equalsIgnoreCase("Y")) {
                service.removeOrder(o);
                view.displayRemoveOrderSuccess(true, o);
            } else if (response.equalsIgnoreCase("N")) {
                view.displayRemoveOrderSuccess(false, o);
            } else {
                unknownCommand();
            }
        } catch (InvalidOrderNumberException e) {
            view.displayErrorMessage(e.getMessage());
        }
    }

    private void unknownCommand() {
        view.displayUnknownCommandBanner();
    }

    private void exitMessage() {
        view.displayExitBanner();
    }
}

