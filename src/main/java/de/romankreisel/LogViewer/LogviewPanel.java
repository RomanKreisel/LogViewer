/*
 * Copyright (c) 2011, Roman Kreisel
 * This file is licensed under the terms of the GNU Lesser General Public License 2.1
 * http://www.gnu.org/licenses/lgpl-2.1.txt
 */
package de.romankreisel.LogViewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;

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
        this.splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        this.add(this.splitPane, BorderLayout.CENTER);
        this.splitPane.setOneTouchExpandable(true);

        this.textPane = new JEditorPane("text/html", LogviewPanel.EMPTY_TEXT);
        this.textPane.setEditable(false);
        this.splitPane.setRightComponent(new JScrollPane(this.textPane));

        this.table = new JXTable();
        this.table.setAutoCreateRowSorter(true);
        this.table.setColumnControlVisible(true);
        this.table.setAutoResizeMode(JXTable.AUTO_RESIZE_LAST_COLUMN);
        this.table.addHighlighter(new AbstractHighlighter(HighlightPredicate.ALWAYS) {

            @Override
            protected Component doHighlight(Component component, ComponentAdapter adapter) {
                Object levelColumn = adapter.getValue(2);
                if (levelColumn instanceof Level) {
                    Level level = (Level) levelColumn;
                    if (level.intValue() <= Level.FINEST.intValue()) {
                        component.setBackground(new Color(0xf0, 0xf0, 0xf0));
                    } else if (level.intValue() <= Level.FINER.intValue()) {
                        component.setBackground(Color.lightGray);
                    } else if (level.intValue() <= Level.FINE.intValue()) {
                        component.setBackground(Color.gray);
                        component.setForeground(new Color(0xf0, 0xf0, 0xf0));
                    } else if (level.intValue() <= Level.CONFIG.intValue()) {
                        component.setBackground(new Color(0xa0, 0xff, 0xa0));
                    } else if (level.intValue() <= Level.INFO.intValue()) {
                        component.setBackground(Color.green);
                    } else if (level.intValue() <= Level.WARNING.intValue()) {
                        component.setBackground(Color.yellow);
                    } else {
                        component.setBackground(Color.red);
                    }
                }
                return component;
            }
        });
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
        int totalSize = 0;
        this.table.setAutoResizeMode(JXTable.AUTO_RESIZE_OFF);
        this.table.packColumn(LogTableModel.COLUMN_MESSAGE, 0);
        for (int i = 0; i < this.table.getColumnCount(); ++i) {
            if (i != LogTableModel.COLUMN_MESSAGE) {
                this.table.packColumn(i, 0);
                totalSize += this.table.getColumnExt(i).getPreferredWidth();
            }
        }
        this.table.packColumn(LogTableModel.COLUMN_MESSAGE, 0, this.table.getWidth() - (50 + totalSize));
        this.table.setAutoResizeMode(JXTable.AUTO_RESIZE_LAST_COLUMN);
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
                            sb.append("(Line " + throwable.getStackTrace()[i].getLineNumber() + ")");
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
