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

import com.google.gson.annotations.Expose;

import hudson.model.User;
import hudson.scm.ChangeLogSet.Entry;
import hudson.tasks.Mailer.UserProperty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.Check;

/**
 * Class that represents a single commit in a Git repository.
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class Commit extends Entry {

	@Expose
	private final Date date;

	@Expose
	private final String email;

	@Expose
	private final String id;

	@Expose
	private final String message;

	@Expose
	private final String name;

	@Expose
	private final List<CommitFile> files;

	/**
	 * Create commit from repository and {@link RevCommit} instance
	 * 
	 * @param repository
	 * @param commit
	 */
	public Commit(Repository repository, RevCommit commit) {
		if (repository == null)
			throw new IllegalArgumentException("Repository cannot be null");
		if (commit == null)
			throw new IllegalArgumentException("Commit cannot be null");
		PersonIdent person = commit.getAuthorIdent();
		if (person == null)
			person = commit.getCommitterIdent();
		name = person.getName();
		email = person.getEmailAddress();
		date = person.getWhen();
		message = commit.getFullMessage();
		id = commit.name();
		files = CommitFileFilter.getFiles(repository, commit);
	}

	/**
	 * Set parent commit log
	 * 
	 * @param log
	 * @return this commit
	 */
	public Commit setParent(CommitLog log) {
		super.setParent(log);
		return this;
	}

	public String getCommitId() {
		return id;
	}

	public long getTimestamp() {
		return date != null ? date.getTime() : super.getTimestamp();
	}

	public Collection<CommitFile> getAffectedFiles() {
		return files;
	}

	public String getMsg() {
		return message;
	}

	public User getAuthor() {
		User user = User.get(name, true);
		try {
			user.addProperty(new UserProperty(email));
		} catch (IOException ignored) {
			// Ignored
		}
		return user;
	}

	public Collection<String> getAffectedPaths() {
		final List<String> paths = new ArrayList<String>(files.size());
		for (CommitFile file : files)
			paths.add(file.getPath());
		return paths;
	}

	public int hashCode() {
		return id.hashCode();
	}

	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof Commit))
			return false;
		return Check.equalsNonNull(name, ((Commit) obj).name);
	}

	public String toString() {
		return id + " by " + name + " <" + email + ">";
	}
}
