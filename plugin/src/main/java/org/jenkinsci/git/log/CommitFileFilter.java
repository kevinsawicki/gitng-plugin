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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.filter.commit.CommitDiffFilter;
import org.gitective.core.filter.commit.CommitFilter;
import org.gitective.core.service.CommitFinder;

/**
 * Filter that builds a list of {@link CommitFile} from {@link DiffEntry}
 * objects
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class CommitFileFilter extends CommitDiffFilter {

	/**
	 * Get files that were affected by the given commit
	 * 
	 * @param repository
	 * @param commit
	 * @return non-null but possibly empty list of affected file
	 */
	public static List<CommitFile> getFiles(Repository repository,
			RevCommit commit) {
		CommitFileFilter filter = new CommitFileFilter();
		filter.setStop(true);
		new CommitFinder(repository).setFilter(filter).findFrom(commit);
		return filter.getFiles();
	}

	private final List<CommitFile> files = new ArrayList<CommitFile>();

	/**
	 * Get affected file
	 * 
	 * @return non-null but possibly empty array of files
	 */
	public List<CommitFile> getFiles() {
		return files;
	}

	public boolean include(RevCommit commit, Collection<DiffEntry> diffs) {
		for (DiffEntry diff : diffs)
			files.add(new CommitFile(diff));
		return false;
	}

	public CommitFilter reset() {
		files.clear();
		return super.reset();
	}
}
