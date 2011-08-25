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
import org.eclipse.jgit.revwalk.RevCommit;
import org.jenkinsci.git.log.Commit;
import org.junit.Test;

/**
 * Unit tests of {@link Commit} class
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class CommitTest extends GitTestCase {

	/**
	 * Create commit with null {@link Repository}
	 */
	@Test(expected = IllegalArgumentException.class)
	public void commitWithNullRepository() {
		new Commit(null, null);
	}

	/**
	 * Create commit with null {@link RevCommit}
	 * 
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void commitWithNullCommit() throws IOException {
		new Commit(git.repo(), null);
	}
}
