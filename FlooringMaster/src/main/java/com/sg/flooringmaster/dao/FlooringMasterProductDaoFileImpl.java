/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmaster.dao;

import com.sg.flooringmaster.dto.Product;
import com.sg.flooringmaster.service.FlooringMasterPersistenceException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pro
 */
public class FlooringMasterProductDaoFileImpl implements FlooringMasterProductDao {
    public static final String PRODUCT_FILE = "Products.txt";
    public static final String DELIMITER = ",";
/*
    private List<Product> products = new ArrayList<>();

    @Override
    public List<Product> getProducts() {
        try {
            loadProducts();
        } catch (DataPersistenceException ex) {
            Logger.getLogger(FlooringMasterProductDaoFileImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<Product>(products);
    }*/

      @Override
    public Product getProduct(String productType) throws FlooringMasterPersistenceException {
        List<Product> products = loadProducts();
        if (products == null) {
            return null;
        } else {
            Product chosenProduct = products.stream()
                    .filter(p -> p.getProductType().equalsIgnoreCase(productType))
                    .findFirst().orElse(null);
            return chosenProduct;
        }
    }

    private List<Product> loadProducts() throws FlooringMasterPersistenceException {
        Scanner scanner;
        List<Product> products = new ArrayList<>();

        try {
            scanner = new Scanner(
                    new BufferedReader(
                            new FileReader(PRODUCT_FILE)));
        } catch (FileNotFoundException e) {
            throw new FlooringMasterPersistenceException(
                    "-_- Could not load products data into memory.", e);
        }

        String currentLine;
        String[] currentTokens;
        scanner.nextLine();//Skips scanning document headers       
        while (scanner.hasNextLine()) {
            currentLine = scanner.nextLine();
            currentTokens = currentLine.split(DELIMITER);
            if (currentTokens.length == 3) {
                Product currentProduct = new Product();
                currentProduct.setProductType(currentTokens[0]);
                currentProduct.setMaterialCostPerSquareFoot(new BigDecimal(currentTokens[1]));
                currentProduct.setLaborCostPerSquareFoot(new BigDecimal(currentTokens[2]));
                // Put currentProduct into the map using productType as the key
                products.add(currentProduct);
            } else {
                //Ignores line if delimited wrong or empty.
            }
        }
        scanner.close();

        if (!products.isEmpty()) {
            return products;
        } else {
            return null;
        }
    }
}
