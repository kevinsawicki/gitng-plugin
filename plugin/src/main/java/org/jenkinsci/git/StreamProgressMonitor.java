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

import java.io.PrintStream;

import org.eclipse.jgit.lib.ProgressMonitor;

/**
 * Progress monitor that writes task information to a configured
 * {@link PrintStream}
 */
public class StreamProgressMonitor implements ProgressMonitor {

	/**
	 * Done message
	 */
	public static final String DONE = "[DONE]";

	private final PrintStream stream;

	private boolean done = true;

	/**
	 * Create a progress monitor that uses the given non-null stream
	 * 
	 * @param stream
	 *            must be non-null
	 */
	public StreamProgressMonitor(PrintStream stream) {
		if (stream == null)
			throw new IllegalArgumentException("Stream cannot be null");
		this.stream = stream;
	}

	public void start(int totalTasks) {
		// Ignored
	}

	public void beginTask(String title, int totalWork) {
		if (!done)
			endTask();
		stream.print(title);
		if (totalWork != UNKNOWN) {
			stream.print(": ");
			stream.print(totalWork);
		}
		stream.print(' ');
		done = false;
	}

	public void update(int completed) {
		// Ignored
	}

	public void endTask() {
		stream.println(DONE);
	}

	public boolean isCancelled() {
		return false;
	}
}
