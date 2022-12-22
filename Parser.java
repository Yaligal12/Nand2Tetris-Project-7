import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Class Parser -
 * Parses each line of a given source Hack VM language file
 */
public class Parser {

    private BufferedReader reader;
    private Command c;
    private String arg1;
    private int arg2;

    /**
     * Creates a new instance of Parser
     * 
     * @param source .vm file to read from
     * @throws IOException if couldn't open new reader from file
     */
    public Parser(File source) throws IOException {
        this.reader = new BufferedReader(new FileReader(source));
    }

    /**
     * Checks if source file still has more lines to read
     * 
     * @return true if still has more lines to read, false otherwise
     * @throws IOException if a problem with reader occurs
     */
    public boolean hasMoreCommands() throws IOException {
        return reader.ready();
    }

    /**
     * Reads next line from the source file and parses it to the 3 components
     * that builds the Virtual Machine commands
     * 
     * @throws IOException if reader couldn't read next line from file
     */
    public void advance() throws IOException {
        String line = reader.readLine();
        String[] arr = line.trim().replaceAll("( )+", " ").split(" "); // Split the string to it's whitespace seperated
                                                                       // components

        switch (arr[0]) {
            case "push":
                c = Command.C_PUSH;
                arg1 = arr[1];
                arg2 = Integer.parseInt(arr[2]);
                break;
            case "pop":
                c = Command.C_POP;
                arg1 = arr[1];
                arg2 = Integer.parseInt(arr[2]);
                break;
            default:
                c = Command.C_ARITHMETIC;
                arg1 = arr[0];
        }
    }

    /**
     * Returns the current VM command type
     * 
     * @return Command instance representing the current VM command type
     */
    public Command commandType() {
        return c;
    }

    /**
     * Returns the current VM command first argument
     * 
     * @return the first argument of the VM command (segment or arithmetic
     *         operation)
     */
    public String arg1() {
        return arg1;
    }

    /**
     * Returns the current VM command second argument
     * or NULL if not exist
     * (Essentially the index of a push/pop VM command)
     * 
     * @return the second argument of the VM command (Or NULL if not exist)
     */
    public int arg2() {
        return arg2;
    }

    /**
     * Closes the File Reader
     * 
     * @throws IOException if couldn't close the reader
     */
    public void close() throws IOException {
        reader.close();
    }
}
