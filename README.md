King Game Java Backend
----

This was a pretty fun project, as I haven't ever made my own "servlet" with such a lowend class.
Normally I am using something like Spring with Tomcat. Writing my own RequestHandler was the
best part of the assignment, however designing the LinkedList data structure to hold the high
scores was also very rewarding. I have tried to throw as many tests as I could at it, however
I would have liked to figure out a better way to automate some multi-threaded testing of the
actual endpoint with some HTTP client automation. I will have to think and come back to this
issue.

I used Java 13 to code the solution, so the JRE 13 will be required.

The JAR can be executed with the options of port and debug like so.


java -jar king-java-project.jar port=8082 debug=true


The default port is 8080 and debug defaults to false. The debug option will report back on
some of the request and session details.

I attempted to unit test as many components as I could, however I would like to add more
integration tests in the future.

HighScores Data Structure
---

I used a LinkedList that I synchronized myself. After every addition, the data structure
will check if the Score input is greater than the lowest score. If it is greater then it
will insert the score with the following exceptions: if the same score is already present,
or if the user already has a previous score higher than this one.

State
---

Meanwhile, all the session information as well as the highscores per level is contained
by ConcurrentHashMaps which are contained in the GameState object.

I hope you enjoy!