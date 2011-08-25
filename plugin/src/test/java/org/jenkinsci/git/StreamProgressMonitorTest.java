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

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

/**
 * Unit tests of {@link StreamProgressMonitor}
 */
public class StreamProgressMonitorTest {

	private static final String SEPARATOR = System
			.getProperty("line.separator");

	/**
	 * Test creating a progress monitor with a null stream paramter
	 */
	@Test(expected = IllegalArgumentException.class)
	public void nullStream() {
		new StreamProgressMonitor(null);
	}

	/**
	 * Test beginning a task with an unknown amount work
	 */
	@Test
	public void unknownWork() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		StreamProgressMonitor monitor = new StreamProgressMonitor(
				new PrintStream(stream));
		monitor.beginTask("test task", StreamProgressMonitor.UNKNOWN);
		assertEquals("test task ", stream.toString());
	}

	/**
	 * Test beginning a task with a known amount work
	 */
	@Test
	public void knownWork() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		StreamProgressMonitor monitor = new StreamProgressMonitor(
				new PrintStream(stream));
		monitor.beginTask("test task", 8);
		assertEquals("test task: 8 ", stream.toString());
	}

	/**
	 * Test ending a task
	 */
	@Test
	public void endTask() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		StreamProgressMonitor monitor = new StreamProgressMonitor(
				new PrintStream(stream));
		monitor.beginTask("test task", 1);
		monitor.endTask();
		assertEquals("test task: 1 " + StreamProgressMonitor.DONE + SEPARATOR,
				stream.toString());
	}

	/**
	 * Test beginning a task before the previous ends
	 */
	@Test
	public void beginBeforeEnd() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		StreamProgressMonitor monitor = new StreamProgressMonitor(
				new PrintStream(stream));
		monitor.beginTask("task1", 1);
		monitor.beginTask("task2", StreamProgressMonitor.UNKNOWN);
		assertEquals("task1: 1 " + StreamProgressMonitor.DONE + SEPARATOR
				+ "task2 ", stream.toString());
	}

}
