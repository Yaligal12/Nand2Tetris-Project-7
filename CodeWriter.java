import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter {
    final String popFirst = "@SP\n" +
            "M=M-1\n" +
            "A=M\n" +
            "D=M\n";

    final String popSecond = "@SP\n" +
            "M=M-1\n" +
            "A=M\n";

    final String add = "M=M+D\n";
    final String sub = "M=M-D\n";
    final String and = "M=M&D\n";
    final String or = "M=M|D\n";

    final String negate = "M=-M\n";
    final String not = "M=!M\n";

    final String compute = "D=M-D\n" +
            "@TRUE\n";
    final String writeTrueFalse = "@0\n" +
            "D=A\n" +
            "@SP\n" +
            "A=M\n" +
            "M=D\n" +
            "@CONT\n" +
            "0;JMP\n" +
            "(TRUE)\n" +
            "@1\n" +
            "D=A\n" +
            "@SP\n" +
            "A=M\n" +
            "M=D\n" +
            "(CONT)\n" +
            "@SP\n" +
            "M=M+1\n";

    final String lt = compute + "D;JLT\n" + writeTrueFalse;
    final String eq = compute + "D;JEQ\n" + writeTrueFalse;
    final String gt = compute + "D;JGT\n" + writeTrueFalse;

    final String push = "D=M\n" +
            "@SP\n" +
            "A=M\n" +
            "M=D\n" +
            "@SP\n" +
            "M=M+1\n";

    final String pop = "D=D+A\n" +
            "@addr\n" +
            "M=D\n" +
            "@SP\n" +
            "M=M-1\n" +
            "A=M\n" +
            "D=M\n" +
            "@addr\n" +
            "A=M\n" +
            "M=D\n";

    String fileName;
    BufferedWriter writer;

    public CodeWriter(File output) throws IOException {
        this.fileName = output.getName().substring(0, output.getName().length() - 3);
        this.writer = new BufferedWriter(new FileWriter(output));
    }

    public void WriteArithmetic(String command) throws IOException {
        switch (command) {
            case "add":
                writer.write(popFirst + popSecond + add);
                break;
            case "sub":
                writer.write(popFirst + popSecond + sub);
                break;
            case "neg":
                writer.write(popFirst + negate);
                break;
            case "eq":
                writer.write(popFirst + popSecond + eq);
                break;
            case "gt":
                writer.write(popFirst + popSecond + gt);
                break;
            case "lt":
                writer.write(popFirst + popSecond + lt);
                break;
            case "and":
                writer.write(popFirst + popSecond + and);
                break;
            case "or":
                writer.write(popFirst + popSecond + or);
                break;
            case "not":
                writer.write(popFirst + not);
                break;
        }
    }

    public void WritePushPop(Command c, String segmant, int index) throws IOException {
        String line = "";
        String address = "";
        boolean bool = true;
        switch (segmant) {
            case "POINTER":
                segmant = (index == 0) ? "THIS" : "THAT";
            case "LCL":
            case "ARG":
            case "THIS":
            case "THAT":
                line = "@" + segmant + "\nD=M\n@" + index + "\n"; // LCL ARG THIS THAT
                if (c == Command.C_POP) {
                    line = line + pop;
                } else
                    line = line + "A=A+D\n" + push;
                break;
            case "STATIC":
                if (c == Command.C_POP) {
                    line = popFirst + "@" + fileName + index + "\nM=D\n";
                } else
                    line = "@" + fileName + index + "\n" + push;
                break;
            case "TEMP":
                if (c == Command.C_POP) {
                    line = popFirst + "@" + (5 + index) + "\nM=D\n";
                } else
                    line = "@" + (5 + index) + "\n" + push;
                break;
            case "CONSTANT":
                line = "@" + index + "\n" + "D=A" + push.substring(3);
                break;
        }
        writer.write(line);
        writer.close();
    }
}
