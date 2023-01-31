# ProgrammierProjekt
##Benchmarking

Run buildBenchmark and input arguments as such: 
-graph (Path of graph file)  -lon 9.098  -lat 48.746  -que (Path of que (test) file)  -s 638394 

.sol file is checked automatically. Must be in the same directory as the .que file

##Regular

Run buildNormal (further steps will be obvious)


##Server

Run the Server compilation script (requires maven) and then the run script. (Jar with dependencies must be present in target folder, i.e. compile first)

Input Path of Graph file.
Server will listen to requests on localhost:8080.
(Server will continue running even if the command window is closed, so it will have to be shut down manually)
