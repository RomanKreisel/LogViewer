package de.romankreisel.LogViewer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public final class LogfileParser {
    /*
     * Copyright (c) 2011, Roman Kreisel This file is licensed under the terms
     * of the BSD-License http://www.opensource.org/licenses/bsd-license.php
     */

    private LogfileParser() {
        super();
    }

    public static List<LogRecord> parseXML(Logger logger, File xmlFile) throws FileNotFoundException,
            XMLStreamException {
        List<LogRecord> records = new LinkedList<LogRecord>();
        InputStream fis = null;
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            fis = new FileInputStream(xmlFile);
            fis = new XMLLogfileInputStream(fis);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(fis);

            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                switch (event.getEventType()) {
                default:
                    break;
                case XMLStreamConstants.START_ELEMENT:
                    StartElement startElement = event.asStartElement();
                    if (startElement.getName().toString().equals("log")) {
                        boolean logClosed = false;
                        while (eventReader.hasNext() && !logClosed) {
                            event = eventReader.nextEvent();
                            switch (event.getEventType()) {
                            default:
                                break;
                            case XMLStreamConstants.START_ELEMENT:
                                startElement = event.asStartElement();
                                if (startElement.getName().toString().equals("record")) {
                                    records.add(LogfileParser.parseRecord(logger, eventReader));
                                }
                                break;
                            case XMLStreamConstants.END_ELEMENT:
                                EndElement endElement = event.asEndElement();
                                if (endElement.getName().toString().equals("log")) {
                                    logClosed = true;
                                    break;
                                }
                            }
                        }
                    }
                    break;
                }
            }
            return records;
        } catch (FileNotFoundException e) {
            throw e;
        } catch (XMLStreamException e) {
            throw e;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    if (logger != null) {
                        logger.log(Level.FINEST, "Error closing InputStream", e);
                    }
                }
            }
        }
    }

    public static LogRecord parseRecord(Logger logger, XMLEventReader eventReader) throws XMLStreamException {
        XMLEvent event;
        StartElement startElement;
        LogRecord record = new LogRecord(Level.ALL, "");
        while (eventReader.hasNext()) {
            event = eventReader.nextEvent();
            switch (event.getEventType()) {
            default:
                break;
            case XMLStreamConstants.START_ELEMENT:
                startElement = event.asStartElement();
                if (startElement.getName().toString().equals("millis")) {
                    if (eventReader.peek().isCharacters()) {
                        event = eventReader.nextEvent();
                        try {
                            record.setMillis(Long.parseLong(event.asCharacters().toString()));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (startElement.getName().toString().equals("sequence")) {
                    if (eventReader.peek().isCharacters()) {
                        event = eventReader.nextEvent();
                        try {
                            record.setSequenceNumber(Long.parseLong(event.asCharacters().toString()));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (startElement.getName().toString().equals("logger")) {
                    if (eventReader.peek().isCharacters()) {
                        event = eventReader.nextEvent();
                        record.setLoggerName(event.asCharacters().toString());
                    }
                } else if (startElement.getName().toString().equals("logger")) {
                    if (eventReader.peek().isCharacters()) {
                        event = eventReader.nextEvent();
                        record.setLoggerName(event.asCharacters().toString());
                    }
                } else if (startElement.getName().toString().equals("level")) {
                    if (eventReader.peek().isCharacters()) {
                        event = eventReader.nextEvent();
                        record.setLevel(Level.parse(event.asCharacters().toString()));
                    }
                } else if (startElement.getName().toString().equals("class")) {
                    if (eventReader.peek().isCharacters()) {
                        event = eventReader.nextEvent();
                        record.setSourceClassName(event.asCharacters().toString());
                    }
                } else if (startElement.getName().toString().equals("method")) {
                    if (eventReader.peek().isCharacters()) {
                        event = eventReader.nextEvent();
                        record.setSourceMethodName(event.asCharacters().toString());
                    }
                } else if (startElement.getName().toString().equals("thread")) {
                    if (eventReader.peek().isCharacters()) {
                        event = eventReader.nextEvent();
                        try {
                            record.setThreadID(Integer.parseInt(event.asCharacters().toString()));
                        } catch (NumberFormatException e) {
                            record.setThreadID(-1);
                        }
                    }
                } else if (startElement.getName().toString().equals("message")) {
                    if (eventReader.peek().isCharacters()) {
                        event = eventReader.nextEvent();
                        record.setMessage(event.asCharacters().toString());
                    }
                } else if (startElement.getName().toString().equals("exception")) {
                    Exception exception = null;
                    startElement = event.asStartElement();
                    exception = LogfileParser.parseException(logger, eventReader);
                    if (exception != null) {
                        record.setThrown(exception);
                    }
                }
                break;
            case XMLStreamConstants.END_ELEMENT:
                EndElement endElement = event.asEndElement();
                if (endElement.getName().toString().equals("record")) {
                    return record;
                }
            }
        }
        return record;
    }

    private static Exception parseException(Logger logger, XMLEventReader eventReader) throws XMLStreamException {
        Exception exception = null;
        boolean exceptionClosed = false;
        LinkedList<StackTraceElement> stackTraceElements = new LinkedList<StackTraceElement>();
        StartElement startElement;
        while (eventReader.hasNext() && !exceptionClosed) {
            XMLEvent event = eventReader.nextEvent();
            switch (event.getEventType()) {
            default:
                break;
            case XMLStreamConstants.START_ELEMENT:
                startElement = event.asStartElement();
                if (startElement.getName().toString().equals("message")) {
                    if (eventReader.peek().isCharacters()) {
                        event = eventReader.nextEvent();
                        String exceptionString = event.asCharacters().toString();
                        String exceptionClassName = null;
                        String exceptionText = exceptionString.trim();
                        if (exceptionString.split(":").length > 0) {
                            exceptionClassName = exceptionString.split(":")[0].trim();
                            exceptionText = exceptionString.substring(exceptionString.indexOf(':') + 1);
                            exceptionText = exceptionText.trim();
                        }
                        Class<?> exceptionClass = null;
                        if (exceptionClassName != null) {
                            try {
                                exceptionClass = java.lang.ClassLoader.getSystemClassLoader().loadClass(
                                        exceptionClassName);
                            } catch (ClassNotFoundException e) {
                                if (logger != null) {
                                    logger.log(Level.WARNING, "Cannot load exception-class", e);
                                }
                            }
                        }
                        if (exceptionClass != null) {
                            Class<?>[] args = new Class[1];
                            args[0] = String.class;
                            try {
                                Constructor<?> constructor = exceptionClass.getConstructor(args);
                                exception = (Exception) constructor.newInstance(exceptionText);
                            } catch (SecurityException e) {
                                if (logger != null) {
                                    logger.log(Level.WARNING, "Cannot load exception-class", e);
                                }
                            } catch (NoSuchMethodException e) {
                                if (logger != null) {
                                    logger.log(Level.WARNING, "Cannot load exception-class", e);
                                }
                            } catch (IllegalArgumentException e) {
                                if (logger != null) {
                                    logger.log(Level.WARNING, "Cannot load exception-class", e);
                                }
                            } catch (InstantiationException e) {
                                if (logger != null) {
                                    logger.log(Level.WARNING, "Cannot load exception-class", e);
                                }
                            } catch (IllegalAccessException e) {
                                if (logger != null) {
                                    logger.log(Level.WARNING, "Cannot load exception-class", e);
                                }
                            } catch (InvocationTargetException e) {
                                if (logger != null) {
                                    logger.log(Level.WARNING, "Cannot load exception-class", e);
                                }
                            }
                        } else {
                            exception = new Exception(exceptionString);
                        }
                    }
                } else if (startElement.getName().toString().equals("frame")) {
                    boolean frameClosed = false;
                    String declaringClass = null;
                    String methodName = null;
                    int lineNumber = -1;
                    while (eventReader.hasNext() && !frameClosed) {
                        event = eventReader.nextEvent();
                        switch (event.getEventType()) {
                        default:
                            break;
                        case XMLStreamConstants.START_ELEMENT:
                            startElement = event.asStartElement();
                            if (startElement.getName().toString().equals("class")) {
                                if (eventReader.peek().isCharacters()) {
                                    event = eventReader.nextEvent();
                                    declaringClass = event.asCharacters().toString();
                                }
                            } else if (startElement.getName().toString().equals("method")) {
                                if (eventReader.peek().isCharacters()) {
                                    event = eventReader.nextEvent();
                                    methodName = event.asCharacters().toString();
                                }
                            } else if (startElement.getName().toString().equals("line")) {
                                if (eventReader.peek().isCharacters()) {
                                    event = eventReader.nextEvent();
                                    try {
                                        lineNumber = Integer.parseInt(event.asCharacters().toString());
                                    } catch (NumberFormatException e) {
                                        lineNumber = -1;
                                    }
                                }
                            }
                            break;
                        case XMLStreamConstants.END_ELEMENT:
                            if (event.asEndElement().getName().toString().equals("frame")) {
                                frameClosed = true;
                                if (declaringClass != null && methodName != null) {
                                    stackTraceElements.add(new StackTraceElement(declaringClass, methodName, "",
                                            lineNumber));
                                }
                            }
                            break;
                        }
                    }
                }
                break;
            case XMLStreamConstants.END_ELEMENT:
                if (event.asEndElement().getName().toString().equals("exception")) {
                    exceptionClosed = true;
                }
                break;
            }
        }
        if (exception != null) {
            exception.setStackTrace(stackTraceElements.toArray(new StackTraceElement[stackTraceElements.size()]));
        }
        return exception;
    }
}
