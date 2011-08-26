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

import static org.eclipse.jgit.lib.Constants.FETCH_HEAD;
import hudson.FilePath.FileCallable;
import hudson.remoting.VirtualChannel;
import hudson.scm.SCMRevisionState;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.CommitUtils;

/**
 * Operation that generates {@link SCMRevisionState} state for a collection of
 * {@link BuildRepository} instances
 *
 * @author Kevin Sawicki (kevin@github.com)
 */
public class RepositoryStateOperation implements FileCallable<SCMRevisionState> {

	/** serialVersionUID */
	private static final long serialVersionUID = 4327118712078069311L;

	private final Collection<BuildRepository> repos;

	/**
	 * Create repository state operation
	 *
	 * @param repos
	 */
	public RepositoryStateOperation(Collection<BuildRepository> repos) {
		if (repos == null)
			throw new IllegalArgumentException("Repos cannot be null");
		this.repos = repos;
	}

	public SCMRevisionState invoke(File file, VirtualChannel channel)
			throws IOException, InterruptedException {
		final BuildRepositoryState state = new BuildRepositoryState();
		for (BuildRepository repo : repos) {
			Repository gitRepo = new FileRepositoryOperation(repo).invoke(file,
					channel);
			if (gitRepo == null)
				continue;
			RevCommit commit = CommitUtils.getRef(gitRepo, FETCH_HEAD);
			if (commit == null)
				commit = CommitUtils.getLatest(gitRepo);
			state.put(repo, commit);
		}
		return !state.isEmpty() ? state : SCMRevisionState.NONE;
	}
}
