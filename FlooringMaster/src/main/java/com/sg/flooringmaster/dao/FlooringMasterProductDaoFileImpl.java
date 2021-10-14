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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pro
 */
public class FlooringMasterProductDaoFileImpl implements FlooringMasterProductDao {
    
    private final String PRODUCT_FILE;
    public  final String DELIMITER = ",";
    
    public FlooringMasterProductDaoFileImpl() {
        PRODUCT_FILE = "Products.txt";
    }
    public FlooringMasterProductDaoFileImpl(String productTextFile) {
        PRODUCT_FILE = productTextFile;
    }

    //private List<Product> products = new ArrayList<>();
    private Map<String, Product> productMap = new HashMap<>();
    
    @Override
    public List<Product> getProducts() {
        try {
            loadProducts();
        } catch (FlooringMasterPersistenceException ex) {
            Logger.getLogger(FlooringMasterProductDaoFileImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<Product>(productMap.values());
    }
    
    @Override
    public Product getProduct(String productType) throws FlooringMasterPersistenceException {
        loadProducts();
        return productMap.get(productType);
        /*List<Product> products = loadProducts();
        if (products == null) {
            return null;
        } else {
            Product chosenProduct = products.stream()
                    .filter(p -> p.getProductType().equalsIgnoreCase(productType))
                    .findFirst().orElse(null);
            return chosenProduct;
        }*/
    }
    //Translating a line of text into object
    private Product unmarshallProducts (String productsAsText) {
        String[] productsTokens = productsAsText.split(DELIMITER);
        String productType = productsTokens[0];
        Product productFromFile = new Product(productType);
        productFromFile.setMaterialCostPerSquareFoot(new BigDecimal(productsTokens[1]));
        productFromFile.setLaborCostPerSquareFoot(new BigDecimal (productsTokens[2]));
        
        return productFromFile;
    }

    private void /*List<Product>*/ loadProducts() throws FlooringMasterPersistenceException {
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
        Product currentProduct;
        //String[] currentTokens;

        while (scanner.hasNextLine()) {
            currentLine = scanner.nextLine();
            currentProduct = unmarshallProducts (currentLine);
            
            productMap.put(currentProduct.getProductType(), currentProduct);
        }
        scanner.close();
        
    }
}
