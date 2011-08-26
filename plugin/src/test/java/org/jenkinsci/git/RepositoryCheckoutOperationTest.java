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

import hudson.FilePath;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepository;
import org.gitective.core.CommitUtils;
import org.jenkinsci.git.log.CommitLog;
import org.jenkinsci.git.log.CommitLogReader;
import org.junit.Test;

/**
 * Unit tests of {@link RepositoryCheckoutOperation}
 *
 * @author Kevin Sawicki (kevin@github.com)
 */
public class RepositoryCheckoutOperationTest extends GitTestCase {

	/**
	 * Create operation with null repositories
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void nullRepositories() throws IOException {
		new RepositoryCheckoutOperation(null, new FilePath(File.createTempFile(
				"test", ".txt")));
	}

	/**
	 * Create operation with null log
	 */
	@Test(expected = IllegalArgumentException.class)
	public void nullLog() {
		new RepositoryCheckoutOperation(new ArrayList<BuildRepository>(), null);
	}

	/**
	 * Testing initially cloning a repository and then fetching a new commit and
	 * then another new commit
	 *
	 * @throws Exception
	 */
	@Test
	public void checkoutThreeSequentialSingleCommits() throws Exception {
		File dir = git.tempDirectory();
		File gitDir = new File(dir, Constants.DOT_GIT);
		File log = File.createTempFile("log", ".txt");
		BuildRepository repo = new BuildRepository(git.repo().getDirectory()
				.toURI().toString(), BuildRepository.BRANCH_DEFAULT, null);

		RevCommit commit1 = git.add("file.txt", "content");

		RepositoryCheckoutOperation op = new RepositoryCheckoutOperation(
				Collections.singletonList(repo), new FilePath(log));
		assertTrue(op.invoke(dir, null));

		CommitLogReader reader = new CommitLogReader();
		CommitLog cl = reader.parse(null, log);
		assertNotNull(cl);
		assertFalse(cl.isEmptySet());
		assertEquals(1, cl.toArray().length);
		assertEquals(commit1.name(), cl.iterator().next().getCommitId());
		assertTrue(log.delete());
		assertTrue(log.createNewFile());

		Repository gitRepo = new FileRepository(gitDir);
		assertFalse(git.repo().getDirectory().equals(gitRepo.getDirectory()));
		assertEquals(commit1, CommitUtils.getLatest(gitRepo));

		RevCommit commit2 = git.add("file.txt", "new content");
		assertTrue(op.invoke(dir, null));
		assertEquals(commit2, CommitUtils.getLatest(gitRepo));

		cl = reader.parse(null, log);
		assertNotNull(cl);
		assertFalse(cl.isEmptySet());
		assertEquals(1, cl.toArray().length);
		assertEquals(commit2.name(), cl.iterator().next().getCommitId());

		RevCommit commit3 = git.add("file.txt", "less content");
		assertTrue(op.invoke(dir, null));
		assertEquals(commit3, CommitUtils.getLatest(gitRepo));

		cl = reader.parse(null, log);
		assertNotNull(cl);
		assertFalse(cl.isEmptySet());
		assertEquals(1, cl.toArray().length);
		assertEquals(commit3.name(), cl.iterator().next().getCommitId());
	}
}
