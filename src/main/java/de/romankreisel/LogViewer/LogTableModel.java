/*
 * Copyright (c) 2011, Roman Kreisel
 * This file is licensed under the terms of the BSD-License
 * http://www.opensource.org/licenses/bsd-license.php 
 */
package de.romankreisel.LogViewer;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.table.AbstractTableModel;

public class LogTableModel extends AbstractTableModel {
    /**
	 * 
	 */
    private static final long serialVersionUID = 6296572154686892582L;
    private List<LogRecord> allLogRecords = new LinkedList<LogRecord>();
    private List<LogRecord> shownRecords = this.allLogRecords;
    private Level minimumLevel = Level.ALL;

    @Override
    public int getColumnCount() {
        return 8;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
        case 0:
            return Long.class;
        case 2:
            return Level.class;
        case 6:
            return Integer.class;
        case 7:
            return Boolean.class;
        default:
            return String.class;
        }
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
        case 0:
            return "Sequence";
        case 1:
            return "Time";
        case 2:
            return "Level";
        case 3:
            return "Message";
        case 4:
            return "Source-Class";
        case 5:
            return "Source-Method";
        case 6:
            return "Thread-ID";
        case 7:
            return "Throwable";
        default:
            return "Error";
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public int getRowCount() {
        return this.allLogRecords.size();
    }

    @Override
    public Object getValueAt(int row, int column) {
        LogRecord record = this.allLogRecords.get(row);
        switch (column) {
        case 0:
            return record.getSequenceNumber();
        case 1:
            return new SimpleDateFormat().format(new Date(record.getMillis()));
        case 2:
            return record.getLevel();
        case 3:
            return record.getMessage();
        case 4:
            return record.getSourceClassName();
        case 5:
            return record.getSourceMethodName();
        case 6:
            return record.getThreadID();
        case 7:
            return Boolean.valueOf(record.getThrown() != null);
        default:
            return "Error";
        }
    }

    /**
     * @return contained LogRecords
     */
    public List<LogRecord> getLogRecords() {
        return this.allLogRecords;
    }

    /**
     * @param logRecords
     *            Fills TableModel with new content and fires TableDataChanged
     */
    public void setLogRecords(List<LogRecord> logRecords) {
        this.allLogRecords = logRecords;
        this.filterRecords();
        this.fireTableDataChanged();
    }

    private void filterRecords() {
        this.shownRecords = new LinkedList<LogRecord>();
        for (LogRecord record : this.allLogRecords) {
            if (record.getLevel().intValue() >= this.minimumLevel.intValue()) {
                this.shownRecords.add(record);
            }
        }
    }

    /**
     * @param minimumLevel
     *            the minimumLevel to set
     */
    public void setMinimumLevel(Level minimumLevel) {
        this.minimumLevel = minimumLevel;
        this.filterRecords();
    }

    /**
     * @return the minimumLevel
     */
    public Level getMinimumLevel() {
        return this.minimumLevel;
    }

    /**
     * @return the shownRecords
     */
    public List<LogRecord> getShownRecords() {
        return this.shownRecords;
    }
}
