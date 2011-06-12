/*
 * Copyright (c) 2011, Roman Kreisel
 * This file is licensed under the terms of the GNU Lesser General Public License 2.1
 * http://www.gnu.org/licenses/lgpl-2.1.txt
 */
package de.romankreisel.LogViewer.mock;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class creates Logs with sample log-records.
 * 
 * @author Roman Kreisel <mail@romankreisel.de>
 */
public final class TestLogCreator {
    private static Level[] arrayOfLoglevels = {Level.FINEST, Level.FINER, Level.FINE, Level.CONFIG, Level.INFO,
            Level.WARNING, Level.SEVERE};
    private static Random random = new Random(System.currentTimeMillis());

    /**
     * Private Constructor to prevent other classes to instantiate this Class.
     */
    private TestLogCreator() {
    }

    /**
     * Create a testLogfile called "test.log" in the current directory.
     * 
     * @param args
     *            Will be ignored.
     */
    public static void main(String[] args) {
        Logger logger = null;
        try {
            logger = Logger.getAnonymousLogger();
            logger.setUseParentHandlers(false);
            logger.setLevel(Level.ALL);
            logger.addHandler(new FileHandler("test.log"));
            TestLogCreator.appendAllLogRecords(logger);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (logger != null) {
                LinkedList<Handler> handlers = new LinkedList<Handler>();
                for (Handler h : logger.getHandlers()) {
                    handlers.add(h);
                }
                for (Handler h : handlers) {
                    logger.removeHandler(h);
                    h.close();
                }
            }
        }
    }

    /**
     * Appends all LogRecords createable by this class to the logger.
     * 
     * @param logger
     *            to fill with records.
     */
    private static void appendAllLogRecords(Logger logger) {
        TestLogCreator.appendSimpleLogRecords(logger);
        TestLogCreator.appendComplextLogRecords(logger);
    }

    /**
     * Appends simple logmessages.
     * 
     * @param logger
     *            to fill with records.
     */
    public static void appendSimpleLogRecords(Logger logger) {
        for (Level level : arrayOfLoglevels) {
            logger.log(level, "This is a typical logmessage with severity " + level.getName());
            try {
                long timeout = random.nextLong() % 3000;
                if (timeout < 0) {
                    timeout = timeout * -1;
                }
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Appends complex logmessages.
     * 
     * @param logger
     *            to fill with records.
     */
    public static void appendComplextLogRecords(Logger logger) {
        for (Level level : arrayOfLoglevels) {
            logger.log(level, "This is a complex logmessage with severity " + level.getName()
                    + ". It contains a very very loooooong logmessage. Lorem ipsum dolor "
                    + "sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor "
                    + " invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."
                    + " At vero eos et accusam et justo duo dolores et ea rebum."
                    + " Stet clita kasd gubergren, no sea takimata sanctus est Lorem "
                    + "ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing"
                    + " elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
                    + "aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores "
                    + "et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est"
                    + " Lorem ipsum dolor sit amet.");
            try {
                long timeout = random.nextLong() % 3000;
                if (timeout < 0) {
                    timeout = timeout * -1;
                }
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        TestLogCreator.appendLogRecordsWithException(logger);
    }

    /**
     * Appends logmessages containing exceptions.
     * 
     * @param logger
     *            to fill with records.
     */
    public static void appendLogRecordsWithException(Logger logger) {
        for (Level level : arrayOfLoglevels) {
            logger.log(level, "This is a logmessage containing a generated NullPointerException",
                    new NullPointerException("Nothing happend, just a test"));
            try {
                long timeout = random.nextLong() % 3000;
                if (timeout < 0) {
                    timeout = timeout * -1;
                }
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
