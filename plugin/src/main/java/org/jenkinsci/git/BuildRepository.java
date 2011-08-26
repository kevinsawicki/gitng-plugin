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

import static org.eclipse.jgit.lib.Constants.MASTER;
import static org.eclipse.jgit.lib.Constants.R_HEADS;

import java.io.Serializable;

import org.gitective.core.Check;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.export.Exported;

/**
 * Repository used in a build
 *
 * @author Kevin Sawicki (kevin@github.com)
 */
public class BuildRepository implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 6313364458960227507L;

	/**
	 * Default master branch ref
	 */
	public static final String BRANCH_DEFAULT = R_HEADS + MASTER;

	private transient int hashCode = -1;

	private String branch;

	private String directory;

	private String uri;

	/**
	 * Create build repository
	 *
	 * @param uri
	 * @param branch
	 * @param directory
	 */
	@DataBoundConstructor
	public BuildRepository(String uri, String branch, String directory) {
		this.uri = uri;
		this.branch = branch;
		this.directory = directory;
	}

	/**
	 * @return branch
	 */
	@Exported
	public String getBranch() {
		return branch;
	}

	/**
	 * @return directory
	 */
	@Exported
	public String getDirectory() {
		return directory;
	}

	/**
	 * @return uri
	 */
	@Exported
	public String getUri() {
		return uri;
	}

	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (!(other instanceof BuildRepository))
			return false;
		BuildRepository otherRepo = (BuildRepository) other;
		return Check.equals(uri, otherRepo.uri)
				&& Check.equals(branch, otherRepo.branch)
				&& Check.equals(directory, otherRepo.directory);
	}

	public int hashCode() {
		if (hashCode == -1)
			hashCode = toString().hashCode();
		return hashCode;
	}

	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(uri);
		buffer.append(' ');
		buffer.append('[');
		buffer.append(branch);
		buffer.append(']');
		buffer.append(' ');
		buffer.append('-');
		buffer.append(' ');
		buffer.append(directory);
		return buffer.toString();
	}
}
