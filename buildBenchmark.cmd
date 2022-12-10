@echo off
javac -source 7 -target 7 -d ".\build" .\src\standard\RoutePlanner.java
javac -source 7 -target 7 -classpath ".\build" -d ".\build" .\src\benchmark\Benchmark.java
set /p args= "Input (all) arguments with proper Syntax: "
@echo on
java -classpath ".\build"  benchmark.Benchmark  %args%

@pause
