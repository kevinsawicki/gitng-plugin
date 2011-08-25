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

import hudson.scm.EditType;

import java.io.IOException;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepository;
import org.jenkinsci.git.log.Commit;
import org.jenkinsci.git.log.CommitFile;
import org.junit.Test;

/**
 * Unit tests of classes used to build a changelog
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class CommitLogTest extends org.jenkinsci.git.GitTestCase {

	/**
	 * Create commit with null {@link Repository}
	 */
	@Test(expected = IllegalArgumentException.class)
	public void commitWithNullRepository() {
		new Commit(null, null);
	}

	/**
	 * Create commit with null {@link RevCommit}
	 * 
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void commitWithNullCommit() throws IOException {
		new Commit(new FileRepository(testRepo), null);
	}

	/**
	 * Test commit that contains a single added file
	 * 
	 * @throws Exception
	 */
	@Test
	public void commitWithAdd() throws Exception {
		RevCommit commit = add("file.txt", "content");
		Commit parsed = new Commit(new FileRepository(testRepo), commit);
		assertEquals(commit.getFullMessage(), parsed.getMsg());
		assertEquals(commit.name(), parsed.getCommitId());
		assertEquals(commit.getAuthorIdent().getWhen().getTime(),
				parsed.getTimestamp());

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
	@Test
	public void commitWithEdit() throws Exception {
		add("file.txt", "content");
		RevCommit commit = add("file.txt", "content2");
		Commit parsed = new Commit(new FileRepository(testRepo), commit);
		assertEquals(commit.getFullMessage(), parsed.getMsg());
		assertEquals(commit.name(), parsed.getCommitId());
		assertEquals(commit.getAuthorIdent().getWhen().getTime(),
				parsed.getTimestamp());

		assertNotNull(parsed.getAffectedPaths());
		assertEquals(1, parsed.getAffectedPaths().size());
		assertEquals("file.txt", parsed.getAffectedPaths().iterator().next());

		assertNotNull(parsed.getAffectedFiles());
		assertEquals(1, parsed.getAffectedFiles().size());
		CommitFile file = parsed.getAffectedFiles().iterator().next();
		assertEquals("file.txt", file.getPath());
		assertEquals(EditType.EDIT, file.getEditType());
	}
}
