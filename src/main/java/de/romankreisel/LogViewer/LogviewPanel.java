/*
 * Copyright (c) 2011, Roman Kreisel
 * This file is licensed under the terms of the BSD-License
 * http://www.opensource.org/licenses/bsd-license.php
 */
package de.romankreisel.LogViewer;

import java.awt.BorderLayout;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.LogRecord;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXTable;

/**
 * Use this class if you want to embedd the LogViewer in your own Frame.
 * 
 * @author Roman Kreisel <mail@romankreisel.de>
 */
public class LogviewPanel extends JPanel implements ListSelectionListener {
    /**
     *
     */
    private static final long serialVersionUID = 1061235427362096113L;
    private final JEditorPane textPane;
    private final JXTable table;
    private LogTableModel tableModel;
    private final JSplitPane splitPane;
    private static final String EMPTY_TEXT = "<html><h1>Select line from log to inspect content</h1><html>";

    /**
     * Constructs a new LogviewPanel.
     */
    public LogviewPanel() {
        this.setLayout(new BorderLayout());
        this.splitPane = new JSplitPane();
        this.add(this.splitPane, BorderLayout.CENTER);
        this.splitPane.setOneTouchExpandable(true);

        this.textPane = new JEditorPane("text/html", LogviewPanel.EMPTY_TEXT);
        this.textPane.setEditable(false);
        this.splitPane.setRightComponent(new JScrollPane(this.textPane));

        this.table = new JXTable();
        this.table.setAutoCreateRowSorter(true);
        this.table.setColumnControlVisible(true);
        this.splitPane.setLeftComponent(new JScrollPane(this.table));
        this.init();
    }

    /**
     * Initializes Panel.
     */
    private void init() {
        this.tableModel = new LogTableModel();
        this.table.setModel(this.tableModel);
        this.table.getSelectionModel().addListSelectionListener(this);
    }

    /**
     * @param position
     *            the position of the splitpaneDivider
     */
    public void setSplitPanePosition(double position) {
        this.splitPane.setDividerLocation(position);
    }

    /**
     * @return the current position of the splitpaneDivider
     */
    public double getSplitPanePosition() {
        return 1.0 * this.splitPane.getDividerLocation() / this.splitPane.getSize().width;
    }

    /**
     * @return all logRecords contained in this viewer.
     */
    public List<LogRecord> getLogRecords() {
        return this.tableModel.getLogRecords();
    }

    /**
     * @param logRecords
     *            show this records in LogviewPanel
     */
    public void setLogRecords(List<LogRecord> logRecords) {
        this.tableModel.setLogRecords(logRecords);
        this.table.packAll();
    }

    @Override
    public void valueChanged(ListSelectionEvent arg0) {
        LogRecord record = null;
        if (this.table.getSelectedRow() >= 0) {
            record = this.tableModel.getLogRecords().get(this.table.getSelectedRow());
        }
        this.showRecordDetails(record);
    }

    /**
     * @param record
     *            Show this record in the detail-view.
     */
    private void showRecordDetails(LogRecord record) {
        if (record != null) {
            StringBuffer sb = new StringBuffer();
            sb.append("<html>");
            sb.append("<table>");
            sb.append("<tr><td><b>Logger:</td><td>" + record.getLoggerName() + "</tr>");
            sb.append("<tr><td><b>Sequence:</b></td><td>" + record.getSequenceNumber() + "</tr>");
            sb.append("<tr><td><b>Time:</b></td><td>" + new SimpleDateFormat().format(new Date(record.getMillis()))
                    + "</tr>");
            sb.append("<tr><td><b>Source-class:</b></td><td>" + record.getSourceClassName() + "</tr>");
            sb.append("<tr><td><b>Source-method:</b></td><td>" + record.getSourceMethodName() + "</tr>");
            sb.append("<tr><td><b>Thread-ID:</b></td><td>" + record.getThreadID() + "</tr>");
            sb.append("<tr><td><b>Level:</b></td><td>" + record.getLevel() + "</tr>");
            sb.append("<tr><td><b>Message:</b></td><td>" + record.getMessage() + "</tr>");
            sb.append("</table>");
            sb.append("<hr />");
            if (record.getThrown() != null) {
                Throwable throwable = record.getThrown();
                if (throwable.getStackTrace() != null) {
                    sb.append("<b>Stacktrace:</b><br /><table>");
                    for (int i = 0; i < throwable.getStackTrace().length; ++i) {
                        sb.append("<tr><td>" + throwable.getStackTrace()[i].getClassName() + "</td><td>");
                        if (throwable.getStackTrace()[i].getLineNumber() > 0) {
                            sb.append(throwable.getStackTrace()[i].getLineNumber());
                        }
                        sb.append("</td></tr>");
                    }
                    sb.append("</table>");
                }
            }
            sb.append("</html>");
            this.textPane.setText(sb.toString());
        } else {
            this.textPane.setText(LogviewPanel.EMPTY_TEXT);
        }
    }
}
