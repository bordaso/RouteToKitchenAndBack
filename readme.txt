Credits for the task goes to TopDesk!
Solution implemented by BORDASO.

== The Story ==

A robot called Rob lives on a grid. 
The grid is a dangerous place. 
There is
really only one place where Rob feels safe and that is in his room.

Unfortunately now and then Rob gets hungry and he needs a sandwich. 
The only
place he get can get one is in the kitchen. 
It leaves Rob no choice but to 
leave his room now and then and hurry to the kitchen, 
make a sandwich and 
quickly go back to his room again.
 

The grid is rectangular and divided into equally sized squares, or cells. 
Rob
can move in four directions: North, East, South and West. 
Every move takes Rob
one second. Because Rob does not like to leave his room, 
the total journey can
only take a maximum of 100 seconds.

Once Rob arrived in the
 kitchen he has to prepare the sandwich. For that
reason, he has to stay there for a
 minimum of five seconds. After that he can
start his way back home.

During his 
journey Rob must be careful not to fall off the grid. He cannot for
instance
move north if he already is in the top row of the grid. Furthermore,
the grid is 
littered with holes. Rob should not step into one of them. And
most frightening of
 all is the bug that wanders around the grid. Rob cannot
be at the same place at the
 same time the bug is. It can pop up anywhere. Its
movements are seemingly random. 
Fortunately they are predictable. For any
given time, in the past or in the future, 
the grid can predict where the bug
will be.

Except for going into one of the four directions,
 Rob can also choose to pause
one or more moves. For the five seconds that Rob has to spend 
in the kitchen he
needs to make five pausing moves. It might also be necessary to use this 

possibility to wait for the bug to leave the cell Rob wants to go to. One pause
also takes one second.
 Pauses can be used without a reason, but be careful the
bug does not show up on the cell where Rob is doing nothing!

 

== Your Task ==

Your task is to implement the Solution interface. 
It returns a list with all
instructions for Rob how to move in order
 to bring him safely from his room to
the kitchen and back again.
 It is not forbidden to visit the kitchen more than
once. Do remember that 
during at least one of the kitchen visits Rob should 
pause for at least five moves there.
 

We suggest you to read the JavaDoc carefully before you start. Especially take
care to honor 
the requirement that your implementation of the Solution 
interface should provide a 
void (no arguments) constructor. We will test your 
solution with grids for which a journey with less than 
30 moves exists, 
including the time spent in the kitchen. Your solution can use up to 100
instructions.


For convenience a helper class GridFactory is included. It is not mandatory to
use this. 
It might help you to test your own solution.  



== Example ==

The grid looks like this:
.....
.R.O.
.B.K.
.B.O.
.....

- The top left cell has coordinate (0,0)
- 
Rob's room is at coordinate (1,1)
- The kitchen is at coordinate (3,2)
- 
There are two holes; at (3,1) and at (3,3)
- 
The bug relocates after every move from (1,2) to (1,3) and back. 
When exactly
  is not relevant for this example.  

A
 valid output would be: EAST, SOUTH, EAST, PAUSE, PAUSE, PAUSE, PAUSE,
PAUSE, WEST, NORTH, WEST


The total journey in this example was 11 seconds. Keep in mind that it can be
more difficult
 if the bug happens to interfere with Rob's journey.

== Important Notes 

==

Some things to keep in mind when implementing your solution:

*
 Read the JavaDoc carefully before you start. It provides useful information.

*
 We are only interested in an implementation for the Solution interface. 
We 
  do not expect any kind of GUI, nor do we expect a class with a main method.
  
* 
We are not so much looking for a solution that performs very fast. 
If your 
  implementation finds the best solution within one minute, that is good 
  enough.
 
* 
We are interested in the readability of your code.

* Follow the instructions carefully, but keep it simple!
 Your solution might of
  course require more files (Java types), be careful however not to 
  over-engineer your solution.
  
* 
The class that implements the Solution interface should offer a void (no
  arguments) constructor.
 
* 
You are not allowed to change existing code, i.e. do not change existing 
  files.

*
 You can use the existing class YourSolution to implement your code. 
This
  class can be changed and is the only exception to the rule above.
