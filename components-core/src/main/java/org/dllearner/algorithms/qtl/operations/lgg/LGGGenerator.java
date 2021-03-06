/**
 * Copyright (C) 2007 - 2016, Jens Lehmann
 *
 * This file is part of DL-Learner.
 *
 * DL-Learner is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * DL-Learner is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.dllearner.algorithms.qtl.operations.lgg;

import java.util.List;

import org.dllearner.algorithms.qtl.datastructures.impl.RDFResourceTree;

/**
 * 
 * @author Lorenz Bühmann
 *
 */
public interface LGGGenerator {
	
	/**
	 * Returns the Least General Generalization of two RDF resource trees.
	 * @param tree1 the first tree
	 * @param tree2 the second tree
	 * @return the Least General Generalization
	 */
	RDFResourceTree getLGG(RDFResourceTree tree1, RDFResourceTree tree2);
	
	/**
	 * Returns the Least General Generalization of two RDF resource trees.
	 * @param tree1 the first tree
	 * @param tree2 the second tree
	 * @return the Least General Generalization
	 */
	RDFResourceTree getLGG(RDFResourceTree tree1, RDFResourceTree tree2, boolean learnFilters);
	
	/**
	 * Returns the Least General Generalization of a list of RDF resource trees.
	 * @param trees the trees
	 * @return the Least General Generalization
	 */
	RDFResourceTree getLGG(List<RDFResourceTree> trees);
	
	/**
	 * Returns the Least General Generalization of a list of RDF resource trees. It can be forced to learn filters
	 * on literal values.
	 *
	 * @param trees the trees
	 * @return the Least General Generalization
	 *///todo add better explanation
	RDFResourceTree getLGG(List<RDFResourceTree> trees, boolean learnFilters);

}
