import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Parser {

    private BufferedReader reader;
    private Command c;
    private String arg1;
    private int arg2;

    public Parser(File source) throws IOException {
        this.reader = new BufferedReader(new FileReader(source));
    }

    public boolean hasMoreCommands() throws IOException {
        return reader.ready();
    }

    public void advance() throws IOException {
        String line = reader.readLine();
        String[] arr = line.trim().replaceAll("( )+", " ").split(" ");
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

    public Command commandType() {
        return c;
    }

    public String arg1() {
        return arg1;
    }

    public int arg2() {
        return arg2;
    }

    public void close() throws IOException {
        reader.close();
    }
}
