import java.util.Scanner;

/** Shared console input; Main owns and closes the single Scanner. */
public class ConsoleInput {
    private Scanner scanner;
    private boolean interactive;

    public ConsoleInput(Scanner scanner, boolean interactive) {
        this.scanner = scanner;
        this.interactive = interactive;
    }

    /** Returns the original line, or null only when input has ended. */
    public String readLine(String prompt) {
        if (interactive) System.out.print(prompt);
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    /** Invalid numbers throw NumberFormatException; null means end of input. */
    public Integer readInt(String prompt) {
        String line = readLine(prompt);
        if (line == null) return null;
        return Integer.valueOf(line.trim());
    }
}
