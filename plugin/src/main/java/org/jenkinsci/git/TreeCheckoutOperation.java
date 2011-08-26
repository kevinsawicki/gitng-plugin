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

import hudson.remoting.Callable;

import java.io.IOException;

import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheCheckout;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;

/**
 * Operation that checks out a specific commit
 *
 * @author Kevin Sawicki (kevin@github.com)
 */
public class TreeCheckoutOperation implements Callable<ObjectId, IOException> {

	/** serialVersionUID */
	private static final long serialVersionUID = 1247715173832080149L;

	private final Repository repo;

	private final RevCommit commit;

	/**
	 * Create tree checkout operation
	 *
	 * @param repo
	 *            must be non-null
	 * @param commit
	 *            must be non-null
	 */
	public TreeCheckoutOperation(Repository repo, RevCommit commit) {
		if (repo == null)
			throw new IllegalArgumentException("Repo cannot be null");
		if (commit == null)
			throw new IllegalArgumentException("Commit cannot be null");
		this.repo = repo;
		this.commit = commit;
	}

	public ObjectId call() throws IOException {
		RevTree tree = commit.getTree();
		DirCache dirCache = repo.lockDirCache();
		DirCacheCheckout co = new DirCacheCheckout(repo, dirCache, tree);
		co.setFailOnConflict(false);
		if (!co.checkout())
			throw new IOException("Checkout failed");
		return tree;
	}
}
