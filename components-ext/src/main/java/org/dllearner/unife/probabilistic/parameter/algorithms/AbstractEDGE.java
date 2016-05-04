/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dllearner.unife.probabilistic.parameter.algorithms;

import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.ComponentInitException;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.ReasoningMethodUnsupportedException;
import org.dllearner.core.config.ConfigOption;
import org.dllearner.kb.OWLAPIOntology;
import org.dllearner.reasoning.ClosedWorldReasoner;
import org.dllearner.reasoning.FastInstanceChecker;
import org.dllearner.reasoning.OWLAPIReasoner;
import org.dllearner.unife.probabilistic.core.AbstractParameterLearningAlgorithm;
import org.dllearner.unife.probabilistic.core.ParameterLearningException;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import unife.edge.EDGE;
import unife.edge.EDGEStat;
import unife.exception.IllegalValueException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Giuseppe Cota <giuseta@gmail.com>, Riccardo Zese
 * <riccardo.zese@unife.it>
 */
public abstract class AbstractEDGE extends AbstractParameterLearningAlgorithm {

    protected EDGE edge;

    public static enum PossibleOutputFormat {

        OWLXML,
        OWLFUNCTIONAL
    }

    @ConfigOption(name = "seed", description = "seed for random generation", defaultValue = "0")
    protected int seed = 0;

    @ConfigOption(name = "randomize", description = "randomize the starting probabilities of the probabilistic axioms", defaultValue = "false")
    protected boolean randomize = false;

    // se randomize All è true allora anche randomize è true
    @ConfigOption(name = "randomizeAll", description = "randomize all the axioms in the starting probabilistic ontology (including non probabilistic ones)", defaultValue = "false")
    protected boolean randomizeAll = false;

    @ConfigOption(name = "differenceLL", description = "stop difference between log-likelihood of two consecutive EM cycles", defaultValue = "0.000000000028")
    protected double differenceLL = 0.00028;

    @ConfigOption(name = "ratioLL", description = "stop ratio between log-likelihood of two consecutive EM cycles", defaultValue = "0.000000000028")
    protected double ratioLL = 0.00028;

    @ConfigOption(name = "maxIterations", description = "maximum number of cycles", defaultValue = "2147000000")
    protected long maxIterations = 2147000000L;

    @ConfigOption(name = "maxExplanations", description = "the maximum number of explanations to find for each query", defaultValue = "" + Integer.MAX_VALUE)
    protected int maxExplanations = Integer.MAX_VALUE;
    // ATTENZIONE EDGE USA IL FORMATO STRING PER TIMEOUT!!!        
    @ConfigOption(name = "timeout", description = "max time allowed for the inference (format: [0-9]h[0-9]m[0-9]s)", defaultValue = "0s (infinite timeout)")
    protected String timeout = "0s";

    @ConfigOption(name = "showAll", description = "force the visualization of all results", defaultValue = "false")
    protected boolean showAll = false;

    @ConfigOption(name = "outputFileFormat", description = "format of the output file", defaultValue = "OWLXML")
    protected PossibleOutputFormat outputformat = PossibleOutputFormat.OWLXML;

    @ConfigOption(name = "maxPositiveExamples", description = "max number of positive examples that edge must handle when a class learning problem is given", defaultValue = "0 (infinite)")
    protected int maxPositiveExamples = 0;

    @ConfigOption(name = "maxNegativeExamples", description = "max number of negative examples that edge must handle when a class learning problem is given", defaultValue = "0 (infinite)")
    protected int maxNegativeExamples = 0;

    @ConfigOption(name = "accuracy", description = "accuracy used during the computation of the probabilistic values (number of digital places)", defaultValue = "5")
    protected int accuracy = 5;

    @ConfigOption(name = "keepParameters", description = "If true EDGE keeps the old parameter values of all the probabilistic axioms and it does not relearn them", defaultValue = "false")
    protected boolean keepParameters = false;

    protected Set<OWLAxiom> positiveExampleAxioms;
    protected Set<OWLAxiom> negativeExampleAxioms;

    protected EDGEStat results;

    // ontology obtained by merging all the sources
    protected OWLOntology sourcesOntology;
    

    /**
     * Get the Log-Likelihood of all the examples/queries.
     *
     * @return the log-likelihood of all the examples/queries
     */
    public BigDecimal getLL() {
        if (results != null) {
            return results.getLL();
        } else {
//            return new BigDecimal("-500.04"); // stub
            throw new NullPointerException("EDGE results are NULL");
        }
    }

    @Override
    public Map<String, Long> getTimeMap() {
        if (results != null) {
            return results.getTimers();
        } else {
            throw new NullPointerException("EDGE results are NULL");
        }
    }

    @Override
    public void init() throws ComponentInitException {
        try {
            edge.setAccuracy(accuracy);
            edge.setDiffLL("" + differenceLL);
            edge.setMaxExplanations(maxExplanations);
            edge.setMaxIterations(maxIterations);
            edge.setMerge(true);
            edge.setRandomize(randomize);
            edge.setRandomizeAll(randomizeAll);
            edge.setRatioLL("" + ratioLL);
            edge.setSeed(seed);
            edge.setShowAll(showAll);
            edge.setTimeOut(timeout);
            AbstractReasonerComponent rc = learningProblem.getReasoner();
            if (rc instanceof FastInstanceChecker) {
                sourcesOntology = ((FastInstanceChecker) rc).getReasonerComponent().getOntology();
            } else if (rc instanceof ClosedWorldReasoner) {
                sourcesOntology = ((ClosedWorldReasoner) rc).getReasonerComponent().getOntology();
            } else if (rc instanceof OWLAPIReasoner) {
                sourcesOntology = ((OWLAPIReasoner) rc).getOntology();
            } else {
                throw new ReasoningMethodUnsupportedException("Unsupported Reasoning: "
                        + rc.getClass());
            }
            edge.setOntologies(sourcesOntology);
        } catch (IllegalValueException ilve) {
            throw new ComponentInitException(ilve);
        } catch (ReasoningMethodUnsupportedException rmue) {
            throw new ComponentInitException(rmue);
        }
    }

