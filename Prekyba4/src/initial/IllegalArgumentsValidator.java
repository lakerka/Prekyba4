package initial;

import java.util.Arrays;
import java.util.List;

public class IllegalArgumentsValidator {

    public static void checkNullPointerPassed(Object... targets) {

        for (Object target : targets) {

            if (target == null) {
                throw new IllegalArgumentException(
                        "Arguments must not be null!");
            }
        }
    }

    public static void checkNotPositive(Integer target, String variableName) {

        if (target <= 0) {
            throw new IllegalArgumentException(variableName
                    + " must be positive!");
        }
    }

}
