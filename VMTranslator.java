import java.io.File;
import java.io.IOException;

/**
 * 
 */
public class VMTranslator {
    /**
     * Runs the process of translation of a VM script file to Hack Assembly program
     * that implements the VM file logic
     */
    public static void main(String[] args) {
        try {
            // Create a new source file (from the given source file argument) and a new
            // output file to write to
            File source = new File(args[0]);
            File output = new File(args[0].substring(0, args[0].length() - 2) + "asm");

            // Create a new Parser and CodeWriter to handle reading and writing VM commands
            // and their translation
            Parser parser = new Parser(source);
            CodeWriter writer = new CodeWriter(output);

            // Main loop (essentialy reads next command and writes it's translation to the
            // output file)
            while (parser.hasMoreCommands()) {
                parser.advance();
                switch (parser.commandType()){
                    case "arithmetic":
                        writer.WriteArithmetic(parser.arg1());
                        break;
                    case "pop":
                    case "push":
                        String segment = parser.arg1();
                        switch (segment) {
                            case "local":
                                segment = "LCL";
                                break;
                            case "argument":
                                segment = "ARG";
                                break;
                            case "this":
                                segment = "THIS";
                                break;
                            case "that":
                                segment = "THAT";
                                break; }
                        writer.WritePushPop(parser.commandType(), segment, parser.arg2());
                        break;
                    case "label":
                        writer.writeLabel(parser.arg1());
                        break;
                    case "goto":
                        writer.writeGoTo(parser.arg1());
                        break;
                    case "if-goto":
                        writer.writeIf(parser.arg1());
                        break;
                    case "comment":
                        break;
                }
            }

            // Add an infinite loop at the end of the script and close the streams
            writer.infiniteLoop();
            writer.close();
            parser.close();
        } catch (IOException e) {
            // Problem with input or output files
            e.printStackTrace();
        }
    }
}
