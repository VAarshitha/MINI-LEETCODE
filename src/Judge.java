import java.lang.reflect.Method;

public class Judge {

    public static boolean runTest(Class<?> userClass, TestCase testCase) throws Exception {
        Method solveMethod = userClass.getMethod("solve", String.class);
        Object output = solveMethod.invoke(null, testCase.getInput());
        return output.toString().equals(testCase.getExpectedOutput());
    }
}
