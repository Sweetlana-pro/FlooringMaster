/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmaster.dao;

import com.sg.flooringmaster.dto.State;
import com.sg.flooringmaster.service.FlooringMasterPersistenceException;
import java.math.BigDecimal;

/**
 *
 * @author pro
 */
public interface FlooringMasterStateDao {
    State getState(String stateAbbr) throws FlooringMasterPersistenceException;

}
