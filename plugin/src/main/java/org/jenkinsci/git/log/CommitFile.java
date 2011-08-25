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

import hudson.scm.ChangeLogSet.AffectedFile;
import hudson.scm.EditType;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.gitective.core.Check;

/**
 * Class that represents a file affected by a commit
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class CommitFile implements AffectedFile {

	@Expose
	private final ChangeType type;

	@Expose
	private final String oldPath;

	@Expose
	private final String newPath;

	/**
	 * Create commit file from diff entry
	 * 
	 * @param entry
	 */
	public CommitFile(DiffEntry entry) {
		type = entry.getChangeType();
		switch (type) {
		case ADD:
			oldPath = null;
			newPath = entry.getNewPath();
			break;
		case DELETE:
			oldPath = entry.getOldPath();
			newPath = null;
			break;
		case COPY:
		case MODIFY:
		case RENAME:
		default:
			oldPath = entry.getOldPath();
			newPath = entry.getNewPath();
			break;
		}
	}

	public String getPath() {
		return newPath != null ? newPath : oldPath;
	}

	public EditType getEditType() {
		switch (type) {
		case ADD:
			return EditType.ADD;
		case DELETE:
			return EditType.DELETE;
		case COPY:
			return new EditType("copy", "The file was copied");
		case RENAME:
			return new EditType("rename", "The file was renamed");
		case MODIFY:
		default:
			return EditType.EDIT;
		}
	}

	public int hashCode() {
		return toString().hashCode();
	}

	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof CommitFile))
			return false;
		CommitFile other = (CommitFile) obj;
		return type == other.type && Check.equals(oldPath, other.oldPath)
				&& Check.equals(newPath, other.newPath);
	}

	public String toString() {
		return getPath() + " [" + type + "]";
	}
}
