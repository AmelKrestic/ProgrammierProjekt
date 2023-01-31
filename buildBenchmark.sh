
javac -source 15 -target 15 -classpath "./build" -d "./build" ./src/project/pathfinding/standard/RoutePlanner.java
javac -source 15 -target 15 -classpath "./build" -d "./build" ./src/project/pathfinding/benchmark/Benchmark.java 
echo "Input (all) arguments with proper Syntax: "
read args
java -classpath ".\build"  benchmark.Benchmark  $args
$SHELL
