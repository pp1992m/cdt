/*******************************************************************************
 * Copyright (c) 2018 Red Hat Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat - Initial Contribution
 *******************************************************************************/
package org.eclipse.cdt.core;

import java.util.List;

import org.eclipse.core.runtime.jobs.Job;

/**
 * @since 6.5
 *
 */
public interface IConsoleParser2 extends IConsoleParser {
	
	/**
	 * Process a line of output in a job
	 * @param s - String to process
	 * @param jobList - list of jobs to add to
	 * @return true if line processed, false otherwise
	 */
	boolean processLine (String s, List<Job> jobList);

}
