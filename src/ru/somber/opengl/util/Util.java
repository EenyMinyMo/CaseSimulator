package ru.somber.opengl.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class Util {

    private Util() {}

    public static String loadShaderCode(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);

        StringBuilder sb = new StringBuilder();
        for (String str : lines) {
            sb.append(str).append("\n");
        }
        return sb.toString();
    }

}
