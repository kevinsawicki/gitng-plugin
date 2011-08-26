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

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.RefSpec;
import org.gitective.core.CommitUtils;
import org.gitective.core.GitException;

/**
 * Operation that fetches from a {@link BuildRepository} configuration
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class FetchOperation implements FileCallable<RevCommit> {

	/** serialVersionUID */
	private static final long serialVersionUID = 3956046067095683554L;

	private final BuildRepository repo;

	private final Repository gitRepo;

	private final StreamProgressMonitor monitor;

	/**
	 * Create fetch operation
	 * 
	 * @param repo
	 * @param gitRepo
	 */
	public FetchOperation(BuildRepository repo, Repository gitRepo) {
		this(repo, gitRepo, null);
	}

	/**
	 * Create fetch operation
	 * 
	 * @param repo
	 * @param gitRepo
	 * @param monitor
	 */
	public FetchOperation(BuildRepository repo, Repository gitRepo,
			StreamProgressMonitor monitor) {
		if (repo == null)
			throw new IllegalArgumentException("Repo cannot be null");
		if (gitRepo == null)
			throw new IllegalArgumentException("Git repo cannot be null");
		this.repo = repo;
		this.gitRepo = gitRepo;
		this.monitor = monitor;
	}

	public RevCommit invoke(File f, VirtualChannel channel) throws IOException {
		FetchCommand fetch = Git.wrap(gitRepo).fetch();
		fetch.setRemote(repo.getUri());
		fetch.setRefSpecs(new RefSpec(repo.getBranch()));
		if (monitor != null)
			fetch.setProgressMonitor(monitor);
		try {
			fetch.call();
			return CommitUtils.getRef(gitRepo, Constants.FETCH_HEAD);
		} catch (GitException e) {
			throw new IOException(e);
		} catch (JGitInternalException e) {
			throw new IOException(e);
		} catch (InvalidRemoteException e) {
			throw new IOException(e);
		}
	}
}
