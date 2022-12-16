import java.io.File;
import java.io.IOException;

public class VMTranslator {

    public static void main(String[] args) {
        try {
            File source = new File(args[0]);
            File output = new File(args[0].substring(0, args[0].length() - 2) + "asm");
            Parser parser = new Parser(source);
            CodeWriter writer = new CodeWriter(output);
            while (parser.hasMoreCommands()) {
                parser.advance();
                if (parser.commandType() == Command.C_ARITHMETIC) {
                    writer.WriteArithmetic(parser.arg1());
                } else {
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
                            break;
                    }
                    writer.WritePushPop(parser.commandType(), segment, parser.arg2());
                }
            }
            writer.infiniteLoop();
            writer.close();
            parser.close();
        } catch (IOException e) {
            System.out.println("zdayen in did");
        }
    }
}
