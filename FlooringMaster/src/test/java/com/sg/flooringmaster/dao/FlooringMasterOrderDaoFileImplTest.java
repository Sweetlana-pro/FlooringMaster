/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmaster.dao;

import com.sg.flooringmaster.service.FlooringMasterPersistenceException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Sweetlana Protsenko
 */
public class FlooringMasterOrderDaoFileImplTest {
    FlooringMasterOrderDao testDao;
    
    public FlooringMasterOrderDaoFileImplTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() throws Exception {
        /*String testFile = "testOrders.txt";
        //Use the FileWriter to quickly blank the file
        new FileWriter (testFile);
        testDao = new FlooringMasterOrderDaoFileImpl(testFile);
        PrintWriter out;

	    try {
	        out = new PrintWriter(new FileWriter(testFile));
	    } catch (IOException e) {
	        throw new FlooringMasterPersistenceException(
	                "Could not save inventory data.", e);
	    }
            out.println("1::Snorlax::0.25::2");
            out.println("2::Picachu::0.45::2");
            out.println("3::Eevee::0.50::1");
            out.println("4::Mewtwo::0.60::2");
            out.flush();
            out.close();
        testDao = new FlooringMasterOrderDaoFileImpl(testFile);*/
    }
    
    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testSomeMethod() {
        fail("The test case is a prototype.");
    }
    
    
}
