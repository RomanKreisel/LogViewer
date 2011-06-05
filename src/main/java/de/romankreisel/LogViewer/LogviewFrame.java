/*
 * Copyright (c) 2011, Roman Kreisel
 * This file is licensed under the terms of the BSD-License
 * http://www.opensource.org/licenses/bsd-license.php 
 */
package de.romankreisel.LogViewer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.logging.LogRecord;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.xml.stream.XMLStreamException;

public class LogviewFrame extends JFrame {

    /**
	 * 
	 */
    private static final long serialVersionUID = -6889068644722360234L;
    private final LogviewPanel logviewPanel;
    private File lastDirectory = new File(System.getProperty("user.dir"));
    private FileFilter filter = null;
    private String frameTitle = "LogViewer";

    public File getLastDirectory() {
        return this.lastDirectory;
    }

    public void setLastDirectory(File lastDirectory) {
        this.lastDirectory = lastDirectory;
    }

    public LogviewFrame() throws HeadlessException {
        this.setTitle(this.frameTitle);
        JPanel southPanel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) southPanel.getLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        this.getContentPane().add(southPanel, BorderLayout.SOUTH);

        JButton btnClose = new JButton("Close");

        btnClose.setMnemonic(KeyEvent.VK_C);
        btnClose.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                LogviewFrame.this.dispose();
            }
        });

        JButton btnOpenFile = new JButton("Open File");
        btnOpenFile.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(LogviewFrame.this.lastDirectory);
                if (LogviewFrame.this.filter != null) {
                    chooser.setFileFilter(LogviewFrame.this.filter);
                }
                int retVal = chooser.showOpenDialog(LogviewFrame.this);
                if (retVal == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = chooser.getSelectedFile();
                    LogviewFrame.this.openFile(selectedFile);
                }
            }
        });
        btnOpenFile.setMnemonic(KeyEvent.VK_F);
        southPanel.add(btnOpenFile);
        southPanel.add(btnClose);

        this.logviewPanel = new LogviewPanel();
        this.getContentPane().add(this.logviewPanel, BorderLayout.CENTER);
        this.init();
    }

    private void init() {
    }

    public void openFile(File file) {
        try {
            List<LogRecord> logRecords = LogfileParser.parseXML(file);
            LogviewFrame.this.lastDirectory = file.getParentFile();
            LogviewFrame.this.logviewPanel.setLogRecords(logRecords);
            LogviewFrame.this.setTitle(LogviewFrame.this.frameTitle + " - " + file.getName());
        } catch (FileNotFoundException e1) {
            JOptionPane.showMessageDialog(LogviewFrame.this, "Logfile cannot be found!", "File not found!",
                    JOptionPane.ERROR_MESSAGE);
        } catch (XMLStreamException e1) {
            JOptionPane.showMessageDialog(LogviewFrame.this, "Logfile cannot be parsed!", "Parsing error!",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public LogviewPanel getLogviewPanel() {
        return this.logviewPanel;
    }

}
