package me.nanjingchj.test.turingmachine;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TuringMachine implements IMachine {
    private final List<IConfigurationEntry> configurationEntries = new ArrayList<>();

    @Override
    public void addConfigurationEntry(IConfigurationEntry entry) {
        configurationEntries.add(entry);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public ICompiledMachine compile() {
        Random random = new Random();
        String id = "C" + String.valueOf(Math.abs(random.nextLong()));
        String code =
                """
                        import java.util.Arrays;
                                                
                        public class ClassName implements Runnable {
                            private final char[] tape = new char[Short.MAX_VALUE];
                            private int head = 0;
                            private char mConfig;
                            
                            public ClassName() {}
                                                                                                                 
                            @Override
                            public void run() {
                                Arrays.fill(tape, ' ');
                                char symbol = tape[head];
                                // CODE
                            }                        
                        }
                                                                             """.replaceAll("ClassName", id);
        String[] configurationCode = new String[configurationEntries.size()];
        for (int i = 0; i < configurationEntries.size(); i++) {
            IConfigurationEntry entry = configurationEntries.get(i);
            StringBuilder codeForEntry = new StringBuilder();
            codeForEntry.append("if (head == '").append(entry.getSymbol()).append("' && mConfig == '").append(entry.getMConfiguration().toChar()).append("') {\n");
            // code the operations
            for (IOperation operation : entry.getOperations()) {
                String operationCode = operation.getOperationCode();
                switch (operationCode.charAt(0)) {
                    case 'L' -> codeForEntry.append("\t\t\thead--;\n");
                    case 'R' -> codeForEntry.append("\t\t\thead++;\n");
                    case 'P' -> codeForEntry.append("\t\t\ttape[head] = '").append(operationCode.charAt(1)).append("';\n");
                }
            }
            codeForEntry.append("\t\t\tmConfig = '").append(entry.getFinalMConfiguration().toChar()).append("';");
            codeForEntry.append("\n\t\t}");
            configurationCode[i] = codeForEntry.toString();
        }
        StringBuilder processedCode = new StringBuilder();
        for (int i = 0; i < configurationCode.length - 1; i++) {
            processedCode.append(configurationCode[i]).append(" else ");
        }
        processedCode.append(configurationCode[configurationCode.length - 1]).append(" else {\n\t\t\treturn;\n\t\t}");
        code = code.replace("// CODE", processedCode.toString());

        System.out.println(code);
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        File tempSource = new File(System.getProperty("user.dir") + "/temp/" + id + ".java");
        try {
            boolean ignored = tempSource.createNewFile();

            FileOutputStream fos = new FileOutputStream(tempSource);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(code);
            writer.flush();
            writer.close();
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        try {
            Process compileProcess = Runtime.getRuntime().exec("javac -d " + System.getProperty("user.dir") + "/temp/ " + System.getProperty("user.dir") + "/temp/" + id + ".java");
            compileProcess.waitFor();
            ClassLoader loader = new URLClassLoader(new URL[]{new URL("file://" + System.getProperty("user.dir") + "/temp/")});
            Class<?> myClass = loader.loadClass(id);
            return new CompiledTuringMachine((Runnable) myClass.getConstructors()[0].newInstance());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            tempSource.delete();
            new File("temp/" + id + ".class").delete();
        }
    }

    public static class CompiledTuringMachine implements ICompiledMachine {
        private final Runnable runnable;

        public CompiledTuringMachine(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void execute() {
            runnable.run();
        }
    }

    public static class ConfigurationEntry implements IConfigurationEntry {
        private final IMConfiguration mConfig;
        private final char symbol;
        private final IOperation[] operations;
        private final IMConfiguration finalMConfig;

        public ConfigurationEntry(IMConfiguration mConfig, char symbol, IOperation[] operations, IMConfiguration finalMConfig) {
            this.mConfig = mConfig;
            this.symbol = symbol;
            this.operations = operations;
            this.finalMConfig = finalMConfig;
        }

        @Override
        public IMConfiguration getMConfiguration() {
            return mConfig;
        }

        @Override
        public char getSymbol() {
            return symbol;
        }

        @Override
        public IOperation[] getOperations() {
            return operations;
        }

        @Override
        public IMConfiguration getFinalMConfiguration() {
            return finalMConfig;
        }
    }

    public static class MConfiguration implements IMConfiguration {
        private final char config;

        public MConfiguration(char c) {
            this.config = c;
        }

        @Override
        public char toChar() {
            return config;
        }

        @Override
        public String toString() {
            return String.valueOf(toChar());
        }
    }

    public static class Operation implements IOperation {
        private final String code;

        public Operation(String code) {
            this.code = code;
        }

        @Override
        public String getOperationCode() {
            return code;
        }
    }
}
