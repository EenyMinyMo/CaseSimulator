package ru.somber.opengl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShaderProgram {

    private int shaderProgramID;
    private List<Shader> attachShaders;

    public ShaderProgram() {
        shaderProgramID = GL20.glCreateProgram();
        attachShaders = new ArrayList<>();
    }

    public boolean attachShader(Shader shader) {
        if (! shader.isCompile())
            return false;

        GL20.glAttachShader(shaderProgramID, shader.getShaderID());
        attachShaders.add(shader);
        return true;
    }

    public void detachShader(Shader shader) {
        GL20.glDetachShader(shaderProgramID, shader.getShaderID());
        attachShaders.remove(shader);
    }

    public void detachAllShader() {
        attachShaders.forEach((Shader shader) -> { GL20.glDetachShader(shaderProgramID, shader.getShaderID()); });
    }

    public boolean linkProgram() {
        GL20.glLinkProgram(shaderProgramID);
        return isLink();
    }

    public int getLinkStatus() {
        int linkStatus = GL20.glGetProgrami(shaderProgramID, GL20.GL_LINK_STATUS);
        return linkStatus;
    }

    public boolean isLink() {
        return getLinkStatus() == GL11.GL_TRUE;
    }

    public String getInfoLog() {
        int messageLength = GL20.glGetProgrami(shaderProgramID, GL20.GL_INFO_LOG_LENGTH);
        String message = GL20.glGetProgramInfoLog(shaderProgramID, messageLength);
        return message;
    }

    public Shader[] getAttachShaders() {
        return attachShaders.toArray(new Shader[0]);
    }

    public int getShaderProgramID() {
        return shaderProgramID;
    }

    public void deleteProgram() {
        GL20.glDeleteProgram(shaderProgramID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShaderProgram that = (ShaderProgram) o;
        return shaderProgramID == that.shaderProgramID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(shaderProgramID);
    }


    public static void useProgram(ShaderProgram shaderProgram) {
        GL20.glUseProgram(shaderProgram.shaderProgramID);
    }

    public static void useNone() {
        GL20.glUseProgram(0);
    }

}
