JValuer
=======
Java lib for running and checking solutions

JChecker
--------
Java program for testing a lot of tests automatically.

Usage: ``java -jar JChecker.jar [<options>]``

Where options are:
* `-time <time-limit>` - time limit, terminate after <time-limit> seconds, you can add "ms" (without quotes) after the number to specify
* `-memory <memory-limit>` - memory limit, terminate if working set of the process exceeds <mem-limit> bytes, you can add K or M to specify
* `-in <input>` - sets input stream, from file or stdin
* `-out <output>` - sets output stream, from file or stdout
* `-exe <file>` - sets executable solution location
* `-folder <file>` - specify folder with tests
* `-files` - program will ask you for in and out, if needed
* `-silent` - program will not ask you for time and memory limits``
  
All important parameters that you didn't specify in command line program ask you later.
JChecker checks all tests which it can find. Program search tests with the following patterns:
* `test.i - test.o`
* `test.in - test.out`
* `test - test.a`
* `test.in.num - test.out.num`

If you need more patterns, you can add issue.
