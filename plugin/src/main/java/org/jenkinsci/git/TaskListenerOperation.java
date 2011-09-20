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

import hudson.model.TaskListener;

/**
 * Base operation that logs to a configured {@link TaskListener} instance
 *
 * @author Kevin Sawicki (kevin@github.com)
 */
public abstract class TaskListenerOperation {

	/**
	 * Listener, possibly null
	 */
	protected final TaskListener listener;

	/**
	 * Create operation with no task listener
	 */
	public TaskListenerOperation() {
		this(null);
	}

	/**
	 * Create operation that logs to given task listener
	 *
	 * @param listener
	 */
	public TaskListenerOperation(TaskListener listener) {
		this.listener = listener;
	}
}
