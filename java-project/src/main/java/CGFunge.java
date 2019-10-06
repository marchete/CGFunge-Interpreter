import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

class Interpreter {
	private final int[] dx = { 1, 0, -1, 0 };
	private final int[] dy = { 0, 1, 0, -1 };
	private char[][] grid;
	private boolean[][] marked;
	private static boolean[][] coverage = new boolean[40][30];
	private int width;
	private int height;

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";
	public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
	public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
	public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
	public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
	public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
	public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
	public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
	public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

	public Stack<Integer> stack = new Stack<>();
	private int x = 0;
	private int y = 0;
	private int dir = 0;
	private boolean finished = false;
	private boolean quoted = false;
	private boolean skip = false;
	private String output = "";

	public Interpreter(String input) {
		stack.push(Integer.parseInt(input));
	}

	public void setCode(List<String> code) {
		width = 0;
		height = code.size();
		for (int i = 0; i < height; i++) { //Remove tabs that was added in Excel
			code.set(i, code.get(i).replace("\t", ""));
		}		
		for (int i = 0; i < height; i++) {
			width = Math.max(width, code.get(i).length());
		}

		grid = new char[width][height];
		marked = new boolean[width][height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				grid[x][y] = ' ';
				marked[x][y] = false;
				if (x < code.get(y).length())
					grid[x][y] = code.get(y).charAt(x);
			}
		}
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isFinished() {
		return finished;
	}

	public void printMarked() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				System.out.print((marked[x][y] ? ANSI_GREEN_BACKGROUND : ANSI_WHITE_BACKGROUND) + grid[x][y]
						+ ANSI_WHITE_BACKGROUND);
			}
			System.out.println();
		}
	}

	public void printCoverage() {
	    System.out.println(ANSI_CYAN_BACKGROUND+"****************COVERAGE****************"+ANSI_WHITE_BACKGROUND);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				System.out.print((coverage[x][y] ? ANSI_YELLOW_BACKGROUND : ANSI_WHITE_BACKGROUND) + grid[x][y]
						+ ANSI_WHITE_BACKGROUND);
			}
			System.out.println();
		}

		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				coverage[x][y] = false;

	}

	public String getOutput() {
		return output;
	}

	public boolean outOfRange() {
		return x < 0 || x >= width || y < 0 || y >= height;
	}

	private void movePointer() {
		marked[x][y] = true;
		coverage[x][y] = true;
		x += dx[dir];
		y += dy[dir];
	}

	public void step() {
		handleAction();
		movePointer();
	}

	private int doPop() {
		if (stack.size() == 0) {
			System.err.println("Stack is empty at: " + x + "," + y);
		}
		return stack.pop();
	}

	public void handleAction() {
		if (skip) {
			skip = false;
			return;
		}

		char c = grid[x][y];
		if (c == '"') {
			quoted = !quoted;
			return;
		}
		if (quoted) {
			stack.push((int) c);
			return;
		}

		if (c == '+') {
			stack.push(doPop() + doPop());
		} else if (c == '-') {
			stack.push(-doPop() + doPop());
		} else if (c == '*') {
			stack.push(doPop() * doPop());
		} else if (c == '/') {
			int v1 = doPop();
			int v2 = doPop();
			stack.push(v2 / v1);
		} else if (c >= '0' && c <= '9') {
			stack.push(c - '0');
		} else if (c == 'Q') {

			if (stack.size() > 0) {
				output += " Stack";
				for (int i = stack.size() - 1; i >= 0; --i)
					output += "'" + stack.get(i) + "',";
				output += " ";
			}
			finished = true;
		} else if (c == 'I') {
			output += "'" + doPop() + "'";
		} else if (c == 'C') {
			output += (char) doPop();
		} else if (c == '>')
			dir = 0;
		else if (c == 'v')
			dir = 1;
		else if (c == '<')
			dir = 2;
		else if (c == '^')
			dir = 3;
		else if (c == ':') {
			int v = doPop();
			if (v < 0)
				dir = (dir + 3) % 4;
			if (v > 0)
				dir = (dir + 1) % 4;
		} else if (c == 'P') {
			doPop();
		} else if (c == 'D') {
			stack.push(stack.peek());
		} else if (c == 'X') {
			int index = doPop();
			int value = stack.get(stack.size() - 1 - index);
			stack.remove(stack.size() - 1 - index);
			stack.push(value);
		} else if (c == 'S')
			skip = true;
		else if (c == 'E')
			finished = true;
	}
}

