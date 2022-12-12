import java.io.BufferedReader;
import java.io.IOException;

public class Parser {

    BufferedReader reader;
    Command c;
    String arg1;
    int arg2;

    public Parser(BufferedReader reader) {
        this.reader = reader;
    }

    public boolean hasMoreCommands() throws IOException {
        return reader.ready();
    }

    public void advance() throws IOException {
        // String line = reader.readLine();
        String line = "add";
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

}
