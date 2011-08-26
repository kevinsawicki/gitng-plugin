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

import hudson.scm.PollingResult;

import java.io.File;
import java.util.Collections;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.jenkinsci.git.BuildRepository;
import org.jenkinsci.git.BuildRepositoryState;
import org.jenkinsci.git.PollOperation;
import org.junit.Test;

/**
 * Unit tests of {@link PollOperation}
 *
 * @author Kevin Sawicki (kevin@github.com)
 */
public class PollOperationTest extends GitTestCase {

	/**
	 * Create operation with null baseline
	 */
	@Test(expected = IllegalArgumentException.class)
	public void nullBaseline() {
		new PollOperation(null, Collections.<BuildRepository> emptyList());
	}

	/**
	 * Create operation with null repository list
	 */
	@Test(expected = IllegalArgumentException.class)
	public void nullRepos() {
		new PollOperation(new BuildRepositoryState(), null);
	}

	/**
	 * Test polling an empty list of repositories
	 *
	 * @throws Exception
	 */
	@Test
	public void emptyRepos() throws Exception {
		PollOperation op = new PollOperation(new BuildRepositoryState(),
				Collections.<BuildRepository> emptyList());
		assertEquals(PollingResult.NO_CHANGES, op.invoke(null, null));
	}

	/**
	 * Test polling with an empty repository state
	 *
	 * @throws Exception
	 */
	@Test
	public void emptyState() throws Exception {
		git.add("file.txt", "a");
		BuildRepository repo = new BuildRepository(git.repo().getDirectory()
				.toURI().toString(), "refs/heads/master", null);
		CloneOperation clone = new CloneOperation(repo);
		File dir = git.tempDirectory();
		assertNotNull(clone.invoke(dir, null));
		PollOperation op = new PollOperation(new BuildRepositoryState(),
				Collections.singletonList(repo));
		assertEquals(PollingResult.BUILD_NOW, op.invoke(dir, null));
	}

	/**
	 * Test polling a {@link BuildRepository} that does not resolve to a
	 * {@link Repository}
	 *
	 * @throws Exception
	 */
	@Test
	public void noRepo() throws Exception {
		BuildRepository repo = new BuildRepository("a", "master", null);
		PollOperation op = new PollOperation(new BuildRepositoryState(),
				Collections.singletonList(repo));
		File dir = git.tempDirectory();
		assertEquals(PollingResult.BUILD_NOW, op.invoke(dir, null));
	}

	/**
	 * Test polling a {@link BuildRepository} with no new changes
	 *
	 * @throws Exception
	 */
	@Test
	public void noChanges() throws Exception {
		RevCommit commit = git.add("file.txt", "a");
		BuildRepository repo = new BuildRepository(git.repo().getDirectory()
				.toURI().toString(), "refs/heads/master", null);
		CloneOperation clone = new CloneOperation(repo);
		File dir = git.tempDirectory();
		assertNotNull(clone.invoke(dir, null));
		BuildRepositoryState state = new BuildRepositoryState();
		state.put(repo, commit);
		PollOperation op = new PollOperation(state,
				Collections.singletonList(repo));
		assertEquals(PollingResult.NO_CHANGES, op.invoke(dir, null));
	}

	/**
	 * Test polling a {@link BuildRepository} with one new change
	 *
	 * @throws Exception
	 */
	@Test
	public void oneChange() throws Exception {
		RevCommit commit = git.add("file.txt", "a");
		BuildRepository repo = new BuildRepository(git.repo().getDirectory()
				.toURI().toString(), "refs/heads/master", null);
		CloneOperation clone = new CloneOperation(repo);
		File dir = git.tempDirectory();
		assertNotNull(clone.invoke(dir, null));
		BuildRepositoryState state = new BuildRepositoryState();
		state.put(repo, commit);
		PollOperation op = new PollOperation(state,
				Collections.singletonList(repo));
		git.add("file2.txt", "b");
		assertEquals(PollingResult.SIGNIFICANT, op.invoke(dir, null));
	}
}
