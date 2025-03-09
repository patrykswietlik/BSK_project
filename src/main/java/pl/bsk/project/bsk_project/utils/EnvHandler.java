package pl.bsk.project.bsk_project.utils;

import java.util.Objects;

public class EnvHandler {

    public static String getSystemEnv(String env) {
        final String result = System.getenv(env);

        if (Objects.isNull(result)) {
            throw new IllegalArgumentException("No env called " + env + " found");
        }

        return result;
    }
}
