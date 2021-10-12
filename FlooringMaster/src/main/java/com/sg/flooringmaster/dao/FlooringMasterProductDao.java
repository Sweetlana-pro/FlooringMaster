/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmaster.dao;

import com.sg.flooringmaster.dto.Product;
import com.sg.flooringmaster.service.FlooringMasterPersistenceException;
import java.util.List;

/**
 *
 * @author pro
 */
public interface FlooringMasterProductDao {
    //List<Product> getProducts();

    Product getProduct(String productType) throws FlooringMasterPersistenceException;

}
