/*
 * Copyright (c) 2011, Roman Kreisel
 * This file is licensed under the terms of the GNU Lesser General Public License 2.1
 * http://www.gnu.org/licenses/lgpl-2.1.txt
 */

package de.romankreisel.LogViewer.examples;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import de.romankreisel.LogViewer.LogviewFrame;
import de.romankreisel.LogViewer.MemoryHandler;

/**
 * Example Application which shows how to add a LogViewer to your own
 * application.
 * 
 * @author Roman Kreisel <mail@romankreisel.de>
 */
public final class ExampleOfEmbeddedViewer {

    /**
     * Main-Method - starts this example-application.
     * 
     * @param args
     *            are ignored
     */
    public static void main(String[] args) {
        Logger logger = Logger.getAnonymousLogger();
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.ALL); // we want ALL LogRecords

        // Create a new memoryhandler with place for 1000 LogRecords and add it
        // to your logger
        MemoryHandler memoryHandler = new MemoryHandler(1000);
        logger.addHandler(memoryHandler);

        // Just a more beautiful look&feel, no need to do this, if you don't
        // like it
        for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                try {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                } catch (ClassNotFoundException e) {
                    System.err.println("Nimbus not found, not changing Look&Feel");
                } catch (InstantiationException e) {
                    System.err.println("Nimbus not found, not changing Look&Feel");
                } catch (IllegalAccessException e) {
                    System.err.println("Nimbus not found, not changing Look&Feel");
                } catch (UnsupportedLookAndFeelException e) {
                    System.err.println("Nimbus not found, not changing Look&Feel");
                }
                break;
            }
        }
        // Instantiate and show the LogViewer
        LogviewFrame mainFrame = new LogviewFrame();
        mainFrame.pack();
        mainFrame.setVisible(true);
        mainFrame.getLogviewPanel().setSplitPanePosition(0.5);

        // Don't do this in your own application, it causes the whole
        // application to exit when the LogViewer was closed!
        mainFrame.setDefaultCloseOperation(LogviewFrame.EXIT_ON_CLOSE);

        mainFrame.getLogviewPanel().setMemoryHandler(memoryHandler);

        // New LogRecords will automatically be shown in all opened LogViewers,
        // but of course existent ones will be shown as well.
        TestLogCreator.appendAllLogRecords(logger);

    }

    /**
     * Prevent other classes to instantiate this class.
     */
    private ExampleOfEmbeddedViewer() {
    }

}
