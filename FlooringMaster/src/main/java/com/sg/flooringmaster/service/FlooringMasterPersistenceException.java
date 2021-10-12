/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmaster.service;

/**
 *
 * @author pro
 */
public class FlooringMasterPersistenceException extends Exception {
    public FlooringMasterPersistenceException(String message) {
        super(message);
    }

    public FlooringMasterPersistenceException(String message, Throwable cause){
        super(message, cause);
    }
}
