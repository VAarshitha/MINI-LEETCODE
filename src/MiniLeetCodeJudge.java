import java.util.List;

public class MiniLeetCodeJudge {

    public static void main(String[] args) throws Exception {

        // Sample user submission code: reverse input string
        String userCode = """
            public class UserSolution {
                public static String solve(String input) {
                    return new StringBuilder(input).reverse().toString();
                }
            }
            """;

        // Define test cases
        List<TestCase> tests = List.of(
                new TestCase("hello", "olleh"),
                new TestCase("world", "dlrow"),
                new TestCase("java", "avaj")
        );

        DynamicCompilerAndRunner compiler = new DynamicCompilerAndRunner();
        Class<?> userClass = compiler.compileAndLoad(userCode);

        for (int i = 0; i < tests.size(); i++) {
            boolean passed = Judge.runTest(userClass, tests.get(i));
            System.out.println("Test #" + (i + 1) + ": " + (passed ? "PASSED" : "FAILED"));
        }
    }
}
