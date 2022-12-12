import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

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

    final String push = "A=A+D\n" +
            "D=M\n" +
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

    BufferedWriter writer;

    public CodeWriter(BufferedWriter writer) {
        this.writer = writer;
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
        String line = "@" + segmant + "\nD=M\n@" + index + "\n";
        if (c == Command.C_POP) {
            line = line + pop;
        } else
            line = line + push;
        writer.write(line);
    }
}
