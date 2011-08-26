/*
 * Copyright (c) 2011 GitHub Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
package org.jenkinsci.git;

import hudson.scm.SCMRevisionState;

import java.util.Collections;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

/**
 * Unit tests of {@link RepositoryStateOperation}
 *
 * @author Kevin Sawicki (kevin@github.com)
 */
public class RepositoryStateOperationTest extends GitTestCase {

	/**
	 * Create operation with null repositories
	 */
	@Test(expected = IllegalArgumentException.class)
	public void nullRepositories() {
		new RepositoryStateOperation(null);
	}

	/**
	 * Get state with no repositories
	 *
	 * @throws Exception
	 */
	@Test
	public void emptyRepositories() throws Exception {
		RepositoryStateOperation op = new RepositoryStateOperation(
				Collections.<BuildRepository> emptyList());
		assertEquals(SCMRevisionState.NONE, op.invoke(null, null));
	}

	/**
	 * Get state for non-existent repository
	 *
	 * @throws Exception
	 */
	@Test
	public void nonExistentRepository() throws Exception {
		BuildRepository repo = new BuildRepository("a", "b", null);
		RepositoryStateOperation op = new RepositoryStateOperation(
				Collections.singletonList(repo));
		assertEquals(SCMRevisionState.NONE,
				op.invoke(git.tempDirectory(), null));
	}

	/**
	 * Get state for one repository
	 *
	 * @throws Exception
	 */
	@Test
	public void oneRepository() throws Exception {
		RevCommit commit = git.add("file.txt", "a");
		BuildRepository repo = new BuildRepository("a", "b", null);
		RepositoryStateOperation op = new RepositoryStateOperation(
				Collections.singletonList(repo));
		SCMRevisionState state = op.invoke(git.repo().getDirectory(), null);
		assertNotNull(state);
		assertTrue(state instanceof BuildRepositoryState);
		assertEquals(commit, ((BuildRepositoryState) state).get(repo));
	}
}
