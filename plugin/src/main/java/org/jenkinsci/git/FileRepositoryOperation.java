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

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.util.FS;

/**
 * Operation that resolves a file-based {@link Repository} for a
 * {@link BuildRepository}
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class FileRepositoryOperation implements FileCallable<Repository> {

	/** serialVersionUID */
	private static final long serialVersionUID = -552221808510096678L;

	private final BuildRepository repo;

	/**
	 * Create file repository operation
	 * 
	 * @param repository
	 */
	public FileRepositoryOperation(BuildRepository repository) {
		if (repository == null)
			throw new IllegalArgumentException("Repository cannot be null");
		repo = repository;
	}

	public Repository invoke(File file, VirtualChannel channel)
			throws IOException {
		String directory = repo.getDirectory();
		File gitDir;
		if (directory == null || directory.length() == 0
				|| ".".equals(directory))
			gitDir = file;
		else
			gitDir = new File(file, directory);
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		builder.setFS(FS.DETECTED);
		builder.setGitDir(gitDir);
		builder.setMustExist(true);
		return builder.build();
	}
}