    @Override
    public BigDecimal getParameter(OWLAxiom ax) throws ParameterLearningException {
        Map<OWLAxiom, BigDecimal> pMap = edge.getPMap();
        BigDecimal parameter;
        for (OWLAxiom axiom : pMap.keySet()) {
            if (axiom.equalsIgnoreAnnotations(ax)) {
                parameter = pMap.get(axiom);
                return parameter;
            }
        }
        
        return null;
    }

    /**
     * @return the seed
     */
    public int getSeed() {
        return seed;
    }

    /**
     * @param seed the seed to set
     */
    public void setSeed(int seed) {
        this.seed = seed;
    }

    /**
     * @return the randomize
     */
    public boolean isRandomize() {
        return randomize;
    }

    /**
     * @param randomize the randomize to set
     */
    public void setRandomize(boolean randomize) {
        this.randomize = randomize;
    }

    /**
     * @return the randomizeAll
     */
    public boolean isRandomizeAll() {
        return randomizeAll;
    }

    /**
     * @param randomizeAll the randomizeAll to set
     */
    public void setRandomizeAll(boolean randomizeAll) {
        this.randomizeAll = randomizeAll;
    }

    /**
     * @return the differenceLL
     */
    public double getDifferenceLL() {
        return differenceLL;
    }

    /**
     * @param differenceLL the differenceLL to set
     */
    public void setDifferenceLL(double differenceLL) {
        this.differenceLL = differenceLL;
    }

    /**
     * @return the ratioLL
     */
    public double getRatioLL() {
        return ratioLL;
    }

    /**
     * @param ratioLL the ratioLL to set
     */
    public void setRatioLL(double ratioLL) {
        this.ratioLL = ratioLL;
    }

    /**
     * @return the maxIterations
     */
    public long getMaxIterations() {
        return maxIterations;
    }

    /**
     * @param maxIterations the maxIterations to set
     */
    public void setMaxIterations(long maxIterations) {
        this.maxIterations = maxIterations;
    }

    /**
     * @return the maxExplanations
     */
    public int getMaxExplanations() {
        return maxExplanations;
    }

    /**
     * @param maxExplanations the maxExplanations to set
     */
    public void setMaxExplanations(int maxExplanations) {
        this.maxExplanations = maxExplanations;
    }

    /**
     * @return the timeout
     */
    public String getTimeout() {
        return timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    /**
     * @return the showAll
     */
    public boolean isShowAll() {
        return showAll;
    }

    /**
     * @param showAll the showAll to set
     */
    public void setShowAll(boolean showAll) {
        this.showAll = showAll;
    }

    /**
     * @return the outputformat
     */
    public PossibleOutputFormat getOutputformat() {
        return outputformat;
    }

    /**
     * @param outputformat the outputformat to set
     */
    public void setOutputformat(PossibleOutputFormat outputformat) {
        this.outputformat = outputformat;
    }

    /**
     * @return the maxPositiveExamples
     */
    public int getMaxPositiveExamples() {
        return maxPositiveExamples;
    }

    /**
     * @param maxPositiveExamples the maxPositiveExamples to set
     */
    public void setMaxPositiveExamples(int maxPositiveExamples) {
        this.maxPositiveExamples = maxPositiveExamples;
    }

    /**
     * @return the maxNegativeExamples
     */
    public int getMaxNegativeExamples() {
        return maxNegativeExamples;
    }

    /**
     * @param maxNegativeExamples the maxNegativeExamples to set
     */
    public void setMaxNegativeExamples(int maxNegativeExamples) {
        this.maxNegativeExamples = maxNegativeExamples;
    }

    /**
     * @return the positiveExamples
     */
    public Set<OWLAxiom> getPositiveExampleAxioms() {
        return positiveExampleAxioms;
    }

    /**
     * @param positiveExampleAxioms the positiveExamples to set
     */
    public void setPositiveExampleAxioms(Set<OWLAxiom> positiveExampleAxioms) {
        this.positiveExampleAxioms = positiveExampleAxioms;
    }

    /**
     * @return the negativeExamples
     */
    public Set<OWLAxiom> getNegativeExampleAxioms() {
        return negativeExampleAxioms;
    }

    /**
     * @param negativeExampleAxioms the negativeExamples to set
     */
    public void setNegativeExampleAxioms(Set<OWLAxiom> negativeExampleAxioms) {
        this.negativeExampleAxioms = negativeExampleAxioms;
    }

    /**
     * @return the accuracy
     */
    public int getAccuracy() {
        return accuracy;
    }

    /**
     * @param accuracy the accuracy to set
     */
    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public OWLOntology getLearnedOntology() {
        return edge.getLearnedOntology();
    }

    /**
     * @return the ontologySources
     */
    public OWLOntology getSourcesOntology() {
        return sourcesOntology;
    }

    public void changeSourcesOntology(OWLOntology ontology) {
        sourcesOntology = ontology;
        edge.setOntologies(ontology);
        learningProblem.getReasoner().changeSources(Collections.singleton((KnowledgeSource) new OWLAPIOntology(ontology)));
    }

    public void reset() {
        edge.reset();
        isRunning = false;
        stop = false;
    }

}