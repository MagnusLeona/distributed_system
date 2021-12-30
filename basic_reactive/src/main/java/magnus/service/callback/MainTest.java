package magnus.service.callback;

import javax.security.auth.callback.Callback;
import java.util.Observable;

public class MainTest {
    public static void main(String[] args) {
        Student student = new Student("小明");
        student.callHelp(1, 2);

        Observable observable = new Observable();
    }
}
