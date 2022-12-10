@echo off
javac -target 7 -d ".\build" .\src\standard\RoutePlanner.java
javac -target 7 -classpath ".\build" -d ".\build" .\src\standard\Main.java
set /p args= "Input graph path (without -graph): "
@echo on
java -classpath ".\build"  standard.Main  -graph %args%

@pause
