package magnus.service.callback;

public class Student{
    private String name;

    Student(String name) {
        this.name = name;
    }

    public void callHelp(int a, int b) {
        new Calculator().add(a, b, (a1, b1, result) -> {
            System.out.println(name + "获取 " + a + " + " + b + " 的结果为：" + result);
        });
    }
}
