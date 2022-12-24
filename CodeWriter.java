import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;

/**
 * Class CodeWriter -
 * Write translated Hack VM command to it's matching code in Hack Assembly
 * language
 */
public class CodeWriter {

    // Predefined Hack Assembly code segments (divided by operations)
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

    final String setARG = "D=A\n"+
                          "@5\n"+
                          "D=D+A\n"+
                          "@SP\n"+
                          "D=M-D\n"+
                          "@ARG\n"+
                          "M=D\n";

    final String setLocal = "@SP\n"+
                            "D=M\n"+
                            "@LCL\n"+
                            "M=D\n";

    final String repositionPointer = "D=A\n" + 
                                     "@endFrame\n" +
                                     "A=M-D\n" +
                                     "D=M\n" ;

    // Class Fields
    String fileName;
    BufferedWriter writer;
    int contCounter;
    int funcCounter;
    Stack<String> funcStack;

    /**
     * Creates a new instance of CodeWriter
     * 
     * @param output file to write the translated Hack Assembly commands into
     * @throws IOException if couldn't create a new writer
     */
    public CodeWriter(File output) throws IOException {
        this.writer = new BufferedWriter(new FileWriter(output));
        this.contCounter = 0;
        this.funcCounter = 0;
        this.funcStack = new Stack<String>();
    }

    /**
     * Writes Hack Assembly code to implement a given VM language command of
     * arithmetic type
     * 
     * @param command Hack VM language command of arithmetic type
     * @throws IOException if couldn't write the line to the file
     */
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

    /**
     * Writes Hack Assembly code to implement a given VM language command of
     * push/pop type
     * 
     * @param c       Hack VM language command (essentialy push/pop)
     * @param segmant the segment in Hack memory to push/pop from/into
     * @param index   the index of the address cell location inside the given
     *                segment
     * @throws IOException if couldn't write the line to the file
     */
    public void WritePushPop(String c, String segmant, int index) throws IOException {
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
                if (c.equals("pop")) {
                    line = line + pop;
                } else
                    line = line + "A=A+D\n" + push;
                break;

            case "constant":
                def = false;
                line = "@" + index + "\nD=A" + push.substring(3);
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
            if (c.equals("pop")) {
                line = popFirst + "@" + address + "\nM=D\n";
            } else
                line = "@" + address + "\n" + push;
        }
        writer.write("//" + c + " " + segmant + " " + index + "\n" + line);
    }

    /**
     * Helper Private Function to assemble a comarison Hack VM language commands
     * 
     * @param comp the comparison to be done (in Hack Assembly language)
     * @return a full code snippet that implements the comparison Hack VM language
     *         command in Hack Assembly language
     */
    private String getCompareCommand(String comp) {
        return popFirst + popSecond + compute + contCounter + "\n" + comp + writeFalse + "@CONT" + contCounter
                + "\n0;JMP\n(TRUE" + contCounter + ")\n" + writeTrue + "(CONT" + (contCounter++)
                + ")\n@SP\nM=M+1\n";
    }
    
    /**
     * 
     * @param label
     * @throws IOException
     */
    public void writeLabel(String label) throws IOException {
        writer.write("//lable\n(" + label + ")\n");
    }

    public void writeGoTo(String label) throws IOException {
        writer.write("//goto\n@" + label + "\n0;JMP\n");
    }

    public void writeIf(String label) throws IOException {
        writer.write("//if-goto\n" + popFirst + "@" + label + "\nD;JNE\n");
    }

    public void writeFunction(String name, int nVars) throws IOException{
        writer.write("//Function" + name + nVars + "\n");
        funcStack.push(name);
        writer.write("//function label\n(" + fileName + name +")\n");
        writer.write("//Save Counter = nVars\n@" + nVars + "\nD=A\n@Counter\nM=D\n");
        writer.write("//if nVars == 0 Continue\n@CONT" + contCounter + "\nD;JEQ\n");
        writer.write("//Init Local Variables\n(LocalInit" + contCounter + ")\n@0\nD=A" + push.substring(3));
        writer.write("//Counter-- and Loop\n" + "@Counter\n"+
                                                "M=M-1\n" +
                                                "D=M\n" + 
                                                "@LocalInit" + contCounter + "\n" +
                                                "D;JGT\n" +
                                                "(CONT"+ (contCounter++) + ")\n");
    }

    public void writeCall(String name, int nArgs) throws IOException{
        writer.write("//Call" + name + nArgs + "\n");
        String returnAddr =  fileName + funcStack.peek() + "$ret." + (funcCounter++);
        writer.write("//push return address\n@" + returnAddr + "\nD=A" + push.substring(3)); //push return address
        writer.write("//push LCL\n@LCL\n" + push); //push LCL
        writer.write("//push ARG\n@ARG\n" + push); //push ARG
        writer.write("//push THIS\n@THIS\n" + push); //push THIS
        writer.write("//push THAT\n@THAT\n" + push); //push THAT
        writer.write("//Set new ARG\n@" + nArgs + "\n" + setARG); //set new ARG
        writer.write("//Set Local\n" + setLocal); //set Local 
        writer.write("//call function\n@" + fileName + name + "\n0;JMP\n" + "(" + returnAddr +")\n");
    }

    public void writeReturn() throws IOException {
        writer.write("//Return\n");
        writer.write("//save the endFrame\n" +
                     "@LCL\n" +
                     "D=M\n" +
                     "@endFrame\n" +
                     "M=D\n");
        writer.write("//compute return address\n" +
                     "D=M\n" +
                     "@5\n" +
                     "D=D-A\n" +
                     "A=D\n" +
                     "D=M\n" +
                     "@retAddress\n" +
                     "M=D\n");
        writer.write("//*ARG = pop()\n" +
                     "@SP\n" +
                     "A=M-1\n" +
                     "D=M\n" +
                     "@ARG\n" +
                     "A=M\n" +
                     "M=D\n");
        writer.write("//update SP\n" +
                     "D=A+1\n" +
                     "@SP\n" +
                     "M=D\n");
        writer.write("//reposition THAT\n@1\n" + repositionPointer + "@THAT\nM=D\n"); 
        writer.write("//reposition THIS\n@2\n" + repositionPointer + "@THIS\nM=D\n");  
        writer.write("//reposition ARG\n@3\n" + repositionPointer + "@ARG\nM=D\n");  
        writer.write("//reposition LCL\n@4\n" + repositionPointer + "@LCL\nM=D\n"); 
        writer.write("//Go-To retAddress\n" + 
                     "@retAddress\n" +
                     "A=M\n" +
                     "0;JMP\n" );  
        funcStack.pop();
    }

    public void writeBootstrapCode() throws IOException{
        writer.write("//Bootstrap Code\n@256\nD=A\n@SP\nM=D\n");
        writer.write("//Set Local\n" + setLocal); //set Local 
        writer.write("//call function\n@" + fileName + "Sys.init\n0;JMP\n");
    }

    /**
     * Adds an infinite loop (to be used at the end of the file)
     * 
     * @throws IOException if couldn't write line into the file
     */
    public void infiniteLoop() throws IOException {
        writer.write("//infinite loop\n(END)\n@END\n0;JMP");
        
    }

    /**
     * Closes the writer and writes the translation of the Hack VM script into the
     * output file
     * 
     * @throws IOException if couldn't close the writer
     */
    public void close() throws IOException {
        writer.close();
    }
}
