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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepository;

/**
 * Git repository helper for use by test cases
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class GitHelper extends Assert {

	/**
	 * Test repository .git directory
	 */
	public File testRepo;

	/**
	 * Author used for commits
	 */
	public PersonIdent author = new PersonIdent("author", "author@test.com");

	/**
	 * Committer used for commits
	 */
	public PersonIdent committer = new PersonIdent("committer",
			"committer@test.com");

	/**
	 * Create Git helper
	 */
	public GitHelper() {
		testRepo = initRepo();
	}

	/**
	 * Create and verify temp directory that will be deleted on exit
	 * 
	 * @return created directory
	 */
	public File tempDirectory() {
		String tmpDir = System.getProperty("java.io.tmpdir");
		assertNotNull("java.io.tmpdir was null", tmpDir);
		File dir = new File(tmpDir, "git-test-case-" + System.nanoTime());
		assertTrue(dir.mkdir());
		dir.deleteOnExit();
		return dir;
	}

	/**
	 * Initialize a new repo in a new directory
	 * 
	 * @return created .git folder
	 */
	public File initRepo() {
		File dir = tempDirectory();

		Git.init().setDirectory(dir).setBare(false).call();
		File repo = new File(dir, Constants.DOT_GIT);
		assertTrue(repo.exists());
		return repo;
	}

	/**
	 * Add file to test repository
	 * 
	 * @param path
	 * @param content
	 * @return commit
	 * @throws Exception
	 */
	public RevCommit add(String path, String content) throws Exception {
		return add(testRepo, path, content);
	}

	/**
	 * Add file to test repository
	 * 
	 * @param repo
	 * 
	 * @param path
	 * @param content
	 * @return commit
	 * @throws Exception
	 */
	public RevCommit add(File repo, String path, String content)
			throws Exception {
		String message = MessageFormat.format("Committing {0} at {1}", path,
				new Date());
		return add(repo, path, content, message);
	}

	/**
	 * Add file to test repository
	 * 
	 * @param path
	 * @param content
	 * @param message
	 * @return commit
	 * @throws Exception
	 */
	public RevCommit add(String path, String content, String message)
			throws Exception {
		return add(testRepo, path, content, message);
	}

	/**
	 * Add file to test repository
	 * 
	 * @param repo
	 * @param path
	 * @param content
	 * @param message
	 * @return commit
	 * @throws Exception
	 */
	public RevCommit add(File repo, String path, String content, String message)
			throws Exception {
		return add(repo, Collections.singletonList(path),
				Collections.singletonList(content), message);
	}

	/**
	 * Create repository
	 * 
	 * @return repository
	 * @throws IOException
	 */
	public Repository repo() throws IOException {
		return repo(testRepo);
	}

	/**
	 * Create repository for file
	 * 
	 * @param file
	 * @return repository
	 * @throws IOException
	 */
	public Repository repo(File file) throws IOException {
		return new FileRepository(file);
	}

	/**
	 * Add files to test repository
	 * 
	 * @param repo
	 * @param paths
	 * @param contents
	 * @param message
	 * @return commit
	 * @throws Exception
	 */
	public RevCommit add(File repo, List<String> paths, List<String> contents,
			String message) throws Exception {
		Git git = Git.open(repo);
		for (int i = 0; i < paths.size(); i++) {
			String path = paths.get(i);
			String content = contents.get(i);
			File file = new File(repo.getParentFile(), path);
			if (!file.getParentFile().exists())
				assertTrue(file.getParentFile().mkdirs());
			if (!file.exists())
				assertTrue(file.createNewFile());
			PrintWriter writer = new PrintWriter(file);
			if (content == null)
				content = "";
			try {
				writer.print(content);
			} finally {
				writer.close();
			}
			git.add().addFilepattern(path).call();
		}
		RevCommit commit = git.commit().setMessage(message).setAuthor(author)
				.setCommitter(committer).call();
		assertNotNull(commit);
		return commit;
	}
}
