/*
 * Copyright (c) 2011, Roman Kreisel
 * This file is licensed under the terms of the BSD-License
 * http://www.opensource.org/licenses/bsd-license.php 
 */
package de.romankreisel.LogViewer;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Hello world!
 * 
 */
public class LogViewer {
	public static void main(String[] args) {
		LogViewer.testViewer();
	}

	public static void testViewer() {
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			if ("Nimbus".equals(info.getName())) {
				try {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				} catch (ClassNotFoundException e) {
					// ignore
				} catch (InstantiationException e) {
					// ignore
				} catch (IllegalAccessException e) {
					// ignore
				} catch (UnsupportedLookAndFeelException e) {
					// ignore
				}
				break;
			}
		}

		Logger testLogger = Logger.getLogger("TestLogger");
		MemoryHandler memoryHandler = new MemoryHandler(30);
		testLogger.setUseParentHandlers(false);
		testLogger.setLevel(Level.ALL);
		testLogger.addHandler(memoryHandler);
		testLogger.log(Level.INFO, "Info");
		testLogger.log(Level.FINE, "Fine");
		testLogger.log(Level.FINER, "Finer");
		testLogger.log(Level.FINEST, "Finest");
		testLogger.log(Level.WARNING, "Warning", new Exception("Testexception"));
		testLogger.log(Level.SEVERE, "Severe", new NullPointerException("Test-NPE"));

		LogviewFrame mainFrame = new LogviewFrame();
		mainFrame.getLogviewPanel().setLogRecords(memoryHandler.getRecords());
		mainFrame.pack();
		mainFrame.setVisible(true);

	}
}
