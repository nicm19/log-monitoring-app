Log Monitoring Tool

Overview:
This app processes a log file (logs.log) containing jobs START AND END times.

- Calculates the duration of each job.
- Logs in the console whether the job took more than 5 minutes (WARNING)
or more than 10 minutes (ERROR)

How to run application:

- Open Main.java and click the green run button next to public class Main.

Example Output:

== Job Durations ==
37980 | scheduled task 032 | 11:35:23 -> 11:35:56 | 0m33s
57672 | scheduled task 796 | 11:36:11 -> 11:36:18 | 0m07s
------------------------
== WARNINGS (>5 min) ==
87228 | 9m28s
50295 | 6m35s
------------------------
== ERRORS (>10 min) ==
39547 | 11m29s
45135 | 12m23s