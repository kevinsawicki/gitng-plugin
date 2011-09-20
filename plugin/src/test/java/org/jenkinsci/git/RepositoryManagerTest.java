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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;

/**
 * Unit tests of {@link RepositoryManager}
 *
 * @author Kevin Sawicki (kevin@github.com)
 */
public class RepositoryManagerTest {

	/**
	 * Create manager with null list
	 */
	@Test
	public void defaultRepos() {
		RepositoryManager manager = new RepositoryManager(null);
		assertNotNull(manager.getRepositories());
		assertTrue(manager.getRepositories().isEmpty());
	}

	/**
	 * Create manager with single repository
	 */
	@Test
	public void singleRepo() {
		BuildRepository repo = new BuildRepository("git://server/project.git",
				"master", ".");
		RepositoryManager manager = new RepositoryManager(
				Collections.singletonList(repo));
		assertNotNull(manager.getRepositories());
		assertFalse(manager.getRepositories().isEmpty());
		assertEquals(repo, manager.getRepositories().get(0));
	}
}
