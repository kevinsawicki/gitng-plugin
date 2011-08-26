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

import hudson.FilePath.FileCallable;
import hudson.remoting.VirtualChannel;
import hudson.scm.PollingResult;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;

/**
 * Operation that generates a {@link PollingResult} for a collection of
 * {@link BuildRepository} instances.
 *
 * @author Kevin Sawicki (kevin@github.com)
 */
public class PollOperation implements FileCallable<PollingResult> {

	/** serialVersionUID */
	private static final long serialVersionUID = 7141515497249417849L;

	private final BuildRepositoryState state;

	private final List<BuildRepository> repos;

	/**
	 * Create poll operation using given baseline
	 *
	 * @param baseline
	 * @param repos
	 */
	public PollOperation(BuildRepositoryState baseline,
			List<BuildRepository> repos) {
		if (baseline == null)
			throw new IllegalArgumentException("Baseline cannot be null");
		if (repos == null)
			throw new IllegalArgumentException("Repos cannot be null");
		this.state = baseline;
		this.repos = repos;
	}

	public PollingResult invoke(File file, VirtualChannel channel)
			throws IOException, InterruptedException {
		for (BuildRepository repo : repos) {
			Repository gitRepo = new FileRepositoryOperation(repo).invoke(file,
					channel);
			if (gitRepo == null)
				return PollingResult.BUILD_NOW;
			LsRemoteOperation op = new LsRemoteOperation(repo, gitRepo);
			ObjectId latest = op.invoke(file, channel);
			if (latest == null)
				return PollingResult.BUILD_NOW;
			ObjectId current = state.get(repo);
			if (current == null)
				return PollingResult.BUILD_NOW;
			if (!latest.equals(current))
				return PollingResult.SIGNIFICANT;
		}
		return PollingResult.NO_CHANGES;
	}
}