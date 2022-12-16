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

    final String incrementSP = "@SP\nM=M+1\n";

    final String negate = "M=-M\n";
    final String not = "M=!M\n";

    final String compute = "D=M-D\n" +
            "@TRUE";
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
            "M=D\n";
    final String writeFalse = "@0\n" +
            "D=A\n" +
            "@SP\n" +
            "A=M\n" +
            "M=D\n";

    final String writeTrue = "@1\n" +
            "A=-A\n" +
            "D=A\n" +
            "@SP\n" +
            "A=M\n" +
            "M=D\n";

    final String lt = "D;JLT\n";
    final String eq = "D;JEQ\n";
    final String gt = "D;JGT\n";

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

    int contCounter;

    public CodeWriter(File output) throws IOException {
        this.fileName = output.getName().substring(0, output.getName().length() - 3);
        this.writer = new BufferedWriter(new FileWriter(output));
        this.contCounter = 0;
    }

    public void WriteArithmetic(String command) throws IOException {
        switch (command) {
            case "add":
                writer.write("//add\n" + popFirst + popSecond + add + incrementSP);
                break;
            case "sub":
                writer.write("//sub\n" + popFirst + popSecond + sub + incrementSP);
                break;
            case "neg":
                writer.write("//neg\n" + popFirst + negate + incrementSP);
                break;
            case "eq":
                writer.write("//eq\n" + getCompareCommand(eq));
                break;
            case "gt":
                writer.write("//gt\n" + getCompareCommand(gt));
                break;
            case "lt":
                writer.write("//lt\n" + getCompareCommand(lt));
                break;
            case "and":
                writer.write("//and\n" + popFirst + popSecond + and + incrementSP);
                break;
            case "or":
                writer.write("//or\n" + popFirst + popSecond + or + incrementSP);
                break;
            case "not":
                writer.write("//not\n" + popFirst + not + incrementSP);
                break;
        }
    }

    public void WritePushPop(Command c, String segmant, int index) throws IOException {
        String line = "";
        String address = "";
        boolean def = true;
        switch (segmant) {
            case "LCL":
            case "ARG":
            case "THIS":
            case "THAT":
                def = false;
                line = "@" + segmant + "\nD=M\n@" + index + "\n";
                if (c == Command.C_POP) {
                    line = line + pop;
                } else
                    line = line + "A=A+D\n" + push;
                break;

            case "constant":
                def = false;
                line = "@" + index + "\n" + "D=A" + push.substring(3);
                break;

            case "static":
                address = fileName + index;
                break;

            case "temp":
                address = "" + (5 + index);
                break;

            case "pointer":
                address = (index == 0) ? "THIS" : "THAT";
                break;
        }
        if (def) {
            if (c == Command.C_POP) {
                line = popFirst + "@" + address + "\nM=D\n";
            } else
                line = "@" + address + "\n" + push;
        }
        writer.write("//" + c + " " + segmant + " " + index + "\n" + line);
    }

    private String getCompareCommand(String comp) {
        return popFirst + popSecond + compute + contCounter + "\n" + comp + writeFalse + "@CONT" + contCounter
                + "\n0;JMP\n(TRUE" + contCounter + ")\n" + writeTrue + "(CONT" + (contCounter++)
                + ")\n@SP\nM=M+1\n";
    }

    public void infiniteLoop() throws IOException {
        writer.write("(END)\n@END\n0;JMP");
    }

    public void close() throws IOException {
        writer.close();
    }
}
