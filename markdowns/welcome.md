# CG-Funge Batch Runner

This Java playground can help you to make fast tests on CG-Funge optimization puzzle [](https://www.codingame.com/ide/puzzle/cgfunge-prime) 

It has some slight differences. First, you should not print in your code the line number, my parser only takes the code. Besides that there is an additional instruccion 'Q', that ends the program but also prints the whole stack.

I worked on this puzzle in the following way:

* I created an Excel template that is where I code the solution. That excel template has conditional formatting and helps you with empty spaces. If you forget an empty space it shows a red cell. Excel is also better for move instructions, transpose it, etc. Once I want to test the code I passed it to a plain text file called code.php
* I have a file called validators.txt with all numbers I want to test. You can use the numbers you like.
* Then I run the interpreter, and I got the valid tests, invalid tests and a coverage of the items. Coverage is useful to know how many times you run an instruction for the validators.

# Interpreter

@[CG Funge Interpreter]({"stubs": ["src/test/java/com/CGFunge/code.php","src/test/java/com/CGFunge/CGFunge.java","src/test/java/com/CGFunge/validators.txt"], "command": "javac CGFunge.java && java CGFunge"})

