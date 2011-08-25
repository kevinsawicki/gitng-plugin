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

import hudson.model.User;
import hudson.scm.ChangeLogSet;
import hudson.scm.ChangeLogSet.Entry;
import hudson.scm.EditType;
import hudson.tasks.Mailer;

import java.io.File;
import java.io.FileWriter;

import org.eclipse.jgit.revwalk.RevCommit;
import org.jenkinsci.git.log.Commit;
import org.jenkinsci.git.log.CommitFile;
import org.jenkinsci.git.log.CommitLog;
import org.jenkinsci.git.log.CommitLogReader;
import org.jenkinsci.git.log.CommitLogWriter;

/**
 * Unit tests of classes used to build a changelog
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class CommitLogTest extends JenkinsGitTestCase {

	/**
	 * Test commit that contains a single added file
	 * 
	 * @throws Exception
	 */
	public void testCommitWithAdd() throws Exception {
		RevCommit commit = git.add("file.txt", "content");
		Commit parsed = new Commit(git.repo(), commit);
		assertEquals(commit.getFullMessage(), parsed.getMsg());
		assertEquals(commit.name(), parsed.getCommitId());
		assertEquals(commit.getAuthorIdent().getWhen().getTime(),
				parsed.getTimestamp());
		User user = parsed.getAuthor();
		assertNotNull(user);
		assertEquals(commit.getAuthorIdent().getName(), user.getFullName());
		assertEquals(commit.getAuthorIdent().getEmailAddress(), user
				.getProperty(Mailer.UserProperty.class).getAddress());

		assertNotNull(parsed.getAffectedPaths());
		assertEquals(1, parsed.getAffectedPaths().size());
		assertEquals("file.txt", parsed.getAffectedPaths().iterator().next());

		assertNotNull(parsed.getAffectedFiles());
		assertEquals(1, parsed.getAffectedFiles().size());
		CommitFile file = parsed.getAffectedFiles().iterator().next();
		assertEquals("file.txt", file.getPath());
		assertEquals(EditType.ADD, file.getEditType());
	}

	/**
	 * Test commit that contains a single edited file
	 * 
	 * @throws Exception
	 */
	public void testCommitWithEdit() throws Exception {
		git.add("file.txt", "content");
		RevCommit commit = git.add("file.txt", "content2");
		Commit parsed = new Commit(git.repo(), commit);
		assertEquals(commit.getFullMessage(), parsed.getMsg());
		assertEquals(commit.name(), parsed.getCommitId());
		assertEquals(commit.getAuthorIdent().getWhen().getTime(),
				parsed.getTimestamp());
		User user = parsed.getAuthor();
		assertNotNull(user);
		assertEquals(commit.getAuthorIdent().getName(), user.getFullName());
		assertEquals(commit.getAuthorIdent().getEmailAddress(), user
				.getProperty(Mailer.UserProperty.class).getAddress());

		assertNotNull(parsed.getAffectedPaths());
		assertEquals(1, parsed.getAffectedPaths().size());
		assertEquals("file.txt", parsed.getAffectedPaths().iterator().next());

		assertNotNull(parsed.getAffectedFiles());
		assertEquals(1, parsed.getAffectedFiles().size());
		CommitFile file = parsed.getAffectedFiles().iterator().next();
		assertEquals("file.txt", file.getPath());
		assertEquals(EditType.EDIT, file.getEditType());
	}

	/**
	 * Test writing and reading back a single commit
	 * 
	 * @throws Exception
	 */
	public void testReadWriteSingleCommit() throws Exception {
		RevCommit revCommit = git.add("file.txt", "content");
		Commit commit = new Commit(git.repo(), revCommit);
		File log = File.createTempFile("changelog", ".json");
		CommitLogWriter writer = new CommitLogWriter(new FileWriter(log));
		writer.write(commit).close();
		CommitLogReader reader = new CommitLogReader();
		ChangeLogSet<? extends Entry> set = reader.parse(null, log);
		assertNotNull(set);
		assertFalse(set.isEmptySet());
		assertTrue(set instanceof CommitLog);
		CommitLog commitLog = (CommitLog) set;
		Commit[] commits = commitLog.toArray();
		assertNotNull(commits);
		assertEquals(1, commits.length);
		Commit parsed = commits[0];
		assertNotNull(parsed);
		assertTrue(commit.equals(parsed));
		assertEquals(commitLog, parsed.getParent());
		assertEquals(commit.getMsg(), parsed.getMsg());
		assertEquals(commit.getCommitId(), parsed.getCommitId());
		assertEquals(commit.getTimestamp(), parsed.getTimestamp());
		assertEquals(commit.getAffectedFiles(), parsed.getAffectedFiles());
	}
}