public class CGFunge {

	private static boolean isPrime(int n) {
		if (n < 2)
			return false;
		for (int i = 2; i * i <= n; i++) {
			if (n % i == 0)
				return false;
		}
		return true;
	}

	final int MAX_HEIGHT = 30;
	final int MAX_WIDTH = 40;
	private static Interpreter interpreter;

	static List<String> validators; // from validators.txt

	public static void main(String[] args) {
		int Wrong = 999;	    
		try {
		    String validatorsPath = "./src/main/java/validators.txt";
		    String codePath = "./src/main/java/code.php"; 
			validators = Files.readAllLines(Paths.get(validatorsPath), StandardCharsets.UTF_8);
			ArrayList<Integer> failed = new ArrayList<Integer>();
			int trackNumber = -1;
			if (args.length > 0)
				trackNumber = Integer.parseInt(args[0]);
			long lastChange = 0;

			//while (true)   //Continuous monitoring, local use
			for (int LOOPS =0; LOOPS<1;++LOOPS)
			{
				File file = new File(codePath);
				int Correct = 0;
				Wrong = 0;
				int TotalSteps = 0;
				int LastWrong = 0;
				int i = 1;
				if (file.lastModified() == lastChange) { //Continuous monitoring, local use
					Thread.sleep(500);					
					continue;
				}
				failed.clear();
				lastChange = file.lastModified();
				file = null;
				try {
					List<String> CODE = Files.readAllLines(Paths.get(codePath), StandardCharsets.UTF_8);// charset.forName("ISO-8859-1")));
					for (String validator : validators) {
						i = Integer.parseInt(validator);
						String Solution = (isPrime(i) ? "" : "NOT ") + "PRIME";
						if (i == trackNumber) {
							System.out.println("==============================================");
							System.out.print("N:" + trackNumber + " " + Solution + "  ");
						}
						interpreter = new Interpreter("" + i);
						interpreter.setCode(CODE);
						int steps = 0;
						while (!interpreter.isFinished()) {

							if (++steps >= 3000) {
								// Flush Stack
								if (i == trackNumber) {
									System.err.print("TOO MANY STEPS!: Stack " + interpreter.stack.size() + " is:");
									while (interpreter.stack.size() > 0) {
										System.err.print("'" + interpreter.stack.pop() + "',");
									}
								}
								break;
							}
							interpreter.step();
						}
						TotalSteps += steps;
						if (interpreter.getOutput().equals(Solution))
							Correct++;
						else {
							LastWrong = i;
							if (Wrong ==0)
							{
								trackNumber = i; //Print first wrong element
							}
							Wrong++;
							failed.add(i);
						}
						if (i == trackNumber) {
							System.out.println("***Validator:"+i+" Steps:" + steps+" ********* OUTPUT: " + interpreter.getOutput());
							interpreter.printMarked();
						}
						if (validator == validators.get(validators.size() - 1))
						{
						    System.out.println("");
							interpreter.printCoverage();
						}
					}
				} catch (EmptyStackException St) {
					failed.add(i);
					System.err.println("ERROR: Stack is empty at: " + interpreter.getX() + "," + interpreter.getY());
					interpreter.printMarked();
				} catch (Exception ex) {
					failed.add(i);
					System.err.println("*********FAILED AT INDEX : " + i);
					ex.printStackTrace();
					interpreter.printMarked();
				}

				System.out.println("===> Correct:" + Correct + " Wrong:" + Wrong
						+ (Wrong > 0 ? " Last Incorrect Validator: " + LastWrong : "") + " Total Steps:" + TotalSteps);
				System.out.print("Validator Errors: ");
				for (int f : failed) {
					System.out.print(f + ",");
				}
				System.out.println();

			}
		} catch (IOException e) {
			e.printStackTrace();
			++Wrong;
		}catch (InterruptedException e) {
			e.printStackTrace();
			++Wrong;
		}
		if (Wrong == 0)
			     System.out.println("TECHIO> success true");
			else System.out.println("TECHIO> success false");
	}
}