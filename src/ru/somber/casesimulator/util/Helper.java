package ru.somber.casesimulator.util;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import ru.somber.opengl.texture.Texture;
import ru.somber.opengl.util.TextureLoader;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Helper {
    public static final float EPS2 = 0.01F;
    public static final float EPS4 = 0.0001F;
    public static final float EPS6 = 0.000001F;

    private static Texture defaultTexture;
    private static FloatBuffer defaultTextureCoordBuffer;


    public static Texture getDefaultTexture() {
        if (defaultTexture == null) {
            int ID = GL11.glGenTextures();

            FloatBuffer buffer = BufferUtils.createFloatBuffer(16).put(
                    new float[] {
                            0, 0, 0, 1,
                            1, 0, 1, 1,
                            1, 0, 1, 1,
                            0, 0, 0, 1
                    }
            );
            buffer.flip();

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, ID);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, 2, 2, 0, GL11.GL_RGBA8, GL11.GL_FLOAT, buffer);
            GL11.glBindTexture(GL11.GL_TEXTURE_1D, 0);

            defaultTexture = new Texture(GL11.GL_TEXTURE_2D, ID);
        }

        return defaultTexture;
    }

    public static FloatBuffer getDefaultTexCoordBuffer() {
        if (defaultTextureCoordBuffer == null) {
            float[] texCoord = {
                    0,   1,
                    1,   1,
                    1,   0,
                    0,   0
            };
            defaultTextureCoordBuffer = BufferUtils.createFloatBuffer(texCoord.length);
            defaultTextureCoordBuffer.put(texCoord);
            defaultTextureCoordBuffer.flip();
        }

        return defaultTextureCoordBuffer;
    }

    public static Texture loadTexture(String pathToFile) {
        TextureLoader loader = new TextureLoader();
        Texture texture = null;
        try {
            texture = loader.getTexture(pathToFile, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return texture;
    }

    public static String loadShaderCode(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);

        StringBuilder sb = new StringBuilder();
        for (String str : lines) {
            sb.append(str).append("\n");
        }
        return sb.toString();
    }

}
