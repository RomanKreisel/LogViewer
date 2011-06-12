/*
 * Copyright (c) 2011, Roman Kreisel
 * This file is licensed under the terms of the GNU Lesser General Public License 2.1
 * http://www.gnu.org/licenses/lgpl-2.1.txt
 */
package de.romankreisel.LogViewer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class LogViewerTest extends TestCase {
    /**
     * Create the test case.
     * 
     * @param testName
     *            name of the test case
     */
    public LogViewerTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(LogViewerTest.class);
    }

    /**
     * Always-True-Test.
     */
    public void testApp() {
        assertTrue(true);
    }
}
