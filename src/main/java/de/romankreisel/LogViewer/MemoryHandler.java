/*
 * Copyright (c) 2011, Roman Kreisel
 * This file is licensed under the terms of the GNU Lesser General Public License 2.1
 * http://www.gnu.org/licenses/lgpl-2.1.txt
 */
package de.romankreisel.LogViewer;

import java.util.LinkedList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Handler for java.util.logging which collects the LogRecords in memory. Use
 * this class, if you want to embedd LogViewer in your application and if you
 * want to show a live-view of your current application-log.
 * 
 * @author Roman Kreisel <mail@romankreisel.de>
 */
public class MemoryHandler extends Handler {

    private final int size;
    private final LinkedList<LogRecord> records;

    /**
     * @param size
     *            defines the maximum size of entries in this memory handler
     */
    public MemoryHandler(int size) {
        this.size = size;
        this.records = new LinkedList<LogRecord>();
    }

    @Override
    public void close() throws SecurityException {
    }

    @Override
    public void flush() {
    }

    @Override
    public void publish(LogRecord record) {
        if (this.records.size() >= this.size) {
            this.records.remove(0);
        }
        this.records.add(record);
    }

    /**
     * @return all records collected by this handler
     */
    public LinkedList<LogRecord> getRecords() {
        return this.records;
    }

}
