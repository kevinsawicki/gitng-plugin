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

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.Writer;

/**
 * Class that writes {@link Commit} objects to a {@link Writer}
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class CommitLogWriter {

	private final Gson gson;

	private final JsonWriter jsonWriter;

	private boolean first = true;

	/**
	 * Create commit log writer
	 * 
	 * @param writer
	 *            must be non-null
	 */
	public CommitLogWriter(Writer writer) {
		if (writer == null)
			throw new IllegalArgumentException("Writer cannot be null");
		gson = GsonUtils.getGson();
		jsonWriter = new JsonWriter(writer);
	}

	/**
	 * Write commit. Null commits will be ignored.
	 * 
	 * @param commit
	 * @return this commit log writer
	 * @throws IOException
	 */
	public CommitLogWriter write(Commit commit) throws IOException {
		if (commit == null)
			return this;
		if (first) {
			jsonWriter.beginArray();
			first = false;
		}
		try {
			gson.toJson(commit, Commit.class, jsonWriter);
		} catch (JsonIOException e) {
			throw new IOException(e);
		}
		return this;
	}

	/**
	 * Close writer
	 * 
	 * @return this commit log writer
	 * @throws IOException
	 */
	public CommitLogWriter close() throws IOException {
		if (first)
			jsonWriter.beginArray();
		jsonWriter.endArray();
		jsonWriter.close();
		return this;
	}
}
