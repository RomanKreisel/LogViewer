/*
 * Copyright (c) 2011, Roman Kreisel
 * This file is licensed under the terms of the BSD-License
 * http://www.opensource.org/licenses/bsd-license.php 
 */

package de.romankreisel.LogViewer;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

public class XMLLogfileInputStream extends FilterInputStream {
	private static int[] invalidCharacters = { 0x1b };
	private LinkedList<Character> lastChars = new LinkedList<Character>();
	private final static int maxLastCharsWindow = 6;
	private LinkedList<Character> attachedChars = null;

	public XMLLogfileInputStream(InputStream in) {
		super(in);
	}

	private void pushChar(char c) {
		if (this.lastChars.size() > maxLastCharsWindow) {
			this.lastChars.remove(0);
		}
		this.lastChars.add(c);
	}

	private boolean isForbidden(int character) {
		for (int c : invalidCharacters) {
			if (c == character) {
				return true;
			}
		}
		return false;
	}

	private void endOfStreamReached() {
		if (this.attachedChars == null) {
			this.attachedChars = new LinkedList<Character>();
			StringBuffer sb = new StringBuffer();
			for (char c : this.lastChars) {
				sb.append(c);
			}
			if (!sb.toString().contains("</log>")) {
				for (char c : "</log>".toCharArray()) {
					this.attachedChars.add(c);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.FilterInputStream#read()
	 */
	@Override
	public int read() throws IOException {
		int retVal = -1;
		do {
			retVal = this.in.read();
			if (this.isForbidden(retVal)) {
				continue;
			} else {
				if (retVal != -1) {
					this.lastChars.add((char) retVal);
					return retVal;
				} else {
					this.endOfStreamReached();
					if (this.attachedChars.size() > 0) {
						return this.attachedChars.pop();
					}
				}
			}
		} while (true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.FilterInputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int retVal = super.read(b, off, len);

		for (int i = 0; i < retVal; ++i) {
			if (this.isForbidden(b[i])) {
				retVal--;
				for (int j = i; j < retVal; ++j) {
					b[j] = b[j + 1];
				}
			}
		}

		if (retVal > 0) {
			for (int i = 0; i < retVal; ++i) {
				this.pushChar((char) b[i]);
			}
		}

		if (retVal != -1) {
			return retVal;
		} else {
			this.endOfStreamReached();
			if (this.attachedChars.size() > 0) {
				retVal = this.attachedChars.size();
				for (int i = 0; i < this.attachedChars.size(); ++i) {
					b[i] = (byte) (char) this.attachedChars.get(i);
				}
				this.attachedChars.clear();
				return retVal;
			} else {
				return -1;
			}
		}
	}
}
