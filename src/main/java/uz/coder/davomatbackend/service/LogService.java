package uz.coder.davomatbackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class LogService {

    private static final Logger logger = LoggerFactory.getLogger(LogService.class);
    private static final String LOG_DIR = "logs";

    public List<Map<String, String>> getRecentLogs(Pageable pageable, String level) {
        return readLogFile("davomat-app.log", pageable, level);
    }

    public List<Map<String, String>> getSecurityLogs(Pageable pageable) {
        return readLogFile("security.log", pageable, "all");
    }

    public List<Map<String, String>> getErrorLogs(Pageable pageable) {
        return readLogFile("error.log", pageable, "all");
    }

    private List<Map<String, String>> readLogFile(String filename, Pageable pageable, String levelFilter) {
        List<Map<String, String>> logs = new ArrayList<>();
        File logFile = new File(LOG_DIR, filename);

        if (!logFile.exists()) {
            logger.warn("Log file not found: {}", filename);
            return logs;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            List<String> lines = new ArrayList<>();
            String line;
            
            // Read all lines
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            // Reverse to show newest first
            Collections.reverse(lines);

            // Parse and filter logs
            List<Map<String, String>> allLogs = new ArrayList<>();
            for (String logLine : lines) {
                Map<String, String> logEntry = parseLogLine(logLine);
                if (logEntry != null) {
                    if ("all".equalsIgnoreCase(levelFilter) || 
                        levelFilter.equalsIgnoreCase(logEntry.get("level"))) {
                        allLogs.add(logEntry);
                    }
                }
            }

            // Apply pagination
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), allLogs.size());
            
            if (start < allLogs.size()) {
                logs = allLogs.subList(start, end);
            }

        } catch (IOException e) {
            logger.error("Error reading log file: {}", filename, e);
        }

        return logs;
    }

    private Map<String, String> parseLogLine(String line) {
        try {
            // Expected format: 2026-02-27 00:31:56 [thread] LEVEL logger - message
            if (line.length() < 20) {
                return null;
            }

            Map<String, String> entry = new HashMap<>();
            
            // Extract timestamp
            String timestamp = line.substring(0, 19);
            entry.put("timestamp", timestamp);

            // Extract level
            String level = "INFO";
            if (line.contains("ERROR")) level = "ERROR";
            else if (line.contains("WARN")) level = "WARN";
            else if (line.contains("DEBUG")) level = "DEBUG";
            entry.put("level", level);

            // Extract message (everything after the logger name)
            int messageStart = line.indexOf(" - ");
            if (messageStart > 0) {
                String message = line.substring(messageStart + 3);
                entry.put("message", message);
            } else {
                entry.put("message", line.substring(20));
            }

            return entry;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean clearLogs(String type) {
        try {
            String filename;
            switch (type.toLowerCase()) {
                case "all":
                    filename = "davomat-app.log";
                    break;
                case "security":
                    filename = "security.log";
                    break;
                case "error":
                    filename = "error.log";
                    break;
                default:
                    return false;
            }

            Path logPath = Paths.get(LOG_DIR, filename);
            if (Files.exists(logPath)) {
                Files.write(logPath, new byte[0]);
                logger.info("Cleared log file: {}", filename);
                return true;
            }
            return false;
        } catch (IOException e) {
            logger.error("Error clearing logs", e);
            return false;
        }
    }
}
