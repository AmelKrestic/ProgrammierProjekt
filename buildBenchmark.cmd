@echo off
javac -d ".\build" .\src\standard\RoutePlanner.java
javac -classpath ".\build" -d ".\build" .\src\benchmark\Benchmark.java
set /p args= "Input (all) arguments with proper Syntax: "
@echo on
java -classpath ".\build"  benchmark.Benchmark  %args%

@pause