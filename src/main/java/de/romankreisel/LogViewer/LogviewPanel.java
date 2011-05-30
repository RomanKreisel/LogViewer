/*
 * Copyright (c) 2011, Roman Kreisel
 * This file is licensed under the terms of the BSD-License
 * http://www.opensource.org/licenses/bsd-license.php 
 */
package de.romankreisel.LogViewer;

import java.awt.BorderLayout;
import java.util.List;
import java.util.logging.LogRecord;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXTable;

public class LogviewPanel extends JPanel implements ListSelectionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1061235427362096113L;
	private final JTextField textField;
	private final JXTable table;
	private LogTableModel tableModel;
	private final JSplitPane splitPane;

	public LogviewPanel() {
		this.setLayout(new BorderLayout());
		this.splitPane = new JSplitPane();
		this.add(this.splitPane, BorderLayout.CENTER);
		this.splitPane.setOneTouchExpandable(true);

		this.textField = new JTextField();
		this.textField.setEditable(false);
		this.textField.setColumns(35);
		this.splitPane.setRightComponent(new JScrollPane(this.textField));

		this.table = new JXTable();
		this.table.setAutoCreateRowSorter(true);
		this.table.setColumnControlVisible(true);
		this.splitPane.setLeftComponent(new JScrollPane(this.table));
		this.init();
	}

	private void init() {
		this.tableModel = new LogTableModel();
		this.table.setModel(this.tableModel);
		this.table.getSelectionModel().addListSelectionListener(this);
	}

	public void setSplitPanePosition(double position) {
		this.splitPane.setDividerLocation(position);
	}

	public double getSplitPanePosition() {
		return 1.0 * this.splitPane.getDividerLocation() / this.splitPane.getSize().width;
	}

	public List<LogRecord> getLogRecords() {
		return this.tableModel.getLogRecords();
	}

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

	private void showRecordDetails(LogRecord record) {
		if (record != null) {
			StringBuffer sb = new StringBuffer();
			sb.append("<HTML>");
			sb.append(record.getMessage());
			sb.append("</HTML>");
			this.textField.setText(record.getMessage());
		} else {
			this.textField.setText("");
		}
	}
}
