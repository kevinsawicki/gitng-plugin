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
import java.util.Collection;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

/**
 * Operation that gets the latest commit remotely
 *
 * @author Kevin Sawicki (kevin@github.com)
 */
public class LsRemoteOperation implements FileCallable<ObjectId> {

	/** serialVersionUID */
	private static final long serialVersionUID = -3623182589852806015L;

	private final BuildRepository repo;

	private final Repository gitRepo;

	/**
	 * Create ls-remote operation
	 *
	 * @param repo
	 *            must be non-null
	 * @param gitRepo
	 *            must be non-null
	 */
	public LsRemoteOperation(BuildRepository repo, Repository gitRepo) {
		if (repo == null)
			throw new IllegalArgumentException("Repo cannot be null");
		if (gitRepo == null)
			throw new IllegalArgumentException("Git repo cannot be null");
		this.repo = repo;
		this.gitRepo = gitRepo;
	}

	public ObjectId invoke(File file, VirtualChannel channel)
			throws IOException {
		LsRemoteCommand ls = Git.wrap(gitRepo).lsRemote();
		ls.setRemote(repo.getUri());
		String branch = repo.getBranch();
		Collection<Ref> refs;
		try {
			refs = ls.call();
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException(e);
		}
		for (Ref ref : refs)
			if (branch.equals(ref.getName()))
				return ref.getObjectId();
		return null;
	}
}
