/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dllearner.unife.probabilistic.core;

import org.dllearner.core.ComponentInitException;
import org.dllearner.unife.reasoning.ProbabilisticReasonerType;

/**
 *
 * @author Giuseppe Cota <giuseta@gmail.com>, Riccardo Zese
 * <riccardo.zese@unife.it>
 */
public class BUNDLEWrapper extends AbstractProbabilisticReasonerComponent{

    @Override
    public ProbabilisticReasonerType getReasonerType() {
        return ProbabilisticReasonerType.BUNDLE;
    }

    @Override
    public void init() throws ComponentInitException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}