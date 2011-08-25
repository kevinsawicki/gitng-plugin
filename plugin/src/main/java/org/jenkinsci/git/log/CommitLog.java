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
package org.jenkinsci.git.log;

import hudson.model.AbstractBuild;
import hudson.scm.ChangeLogSet;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Commit log class that provides iteration of {@link Commit} objects
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class CommitLog extends ChangeLogSet<Commit> {

	private final List<Commit> commits;

	/**
	 * @param build
	 */
	public CommitLog(AbstractBuild<?, ?> build) {
		this(build, null);
	}

	/**
	 * @param build
	 * @param commits
	 */
	public CommitLog(AbstractBuild<?, ?> build, List<Commit> commits) {
		super(build);
		if (commits == null)
			this.commits = Collections.emptyList();
		else
			this.commits = Collections.unmodifiableList(commits);
	}

	/**
	 * Set the parent on each commit to be this commit log
	 * 
	 * @return this commit log
	 */
	public CommitLog updateCommits() {
		for (Commit commit : this)
			commit.setParent(this);
		return this;
	}

	/**
	 * Get commit array
	 * 
	 * @return non-null but possibly empty array of commits
	 */
	public Commit[] toArray() {
		return commits.toArray(new Commit[commits.size()]);
	}

	public Iterator<Commit> iterator() {
		return commits.iterator();
	}

	public boolean isEmptySet() {
		return commits.isEmpty();
	}
}
