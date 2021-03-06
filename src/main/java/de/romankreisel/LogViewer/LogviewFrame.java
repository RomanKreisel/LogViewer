/*
 * Copyright (c) 2011, Roman Kreisel
 * This file is licensed under the terms of the GNU Lesser General Public License 2.1
 * http://www.gnu.org/licenses/lgpl-2.1.txt
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

/**
 * LogViewer-JFrame - use this class if you're searching for an easy way to
 * embed LogViewer in your project.
 * 
 * @author Roman Kreisel <mail@romankreisel.de>
 */
public class LogviewFrame extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = -6889068644722360234L;
    private final LogviewPanel logviewPanel;
    private File lastDirectory = new File(System.getProperty("user.dir"));
    private FileFilter filter = null;
    private String frameTitle = "LogViewer";

    /**
     * @return the last Directory from which a logfile was opened
     */
    public File getLastDirectory() {
        return this.lastDirectory;
    }

    /**
     * @param lastDirectory
     *            sets the directory the open-dialog should initially show
     */
    public void setLastDirectory(File lastDirectory) {
        this.lastDirectory = lastDirectory;
    }

    /**
     * @throws HeadlessException
     *             if GraphicsEnvironment.isHeadless() returns true.
     */
    public LogviewFrame() throws HeadlessException {
        if (this.getClass().getPackage().getImplementationTitle() != null
                && this.getClass().getPackage().getImplementationVersion() != null) {
            this.frameTitle = this.getClass().getPackage().getImplementationTitle() + " "
                    + this.getClass().getPackage().getImplementationVersion();
        }
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

    /**
     * Initializes this frame - called by constructor.
     */
    private void init() {
    }

    /**
     * @param file
     *            open this file in LogviewFrame
     */
    public void openFile(File file) {
        try {
            List<LogRecord> logRecords = LogfileParser.parseXML(null, file);
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

    /**
     * @return embedded LogviewPanel
     */
    public LogviewPanel getLogviewPanel() {
        return this.logviewPanel;
    }
}
