/**
 * Copyright CSIRO Australian e-Health Research Centre (http://aehrc.com).
 * All rights reserved. Use is subject to license terms and conditions.
 */
package au.csiro.snorocket.owlapi;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import junit.framework.Assert;

/**
 * Unit test cases for Protege plugin.
 *
 * @author Alejandro Metke
 *
 */
public class TestSnorocketOWLReasoner {

    /**
     * Uses the Protege plugin to classify a simple ontology that uses concrete
     * domains with equality and inequality operators.
     */
    @Test
    public void testConcreteDomains() {
        System.out.println("Running testConcreteDomains");
        // Load ontology to test
        System.out.println("Loading concrete_domains.owl");
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = null;
        try {
            ontology = manager.loadOntologyFromOntologyDocument(
                    getClass().getResourceAsStream("/concrete_domains.owl"));
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        System.out.println("Classifying");
        // Create Snorocket classifier
        SnorocketReasonerFactory srf = new SnorocketReasonerFactory();
        OWLReasoner reasoner = srf.createNonBufferingReasoner(ontology);

        // Classify
        reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);

        System.out.println("Verifying answer");
        // Verify the X is equivalent to owlNothing
        OWLDataFactory df = OWLManager.getOWLDataFactory();
        OWLClass X = df.getOWLClass(IRI.create("X"));
        Node<OWLClass> bn = reasoner.getBottomClassNode();
        Assert.assertTrue(bn.contains(X));
    }

    @Test
    public void testFunctionalConcreteDomains() {
        System.out.println("Running testConcreteDomains");
        // Load ontology to test
        String[] tests = {
                "functional_concrete_domains_1.owl",
        };
        for (String test: tests) {
            System.out.println("Loading " + test);
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLOntology ontology = null;
            try {
                ontology = manager.loadOntologyFromOntologyDocument(
                        getClass().getResourceAsStream("/" + test));
            } catch (OWLOntologyCreationException e) {
                e.printStackTrace();
                Assert.fail(e.getMessage());
            }

            System.out.println("Classifying");
            // Create Snorocket classifier
            SnorocketReasonerFactory srf = new SnorocketReasonerFactory();
            OWLReasoner reasoner = srf.createNonBufferingReasoner(ontology);

            // Classify
            reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);

            Assert.assertTrue("Ontology should be inconsistent", !reasoner.isConsistent());

            System.out.println("Verifying answer");
            // Verify the X is equivalent to owlNothing
            OWLDataFactory df = OWLManager.getOWLDataFactory();
            OWLClass X = df.getOWLClass(IRI.create("X"));
            Node<OWLClass> bn = reasoner.getBottomClassNode();
            Assert.assertTrue(bn.contains(X));
        }
    }

}
