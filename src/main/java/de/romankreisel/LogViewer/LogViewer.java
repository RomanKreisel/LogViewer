/*
 * Copyright (c) 2011, Roman Kreisel
 * This file is licensed under the terms of the BSD-License
 * http://www.opensource.org/licenses/bsd-license.php
 */
package de.romankreisel.LogViewer;

import java.io.File;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Hello world!
 */
public class LogViewer {
    public static void main(String[] args) {
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
        LogviewFrame mainFrame = new LogviewFrame();
        if (args.length > 0) {
            if (args.length < 2) {
                File logFile = new File(args[0]);
                if (logFile.exists() && logFile.isFile()) {
                    mainFrame.openFile(logFile);
                }
            }
        }
        mainFrame.pack();
        mainFrame.setVisible(true);
    }
}
