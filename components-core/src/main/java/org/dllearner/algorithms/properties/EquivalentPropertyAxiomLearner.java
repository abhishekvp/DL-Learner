package org.dllearner.algorithms.properties;

import java.util.List;

import org.dllearner.core.AxiomLearningAlgorithm;
import org.dllearner.core.Component;
import org.dllearner.core.ComponentInitException;
import org.dllearner.core.configurators.Configurator;
import org.dllearner.core.owl.Axiom;
import org.dllearner.kb.SparqlEndpointKS;

public class EquivalentPropertyAxiomLearner extends Component implements AxiomLearningAlgorithm {
	
	@org.dllearner.ConfigOption(name="propertyToDescribe", description="property of which a axiom should be learned")
	private String propertyToDescribe;
	
	public String getPropertyToDescribe() {
		return propertyToDescribe;
	}

	public void setPropertyToDescribe(String propertyToDescribe) {
		this.propertyToDescribe = propertyToDescribe;
	}

	public EquivalentPropertyAxiomLearner(SparqlEndpointKS ks){
		
	}
	
	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Axiom> getCurrentlyBestAxioms(int nrOfAxioms) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Configurator getConfigurator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init() throws ComponentInitException {
		// TODO Auto-generated method stub
		
	}

}
