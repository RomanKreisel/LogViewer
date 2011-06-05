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

/**
 * TableModel for LogRecords.
 * 
 * @author Roman Kreisel <mail@romankreisel.de>
 */
public class LogTableModel extends AbstractTableModel {
    /**
     *
     */
    private static final long serialVersionUID = 6296572154686892582L;

    private static final int COLUMN_SEQUENCE = 0;
    private static final int COLUMN_TIME = 1;
    private static final int COLUMN_LEVEL = 2;
    private static final int COLUMN_MESSAGE = 3;
    private static final int COLUMN_SOURCE_CLASS = 4;
    private static final int COLUMN_SOURCE_METHOD = 5;
    private static final int COLUMN_THREAD_ID = 6;
    private static final int COLUMN_THROWABLE = 7;

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
        case COLUMN_SEQUENCE:
            return Long.class;
        case COLUMN_LEVEL:
            return Level.class;
        case COLUMN_THREAD_ID:
            return Integer.class;
        case COLUMN_THROWABLE:
            return Boolean.class;
        default:
            return String.class;
        }
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
        case COLUMN_SEQUENCE:
            return "Sequence";
        case COLUMN_TIME:
            return "Time";
        case COLUMN_LEVEL:
            return "Level";
        case COLUMN_MESSAGE:
            return "Message";
        case COLUMN_SOURCE_CLASS:
            return "Source-Class";
        case COLUMN_SOURCE_METHOD:
            return "Source-Method";
        case COLUMN_THREAD_ID:
            return "Thread-ID";
        case COLUMN_THROWABLE:
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
        case COLUMN_SEQUENCE:
            return record.getSequenceNumber();
        case COLUMN_TIME:
            return new SimpleDateFormat().format(new Date(record.getMillis()));
        case COLUMN_LEVEL:
            return record.getLevel();
        case COLUMN_MESSAGE:
            return record.getMessage();
        case COLUMN_SOURCE_CLASS:
            return record.getSourceClassName();
        case COLUMN_SOURCE_METHOD:
            return record.getSourceMethodName();
        case COLUMN_THREAD_ID:
            return record.getThreadID();
        case COLUMN_THROWABLE:
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

    /**
     * Filter records shown by this tableModel.
     */
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
