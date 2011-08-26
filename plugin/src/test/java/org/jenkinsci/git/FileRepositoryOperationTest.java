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

import java.io.IOException;

import org.eclipse.jgit.lib.Repository;
import org.junit.Test;

/**
 * Unit tests of {@link FileRepositoryOperation}
 *
 * @author Kevin Sawicki (kevin@github.com)
 */
public class FileRepositoryOperationTest extends GitTestCase {

	/**
	 * Create a operation with a null build repository
	 */
	@Test(expected = IllegalArgumentException.class)
	public void nullRepository() {
		new FileRepositoryOperation(null);
	}

	/**
	 * Resolve a non-existent repository
	 *
	 * @throws IOException
	 */
	public void resolveNonExistent() throws IOException {
		BuildRepository buildRepo = new BuildRepository("a", "b", null);
		FileRepositoryOperation op = new FileRepositoryOperation(buildRepo);
		assertNull(op.invoke(git.tempDirectory(), null));
	}

	/**
	 * Resolve a repository in a root directory
	 *
	 * @throws Exception
	 */
	@Test
	public void resolveExisting() throws Exception {
		BuildRepository buildRepo = new BuildRepository("a", "b", null);
		FileRepositoryOperation op = new FileRepositoryOperation(buildRepo);
		Repository repo = op.invoke(git.repo().getDirectory(), null);
		assertNotNull(repo);
		assertEquals(git.repo().getDirectory(), repo.getDirectory());
	}
}
