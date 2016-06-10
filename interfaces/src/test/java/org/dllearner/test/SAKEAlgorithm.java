package org.dllearner.test;

import org.dllearner.algorithms.celoe.CELOE;
import org.dllearner.algorithms.celoe.OEHeuristicRuntime;
import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.ComponentInitException;
import org.dllearner.core.StringRenderer;
import org.dllearner.kb.SparqlEndpointKS;
import org.dllearner.learningproblems.PosNegLPStandard;
import org.dllearner.reasoning.SPARQLReasoner;
import org.dllearner.refinementoperators.RhoDRDown;
import org.dllearner.utilities.OWLAPIUtils;
import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Execute the SAKE Algoritnm
 */
public class SAKEAlgorithm {
	final static String NS = "http://www.ontos.com/sake/extractor#";
	public static void main(String[] args) throws MalformedURLException, ComponentInitException {
		StringRenderer.setRenderer(StringRenderer.Rendering.DL_SYNTAX);

		SparqlEndpointKS ks = new SparqlEndpointKS();
		ks.setUrl(new URL("http://sake.informatik.uni-leipzig.de:8850/sparql"));
		ks.setDefaultGraphURIs(Collections.singletonList("http://sake-projekt.de"));
		ks.setUseCache(false);
		ks.init();
		SPARQLReasoner sparqlReasoner = new SPARQLReasoner(ks);
		sparqlReasoner.tryPropertyPath = true;
		AbstractReasonerComponent rc = sparqlReasoner;
		rc.init();

		OWLDataFactory df = new OWLDataFactoryImpl();
		System.err.println("class hierarchy="+rc.getClassHierarchy());
		System.err.println("op hierarchy="+rc.getObjectPropertyHierarchy());
		System.err.println("dp hierarchy="+rc.getDatatypePropertyHierarchy());
		Set<OWLClass> classes = rc.getClasses();
		//System.out.println("classes = " + classes);
		SortedSet<OWLIndividual> failureEvents = rc.getIndividuals(
				OWLAPIUtils.fromManchester("containsFailureData some FailureData", rc, df, true));
		System.out.println(failureEvents.size());
		SortedSet<OWLIndividual> nonFatalEvents = rc.getIndividuals(
				OWLAPIUtils.fromManchester("containsFailureData some (FailureData and not FailureData_999999)", rc, df, true));
		System.out.println(nonFatalEvents.size());
		SortedSet<OWLIndividual> fatalEvents = rc.getIndividuals(
				OWLAPIUtils.fromManchester("containsFailureData some FailureData_999999", rc, df, true));
		System.out.println(fatalEvents.size());

		ArrayList<OWLIndividual> samplingList = new ArrayList<OWLIndividual>(nonFatalEvents);
		Collections.shuffle(samplingList);

		System.err.println("failure events="+fatalEvents);


		for (int i = 0; i < 5; i++) {
			System.err.println("now using sample " + (i + 1));

			PosNegLPStandard lp = new PosNegLPStandard(rc);
			lp.setPositiveExamples(fatalEvents);
			HashSet<OWLIndividual> negativeExamples = new HashSet<>(samplingList.subList(i * fatalEvents.size(), (i + 1) * fatalEvents.size()));
			lp.setNegativeExamples(negativeExamples);
			System.err.println("sample=" + negativeExamples);
			lp.init();


			OWLClassExpression startClass = OWLAPIUtils.fromManchester("Event", rc, df, true);

			RhoDRDown op = new RhoDRDown();
			op.setReasoner(rc);
			op.setStartClass(startClass);
			op.setClassHierarchy(rc.getClassHierarchy());
			op.setObjectPropertyHierarchy(rc.getObjectPropertyHierarchy());
			op.setDataPropertyHierarchy(rc.getDatatypePropertyHierarchy());
			op.setUseCardinalityRestrictions(false);
			op.init();

			OEHeuristicRuntime h = new OEHeuristicRuntime();
			h.setExpansionPenaltyFactor(0.001);
			h.init();

			CELOE celoe = new CELOE(lp, rc);
			celoe.setIgnoredObjectProperties(Collections.singleton(
					df.getOWLObjectProperty(IRI.create(NS + "hasTelegram"))));
			celoe.setOperator(op);
			celoe.setStartClass(startClass);
			celoe.setMaxExecutionTimeInSeconds(600);
			celoe.setHeuristic(h);
			celoe.setReplaceSearchTree(true);
			celoe.setWriteSearchTree(true);
			celoe.setSearchTreeFile("sake.tree");
			celoe.setMaxDepth(15);
			celoe.init();

			celoe.start();
		}
	}
}