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

import java.io.IOException;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.gitective.core.filter.commit.CommitFilter;

/**
 * Filter that writes all commits visited to an underlying
 * {@link CommitLogWriter}
 *
 * @author Kevin Sawicki (kevin@github.com)
 */
public class CommitLogWriterFilter extends CommitFilter {

	private final CommitLogWriter writer;

	/**
	 * Create commit log writer filter
	 *
	 * @param writer
	 */
	public CommitLogWriterFilter(CommitLogWriter writer) {
		if (writer == null)
			throw new IllegalArgumentException("Writer cannot be null");
		this.writer = writer;
	}

	public boolean include(RevWalk walker, RevCommit commit) throws IOException {
		writer.write(new Commit(repository, commit));
		return true;
	}

	public RevFilter clone() {
		return new CommitLogWriterFilter(writer);
	}
}
