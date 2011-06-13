/*
 * Copyright (c) 2011, Roman Kreisel
 * This file is licensed under the terms of the GNU Lesser General Public License 2.1
 * http://www.gnu.org/licenses/lgpl-2.1.txt
 */

package de.romankreisel.LogViewer;

/**
 * Interface allows classes to listen to changes of MemoryHandlers.
 * 
 * @author Roman Kreisel <mail@romankreisel.de>
 */
public interface MemoryHandlerListener {
    /**
     * Fired by MemoryHandler if a new LogRecord was added.
     * 
     * @param memoryHandler
     *            The MemoryHandler firing the event.
     * @param removedOldMessages
     *            True if the MemoryHandler has no more space for new records
     *            and removed old records.
     */
    void logRecordInserted(MemoryHandler memoryHandler, boolean removedOldMessages);
}
