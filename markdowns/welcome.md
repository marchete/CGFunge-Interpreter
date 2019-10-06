# CG-Funge Batch Runner

This Java playground can help you to make faster tests on [CGFunge Primer puzzle](https://www.codingame.com/ide/puzzle/cgfunge-prime) 
It works by using the same code to a list of validators, giving you the number of valid tests, invalid ones, code coverage and a highlight of the first invalid execution.
It's easier to track down problems for me, as you can see with a color what part of the code was executed. If you copy it locally to your PC you can also pass a number parameter to see that execution path and not the first error.

It has some slight differences. First, you should not add the line count at start, my parser only takes a raw code. Besides that there is an additional instruccion 'Q', that ends the program but also prints the whole stack.

I worked on this puzzle in the following way:

* I've created an [ExcelTemplate.xlsx](https://github.com/marchete/CGFunge-Interpreter/raw/master/java-project/src/main/java/ExcelTemplate.xlsx), and I code the solution there. Each cell is one instruction, and it's better to copy and paste, move, transpose, etc. That excel template has conditional formatting and helps you with empty spaces. If you forget an empty space it shows a red cell, always put an space instead of empty spaces.
* Once I want to test the code I copy all and paste it to a plain text file called code.php
* I have a file called validators.txt with all numbers I want to test. You can use the numbers you like.
* Then I run the interpreter, and I got the valid tests, invalid tests, total steps and a code coverage.
* Once I have a correct solution, I remove tabs from code.php, add the number of line as the first line (it's needed on the original puzzle), and I submit it.

# Interpreter

@[CG Funge Interpreter]({"stubs": ["src/main/java/code.php","src/main/java/CGFunge.java","src/main/java/validators.txt"], "command": "CGFunge"})

