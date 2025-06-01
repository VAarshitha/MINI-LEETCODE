import javax.tools.*;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Locale;

    public class DynamicCompilerAndRunner {

        private static final String CLASS_NAME = "UserSolution";

        // Compile user source code and load class dynamically
        public Class<?> compileAndLoad(String sourceCode) throws Exception {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                throw new IllegalStateException("Java Compiler not available. Are you running on a JRE instead of JDK?");
            }

            SimpleJavaFileObject fileObject = new JavaSourceFromString(CLASS_NAME, sourceCode);
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

            StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(diagnostics, null, null);
            MemoryJavaFileManager fileManager = new MemoryJavaFileManager(standardFileManager);

            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, Arrays.asList(fileObject));
            boolean success = task.call();

            if (!success) {
                StringBuilder errorMsg = new StringBuilder();
                for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
                    errorMsg.append("Error on line ").append(diagnostic.getLineNumber())
                            .append(": ").append(diagnostic.getMessage(Locale.ENGLISH)).append("\n");
                }
                throw new RuntimeException("Compilation failed:\n" + errorMsg);
            }

            ClassLoader classLoader = fileManager.getClassLoader(null);
            return classLoader.loadClass(CLASS_NAME);
        }

        // Helper class holding source code in memory
        static class JavaSourceFromString extends SimpleJavaFileObject {
            final String code;

            JavaSourceFromString(String name, String code) {
                super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
                this.code = code;
            }

            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return code;
            }
        }
    }



