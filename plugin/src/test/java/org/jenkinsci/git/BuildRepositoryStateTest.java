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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.jgit.lib.ObjectId;
import org.junit.Test;

/**
 * Unit tests of {@link BuildRepositoryState}
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class BuildRepositoryStateTest {

	/**
	 * Test handling of null parameters
	 */
	@Test
	public void nullParameters() {
		BuildRepositoryState state = new BuildRepositoryState();
		assertNull(state.get(null));
		assertSame(state, state.put(null, null));
	}

	/**
	 * Test accessing an object id
	 */
	@Test
	public void accessId() {
		BuildRepositoryState state = new BuildRepositoryState();
		BuildRepository repo = new BuildRepository("a", "b", "c");
		assertNull(state.get(repo));
		state.put(repo, ObjectId.zeroId());
		assertEquals(ObjectId.zeroId(), state.get(repo));
	}

	/**
	 * Test equals of {@link BuildRepositoryState}
	 */
	@Test
	public void equalsState() {
		BuildRepositoryState state1 = new BuildRepositoryState();
		assertTrue(state1.equals(state1));
		assertFalse(state1.equals(null));
		assertFalse(state1.equals("test"));
		assertEquals(state1.hashCode(), state1.hashCode());
		BuildRepository repo = new BuildRepository("a", "b", "c");
		state1.put(repo, ObjectId.zeroId());

		BuildRepositoryState state2 = new BuildRepositoryState();
		assertFalse(state1.equals(state2));
		state2.put(repo, ObjectId.zeroId());
		assertTrue(state1.equals(state2));
		assertEquals(state1.hashCode(), state2.hashCode());
		assertEquals(state1.toString(), state2.toString());
	}
}
