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
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.scm.ChangeLogParser;
import hudson.scm.PollingResult;
import hudson.scm.SCMRevisionState;
import hudson.scm.SCM;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.jenkinsci.git.log.CommitLogReader;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.export.Exported;

/**
 * Repository build manager
 *
 * @author Kevin Sawicki (kevin@github.com)
 */
public class RepositoryManager extends SCM {

	private final List<BuildRepository> repos;

	/**
	 * Create repository manager with given build repositories
	 *
	 * @param repositories
	 */
	@DataBoundConstructor
	public RepositoryManager(List<BuildRepository> repositories) {
		if (repositories != null)
			repos = Collections.unmodifiableList(repositories);
		else
			repos = Collections.emptyList();
	}

	public SCMRevisionState calcRevisionsFromBuild(AbstractBuild<?, ?> build,
			Launcher launcher, TaskListener listener) throws IOException,
			InterruptedException {
		FilePath workspace = build.getWorkspace();
		if (workspace == null || !workspace.exists())
			return SCMRevisionState.NONE;

		return workspace.act(new RepositoryStateOperation(repos));
	}

	protected PollingResult compareRemoteRevisionWith(
			AbstractProject<?, ?> project, Launcher launcher,
			FilePath workspace, TaskListener listener, SCMRevisionState baseline)
			throws IOException, InterruptedException {
		if (baseline == null || baseline == SCMRevisionState.NONE)
			return PollingResult.BUILD_NOW;

		BuildRepositoryState state = (BuildRepositoryState) baseline;
		return workspace.act(new PollOperation(state, repos));
	}

	public boolean checkout(AbstractBuild<?, ?> build, Launcher launcher,
			FilePath workspace, BuildListener listener, File changelogFile)
			throws IOException, InterruptedException {
		RepositoryCheckoutOperation operation = new RepositoryCheckoutOperation(
				repos, new FilePath(changelogFile), listener);
		return workspace.act(operation);
	}

	/**
	 * Get build repositories
	 *
	 * @return non-null, non-modifiable but possibly empty list of repositories
	 *         to build
	 */
	@Exported
	public List<BuildRepository> getRepositories() {
		return repos;
	}

	public ChangeLogParser createChangeLogParser() {
		return new CommitLogReader();
	}

	public boolean requiresWorkspaceForPolling() {
		return true;
	}
}
