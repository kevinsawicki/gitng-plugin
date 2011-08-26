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

import hudson.scm.SCMRevisionState;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jgit.lib.ObjectId;
import org.gitective.core.Check;

/**
 * Repository state class that stores an {@link ObjectId} representing the state
 * of a {@link BuildRepository}
 *
 * @author Kevin Sawicki (kevin@github.com)
 */
public class BuildRepositoryState extends SCMRevisionState implements
		Iterable<Entry<BuildRepository, ObjectId>> {

	private Map<BuildRepository, ObjectId> states = new HashMap<BuildRepository, ObjectId>();

	/**
	 * Insert a mapping between a repository and an object id
	 *
	 * @param repo
	 * @param id
	 * @return this repository state
	 */
	public BuildRepositoryState put(BuildRepository repo, ObjectId id) {
		if (repo == null || id == null)
			return this;
		states.put(repo, id.copy());
		return this;
	}

	/**
	 * Get object id for repository
	 *
	 * @param repo
	 * @return object id
	 */
	public ObjectId get(BuildRepository repo) {
		if (repo == null)
			return null;
		return states.get(repo);
	}

	/**
	 * @return true if empty, false otherwise
	 */
	public boolean isEmpty() {
		return states.isEmpty();
	}

	public Iterator<Entry<BuildRepository, ObjectId>> iterator() {
		return states.entrySet().iterator();
	}

	public int hashCode() {
		return states.hashCode();
	}

	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof BuildRepositoryState))
			return false;
		return Check.equalsNonNull(states, ((BuildRepositoryState) obj).states);
	}

	public String toString() {
		return states.toString();
	}
}
