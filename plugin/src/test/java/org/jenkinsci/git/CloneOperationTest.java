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

import java.io.File;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.CommitUtils;
import org.junit.Test;

/**
 * Unit tests of {@link CloneOperation}
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class CloneOperationTest extends GitTestCase {

	/**
	 * Create operation with null {@link BuildRepository}
	 */
	@Test(expected = IllegalArgumentException.class)
	public void nullBuildRepository() {
		new CloneOperation(null);
	}

	/**
	 * Test cloning a local repository
	 * 
	 * @throws Exception
	 */
	@Test
	public void cloneRepository() throws Exception {
		Repository existing = git.repo();
		RevCommit commit = git.add("file.txt", "abcd");
		BuildRepository repo = new BuildRepository(existing.getDirectory()
				.toURI().toString(), "master", null);
		File dir = git.tempDirectory();
		CloneOperation op = new CloneOperation(repo);
		Repository cloned = op.invoke(dir, null);
		assertNotNull(cloned);
		assertFalse(existing.getDirectory().equals(cloned.getDirectory()));
		assertTrue(new File(dir, Constants.DOT_GIT).exists());
		assertNotNull(CommitUtils.getCommit(cloned, commit));
	}

	/**
	 * Test cloning into a subdirectory
	 * 
	 * @throws Exception
	 */
	@Test
	public void cloneIntoSubDirectory() throws Exception {
		Repository existing = git.repo();
		RevCommit commit = git.add("file.txt", "abcd");
		BuildRepository repo = new BuildRepository(existing.getDirectory()
				.toURI().toString(), "master", "sub1");
		CloneOperation op = new CloneOperation(repo);
		File dir = git.tempDirectory();
		Repository cloned = op.invoke(dir, null);
		assertNotNull(cloned);
		assertFalse(existing.getDirectory().equals(cloned.getDirectory()));
		assertFalse(new File(dir, Constants.DOT_GIT).exists());
		assertTrue(new File(dir, repo.getDirectory()).exists());
		assertTrue(new File(dir, repo.getDirectory() + File.separatorChar
				+ Constants.DOT_GIT).exists());
		assertNotNull(CommitUtils.getCommit(cloned, commit));
	}
}
