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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class LogviewFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6889068644722360234L;
	private final LogviewPanel logviewPanel;

	public LogviewFrame() throws HeadlessException {
		this.setTitle("LogViewer");
		JPanel southPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) southPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		this.getContentPane().add(southPanel, BorderLayout.SOUTH);

		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				LogviewFrame.this.dispose();
			}
		});
		southPanel.add(btnClose);

		this.logviewPanel = new LogviewPanel();
		this.getContentPane().add(this.logviewPanel, BorderLayout.CENTER);
		this.init();
	}

	private void init() {
	}

	public LogviewPanel getLogviewPanel() {
		return this.logviewPanel;
	}
}
