package magnus.service.callback;

public class Calculator {
    public void add(int a, int b, DoCalculator doCalculator) {
        doCalculator.fillBlank(a, b, a + b);
    }
}
