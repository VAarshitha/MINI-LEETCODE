import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class MemoryJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    private final Map<String, ByteArrayJavaClass> classFileObjects = new HashMap<>();

    protected MemoryJavaFileManager(JavaFileManager fileManager) {
        super(fileManager);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className,
                                               JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        ByteArrayJavaClass fileObject = new ByteArrayJavaClass(className, kind);
        classFileObjects.put(className, fileObject);
        return fileObject;
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        return new MemoryClassLoader(classFileObjects);
    }

    // Inner class representing compiled class in byte array
    static class ByteArrayJavaClass extends SimpleJavaFileObject {
        private ByteArrayOutputStream baos = new ByteArrayOutputStream();

        protected ByteArrayJavaClass(String name, Kind kind) {
            super(URI.create("bytes:///" + name.replace('.', '/') + kind.extension), kind);
        }

        @Override
        public ByteArrayOutputStream openOutputStream() {
            return baos;
        }

        byte[] getBytes() {
            return baos.toByteArray();
        }
    }

    // ClassLoader loading compiled classes from memory
    static class MemoryClassLoader extends ClassLoader {
        private final Map<String, ByteArrayJavaClass> classes;

        MemoryClassLoader(Map<String, ByteArrayJavaClass> classes) {
            super(ClassLoader.getSystemClassLoader());
            this.classes = classes;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            ByteArrayJavaClass fileObject = classes.get(name);
            if (fileObject == null) {
                return super.findClass(name);
            }
            byte[] bytes = fileObject.getBytes();
            return defineClass(name, bytes, 0, bytes.length);
        }
    }
}
