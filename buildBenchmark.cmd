@echo off
javac -target 7 -d ".\build" .\src\standard\RoutePlanner.java
javac -target 7 -classpath ".\build" -d ".\build" .\src\benchmark\Benchmark.java
set /p args= "Input (all) arguments with proper Syntax: "
@echo on
java -classpath ".\build"  benchmark.Benchmark  %args%

@pause
