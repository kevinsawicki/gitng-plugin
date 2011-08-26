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
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

/**
 * Unit tests of {@link TreeCheckoutOperation}
 *
 * @author Kevin Sawicki (kevin@github.com)
 */
public class TreeCheckoutOperationTest extends GitTestCase {

	/**
	 * Create operation with null repository
	 */
	@Test(expected = IllegalArgumentException.class)
	public void nullRepository() {
		new TreeCheckoutOperation(null, null);
	}

	/**
	 * Create operation with null commit
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void nullCommit() throws IOException {
		new TreeCheckoutOperation(git.repo(), null);
	}

	/**
	 * Test checking out a tree from previous commit
	 *
	 * @throws Exception
	 */
	@Test
	public void checkoutPreviousCommitTree() throws Exception {
		RevCommit commit = git.add("file.txt", "a");
		git.add("file.txt", "b");
		Repository repo = git.repo();
		File file = new File(repo.getWorkTree(), "file.txt");
		FileReader reader = new FileReader(file);
		assertEquals('b', (char) reader.read());
		reader.close();
		TreeCheckoutOperation op = new TreeCheckoutOperation(repo, commit);
		ObjectId tree = op.call();
		assertEquals(commit.getTree(), tree);
		reader = new FileReader(file);
		assertEquals('a', (char) reader.read());
		reader.close();
	}
}
