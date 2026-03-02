# auditorium

Spatial audio soundscape tool

![screenshot](screenshot.png)

## Development

```sh
mvn verify          # verify project config & code style
mvn exec:java       # run the project in development
mvn package         # package a JAR with bundled dependencies (`target/auditorium-*.*.*-jar-with-dependencies.jar`)
mvn spotless:apply  # format all source files
```
