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

import hudson.FilePath;
import hudson.FilePath.FileCallable;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.RefUpdate.Result;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.CommitUtils;
import org.gitective.core.service.CommitFinder;
import org.jenkinsci.git.log.Commit;
import org.jenkinsci.git.log.CommitLogWriter;
import org.jenkinsci.git.log.CommitLogWriterFilter;

/**
 * Operation that checks out a collection of {@link BuildRepository} instances.
 *
 * @author Kevin Sawicki (kevin@github.com)
 */
public class RepositoryCheckoutOperation implements FileCallable<Boolean> {

	/** serialVersionUID */
	private static final long serialVersionUID = 8944211954428830644L;

	private final Collection<BuildRepository> repos;

	private final FilePath log;

	/**
	 * Create repository checkout operation
	 *
	 * @param repositories
	 * @param commitLog
	 */
	public RepositoryCheckoutOperation(
			Collection<BuildRepository> repositories, FilePath commitLog) {
		if (repositories == null)
			throw new IllegalArgumentException("Repositories cannot be null");
		if (commitLog == null)
			throw new IllegalArgumentException("Commit log cannot be null");
		repos = repositories;
		log = commitLog;
	}

	public Boolean invoke(File file, VirtualChannel channel)
			throws IOException, InterruptedException {
		CommitLogWriter writer = new CommitLogWriter(new OutputStreamWriter(
				log.write()));
		CommitLogWriterFilter filter = new CommitLogWriterFilter(writer);
		try {
			for (BuildRepository repo : repos) {
				Repository gitRepo = new FileRepositoryOperation(repo).invoke(
						file, channel);
				RevCommit current = null;
				if (gitRepo == null)
					gitRepo = new InitOperation(repo).invoke(file, channel);
				else
					current = CommitUtils.getLatest(gitRepo);

				RevCommit fetched = new FetchOperation(repo, gitRepo).call();
				if (fetched == null)
					return false;

				if (current != null)
					new CommitFinder(gitRepo).setFilter(filter).findBetween(
							fetched, current);
				else
					writer.write(new Commit(gitRepo, fetched));

				new TreeCheckoutOperation(gitRepo, fetched).call();
				RefUpdate refUpdate = gitRepo.updateRef(Constants.HEAD, true);
				refUpdate.setNewObjectId(fetched);
				Result result = refUpdate.forceUpdate();
				if (result == null)
					throw new IOException("Null ref update result");
				switch (result) {
				case NEW:
				case FORCED:
				case FAST_FORWARD:
				case NO_CHANGE:
					// These are the acceptable results
					break;
				default:
					throw new IOException(result.name());
				}
			}
		} finally {
			writer.close();
		}
		return true;
	}
}
