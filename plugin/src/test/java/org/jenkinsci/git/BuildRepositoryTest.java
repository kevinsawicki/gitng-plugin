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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit tests of {@link BuildRepository}
 */
public class BuildRepositoryTest {

	/**
	 * Test getters for values passed into constructor
	 */
	@Test
	public void getters() {
		BuildRepository repo = new BuildRepository("git://host/repo.git",
				"master", ".");
		assertEquals("git://host/repo.git", repo.getUri());
		assertEquals("master", repo.getBranch());
		assertEquals(".", repo.getDirectory());
	}

	/**
	 * Test different {@link BuildRepository} instances being equal
	 */
	@Test
	public void equalsRepository() {
		BuildRepository repo1 = new BuildRepository("git://host/repo.git",
				"master", ".");
		assertTrue(repo1.equals(repo1));
		assertFalse(repo1.equals(null));
		assertFalse(repo1.equals("string"));
		assertEquals(repo1.hashCode(), repo1.hashCode());
		assertEquals(repo1.toString(), repo1.toString());

		BuildRepository repo2 = new BuildRepository(null, null, null);
		assertFalse(repo1.equals(repo2));
		repo2 = new BuildRepository("git://host/repo.git", null, null);
		assertFalse(repo1.equals(repo2));
		repo2 = new BuildRepository("git://host/repo.git", "master", null);
		assertFalse(repo1.equals(repo2));
		repo2 = new BuildRepository("git://host/repo.git", "master", ".");
		assertTrue(repo1.equals(repo2));
		assertEquals(repo1.hashCode(), repo2.hashCode());
		assertEquals(repo1.toString(), repo2.toString());
	}
}
