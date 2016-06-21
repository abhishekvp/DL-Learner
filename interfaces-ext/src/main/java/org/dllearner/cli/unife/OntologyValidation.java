/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dllearner.cli.unife;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dllearner.cli.CLIBase2;
import org.dllearner.core.ComponentAnn;
import org.dllearner.core.LearningProblem;
import org.dllearner.core.probabilistic.unife.OWLProbReasonerResult;
import org.dllearner.core.probabilistic.unife.OWLProbabilisticReasoner;
import org.dllearner.learningproblems.PosNegLP;
import org.dllearner.learningproblems.PosOnlyLP;
import org.dllearner.utils.unife.OWLUtils;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import static org.dllearner.utils.unife.GeneralUtils.safe;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Giuseppe Cota <giuseppe.cota@unife.it>
 */
/**
 * Evaluate a Probabilistic Knowledge Base (Ontology). It runs test queries over
 * the learned ontology.
 *
 * @author Giuseppe Cota <giuseppe.cota@unife.it>
 */
@ComponentAnn(name = "Ontology validator", version = 0, shortName = "")
public class OntologyValidation extends CLIBase2 {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(OntologyValidation.class);

    //private ParameterLearningAlgorithm pla;
    private OWLOntology learnedOntology;

    //private OWLOntology initialOntology;
    @Autowired
    private LearningProblem lp;

    @Autowired
    private OWLProbabilisticReasoner reasoner;

    private OWLClassExpression classExpression;

    private String outputFile;

    @Override
    public void init() throws IOException {
        super.init();
    }

    @Override
    public void run() {
        Set<OWLIndividual> posIndividuals = null;
        Set<OWLIndividual> negIndividuals = null;
        Set<OWLAxiom> posTestQueries;
        Set<OWLAxiom> negTestQueries;
        if (lp instanceof PosNegLP) {
            posIndividuals = ((PosNegLP) lp).getPositiveExamples();
            negIndividuals = ((PosNegLP) lp).getNegativeExamples();
        } else if (lp instanceof PosOnlyLP) {
            posIndividuals = ((PosOnlyLP) lp).getPositiveExamples();
        } else {
            throw new UnsupportedOperationException("Unsupported learning problem: " + lp.getClass());
        }
        // convert the individuals into assertional axioms
        posTestQueries = OWLUtils.convertIndividualsToAssertionalAxioms(posIndividuals, classExpression);
        negTestQueries = OWLUtils.convertIndividualsToAssertionalAxioms(safe(negIndividuals), classExpression);
        // run the test queries over the initial ontology
        Set<OWLProbReasonerResult> posTestResults = computeQueries(posTestQueries);
        Set<OWLProbReasonerResult> negTestResults = computeQueries(negTestQueries);
        try {
            // write result on output test configuration file
            PrintWriter outFile = new PrintWriter(outputFile, "UTF-8");
            outFile.println("pos: " + posTestResults.size());
            outFile.println("neg: " + negTestResults.size());
            outFile.print("values: ");
            for (OWLProbReasonerResult q : posTestResults) {
                outFile.print(q.getProbability() + "-pos");
                outFile.print(",");
            }
            Iterator<OWLProbReasonerResult> it = negTestResults.iterator();
            while (it.hasNext()) {
                OWLProbReasonerResult q = it.next();
                outFile.print(q.getProbability() + "-neg");
                if (it.hasNext()) {
                    outFile.print(",");
                }
                outFile.println();
            }
            outFile.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            logger.error("Impossible to write output file " + outputFile + ". "
                    + "Reason: " + ex.getMessage());
        }

    }

    /**
     * It computes a set of probabilistic queries
     */
    private Set<OWLProbReasonerResult> computeQueries(Set<OWLAxiom> queries) {
        Set<OWLProbReasonerResult> results = new HashSet<>();
        for (OWLAxiom query : queries) {
            results.add(reasoner.computeQuery(query));
        }
        return results;
    }

    /**
     * @param pla the pla to set
     */
//    @Autowired
//    public void setPla(ParameterLearningAlgorithm pla) {
//        this.pla = pla;
//    }
    /**
     * @param learnedOntology the learnedOntology to set
     */
    public void setLearnedOntology(OWLOntology learnedOntology) {
        this.learnedOntology = learnedOntology;
    }

//    /**
//     * @param initialOntology the initialOntology to set
//     */
//    public void setInitialOntology(OWLOntology initialOntology) {
//        this.initialOntology = initialOntology;
//    }
    /**
     * @param outputFile the outputFile to set
     */
    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

}