
javac -source 7 -target 7 -d "./build" ./src/project/pathfinding/standard/RoutePlanner.java
javac -source 7 -target 7 -classpath "./build" -d "./build" ./src/project/pathfinding/standard/Main.java
echo "Input graph path (without -graph): "
read args 
java -classpath "./build"  standard.Main  -graph $args

read $SHELL
