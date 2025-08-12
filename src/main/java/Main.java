import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class Main {
    static class LogEntry {
        enum Type { START, END } // log entries, START and END
        LocalTime time;
        String description;
        Type type;
        String pid;
        LogEntry(LocalTime time, String description, Type type, String pid) {
            this.time = time;
            this.description = description;
            this.type = type;
            this.pid = pid;
        }
    }

    // duration of each job from start to end
    static class JobSpan {
        String pid, description;
        LocalTime start, end;
        Duration duration;
        JobSpan(String pid, String description, LocalTime start, LocalTime end) {
            this.pid = pid;
            this.description = description;
            this.start = start;
            this.end = end;
            this.duration = Duration.between(start, end);
        }

        // formating the time taken for each entry in mins and secs
        String prettyDuration() {
            long s = duration.getSeconds();
            return (s / 60) + "m" + String.format("%02d", s % 60) + "s";
        }
    }

    public static void main(String[] args) throws Exception {
        // Read all lines from logs.log
        List<String> lines = Files.readAllLines(Paths.get("logs.log"));
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss");

        //parses each entry into a LogEntry object
        List<LogEntry> entries = new ArrayList<>();
        for (String line : lines) {
            if (line.isBlank()) continue;
            String[] p = line.split(",", 4);
            entries.add(new LogEntry(
                    LocalTime.parse(p[0].trim(), fmt),
                    p[1].trim(),
                    LogEntry.Type.valueOf(p[2].trim()),
                    p[3].trim()
            ));
        }
        entries.sort(Comparator.comparing(e -> e.time));

        Map<String, LogEntry> active = new HashMap<>();
        List<JobSpan> completed = new ArrayList<>();

        for (LogEntry e : entries) {
            if (e.type == LogEntry.Type.START) {
                active.put(e.pid, e);
            } else {
                LogEntry start = active.remove(e.pid);
                if (start != null) {
                    completed.add(new JobSpan(e.pid, e.description, start.time, e.time));
                }
            }
        }

        System.out.println("== Job Durations ==");
        for (JobSpan js : completed) {
            System.out.printf("%s | %s | %s -> %s | %s%n",
                    js.pid, js.description, js.start, js.end, js.prettyDuration());
        }
//logs warnings >5min <=10mins
        System.out.println("\n== WARNINGS (>5 min) ==");
        completed.stream()
                .filter(js -> js.duration.toMinutes() > 5 && js.duration.toMinutes() <= 10)
                .forEach(js -> System.out.printf("%s | %s%n", js.pid, js.prettyDuration()));

        //logs errors >10mins
        System.out.println("\n== ERRORS (>10 min) ==");
        completed.stream()
                .filter(js -> js.duration.toMinutes() > 10)
                .forEach(js -> System.out.printf("%s | %s%n", js.pid, js.prettyDuration()));
    }
}
