/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmaster;

import com.sg.flooringmaster.controller.FlooringMasterController;
import com.sg.flooringmaster.dao.FlooringMasterOrderDao;
import com.sg.flooringmaster.dao.FlooringMasterOrderDaoFileImpl;
import com.sg.flooringmaster.dao.FlooringMasterProductDao;
import com.sg.flooringmaster.dao.FlooringMasterProductDaoFileImpl;
import com.sg.flooringmaster.dao.FlooringMasterStateDao;
import com.sg.flooringmaster.dao.FlooringMasterStateDaoFileImpl;
import com.sg.flooringmaster.service.FlooringMasterDataValidationException;
import com.sg.flooringmaster.service.FlooringMasterPersistenceException;
import com.sg.flooringmaster.service.FlooringMasterServiceLayer;
import com.sg.flooringmaster.service.FlooringMasterServiceLayerImpl;
import com.sg.flooringmaster.ui.FlooringMasterView;
import com.sg.flooringmaster.ui.UserIO;
import com.sg.flooringmaster.ui.UserIOConsoleImpl;

/**
 *
 * @author pro
 */
public class App {
    public static void main(String[] args) throws FlooringMasterDataValidationException, FlooringMasterPersistenceException {
        UserIO myIo = new UserIOConsoleImpl();
        FlooringMasterView myView = new FlooringMasterView(myIo);
        FlooringMasterOrderDao myOrderDao = new FlooringMasterOrderDaoFileImpl();
        FlooringMasterProductDao myProductsDao = new FlooringMasterProductDaoFileImpl();
        FlooringMasterStateDao myStateDao = new FlooringMasterStateDaoFileImpl();
        //ClassRosterAuditDao myAuditDao = new ClassRosterAuditDaoFileImpl();
        FlooringMasterServiceLayer myService = new FlooringMasterServiceLayerImpl(myOrderDao, myProductsDao, myStateDao);
        
        FlooringMasterController controller =new FlooringMasterController(myService, myView);
        controller.run();
    }
    
    
}
