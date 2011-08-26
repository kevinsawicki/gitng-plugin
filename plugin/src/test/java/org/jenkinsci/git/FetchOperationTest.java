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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

/**
 * Unit tests of {@link FetchOperation}
 *
 * @author Kevin Sawicki (kevin@github.com)
 */
public class FetchOperationTest extends GitTestCase {

	/**
	 * Create operation with null build repository
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void nullBuildRepository() throws IOException {
		new FetchOperation(null, git.repo());
	}

	/**
	 * Create operation with null Git repository
	 */
	@Test(expected = IllegalArgumentException.class)
	public void nullGitRepository() {
		new FetchOperation(new BuildRepository("a", "b", "c"), null);
	}

	/**
	 * Test fetching single commit
	 *
	 * @throws Exception
	 */
	@Test
	public void fetchSingleCommit() throws Exception {
		git.add("file.txt", "a");
		File dir = git.tempDirectory();
		BuildRepository repo = new BuildRepository(git.repo().getDirectory()
				.toURI().toString(), Constants.R_HEADS + Constants.MASTER, null);
		CloneOperation clone = new CloneOperation(repo);
		Repository cloned = clone.invoke(dir, null);
		assertNotNull(cloned);
		RevCommit commit2 = git.add("file1.txt", "b");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		StreamProgressMonitor monitor = new StreamProgressMonitor(
				new PrintStream(out));
		FetchOperation fetch = new FetchOperation(repo, cloned, monitor);
		RevCommit fetched = fetch.call();
		assertEquals(commit2, fetched);
		assertTrue(out.toString().length() > 0);
	}
}
