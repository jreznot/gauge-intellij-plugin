package com.thoughtworks.gauge.inspection;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;
import com.thoughtworks.gauge.Constants;

public class GaugeError {
    private static final Logger LOG = Logger.getInstance(GaugeError.class);

    private final String fileName;
    private final int lineNumber;
    private final String message;

    public GaugeError(String type, String fileName, int lineNumber, String message) {
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.message = String.format("%s line number: %d, %s", type, lineNumber, message);
    }

    boolean isFrom(String fileName) {
        return this.fileName.equals(fileName);
    }

    public String getMessage() {
        return message;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getFileName() {
        return fileName;
    }

    int getOffset(String text) {
        return StringUtil.lineColToOffset(text, lineNumber - 1, 0);
    }

    public static GaugeError getInstance(String error) {
        try {
            String[] parts = error.split(" ");
            String[] fileInfo = parts[1].split(Constants.SPEC_SCENARIO_DELIMITER);
            return new GaugeError(parts[0], fileInfo[0], Integer.parseInt(fileInfo[1]), error.split(":\\d+:? ")[1]);
        } catch (Exception e) {
            LOG.debug(e);
            return null;
        }
    }
}
