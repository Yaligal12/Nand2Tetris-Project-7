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
            File output;
            File[] filesArray;
            
            // Initialize a new CodeWriter and Parser to handle writing the translation of VM commands 
            CodeWriter writer = null;
            Parser parser = null;
            
            if(source.isDirectory()){ 
                filesArray = source.listFiles();
                output = new File(args[0] + "/" + source.getName() + ".asm");
                writer = new CodeWriter(output);
                writer.writeBootstrapCode();
            }
            else {
                filesArray = new File[1];
                filesArray[0] = source;
                output = new File(args[0].substring(0, args[0].length() - 2) + "asm");
                writer = new CodeWriter(output);
            }

            

            // Main loop (essentialy reads next command and writes it's translation to the
            // output file)
            for(File f : filesArray){
                //Create a new Parser to handle reading and parsing the VM commands of file f in the given source (file/directory)
                parser = new Parser(f);
                writer.fileName = f.getName().substring(0,f.getName().length()-2);
                if(!f.getName().endsWith(".vm")) continue;
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
                        case "function":
                            writer.writeFunction(parser.arg1(), parser.arg2());
                            break;
                        case "call":
                            writer.writeCall(parser.arg1(), parser.arg2());
                            break;
                        case "return":
                            writer.writeReturn();
                            break;
                        case "comment":
                            break;
                    }
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

    public static File[] getVMFiles(File dir) {
        File[] temp = dir.listFiles();
        File[] files = new File[temp.length];
        int counter = 1;
        files[0] = new File(dir.getPath() + "/Sys.vm");
        for (File f : temp) {
            if(f.getName().endsWith(".vm") && !f.getName().equals("Sys.vm")){
                files[counter++] = f; 
            }
        }
        File[] finalFilesArr = new File[counter];
        for(int i = 0; i < counter; i++){
            finalFilesArr[i] = files[i];
        }
        return finalFilesArr;
    }
}
