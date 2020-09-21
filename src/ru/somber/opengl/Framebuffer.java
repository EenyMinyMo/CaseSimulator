package ru.somber.opengl;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import ru.somber.opengl.texture.Texture;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Framebuffer {
    private int framebufferID;
    private Texture[] textures;
    private int[] drawBuffers;
    private int textureFormat;
    private int widthTexture, heightTexture;

    public Framebuffer(int widthTexture, int heightTexture, int countTextures, boolean depthTexture) {
        this.widthTexture = widthTexture;
        this.heightTexture = heightTexture;

        framebufferID = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebufferID);

        if (depthTexture) {
            textures = new Texture[countTextures + 1];
            drawBuffers = new int[countTextures + 1];
        } else {
            textures = new Texture[countTextures];
            drawBuffers = new int[countTextures];
        }

        for (int i = 0; i < countTextures; i++) {
            int texID = GL11.glGenTextures();

            Texture tex = new Texture(GL11.GL_TEXTURE_2D, texID);
            tex.setWidth(widthTexture);
            tex.setHeight(heightTexture);
            tex.setTextureWidth(widthTexture);
            tex.setTextureHeight(heightTexture);

            Texture.bindTexture(tex);

            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, widthTexture, heightTexture, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);

            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0 + i, GL11.GL_TEXTURE_2D, texID, 0);

            textures[i] = tex;
            drawBuffers[i] = GL30.GL_COLOR_ATTACHMENT0 + i;
        }

        if (depthTexture) {
            int texID = GL11.glGenTextures();

            Texture tex = new Texture(GL11.GL_TEXTURE_2D, texID);
            tex.setWidth(widthTexture);
            tex.setHeight(heightTexture);
            tex.setTextureWidth(widthTexture);
            tex.setTextureHeight(heightTexture);

            Texture.bindTexture(tex);

            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, widthTexture, heightTexture, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);

            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, texID, 0);

            textures[countTextures] = tex;
            drawBuffers[countTextures] = GL30.GL_DEPTH_ATTACHMENT;
        }
        Texture.bindNone(GL11.GL_TEXTURE_2D);

        IntBuffer db = BufferUtils.createIntBuffer(drawBuffers.length);
        db.put(drawBuffers).flip();
//        GL20.glDrawBuffers(db);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public int getFramebufferID() {
        return framebufferID;
    }

    public Texture[] getTextures() {
        return textures;
    }

    public int[] getDrawBuffers() {
        return drawBuffers;
    }

    public int getTextureFormat() {
        return textureFormat;
    }

    public int getWidthTexture() {
        return widthTexture;
    }

    public int getHeightTexture() {
        return heightTexture;
    }

    public void deleteFrameBuffer() {
        for (Texture tex : textures) {
            tex.deleteTexture();
        }
        GL30.glDeleteFramebuffers(framebufferID);
    }

    public static void bindFramebuffer(Framebuffer framebuffer) {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.getFramebufferID());
        GL11.glViewport(0, 0, framebuffer.getWidthTexture(), framebuffer.getHeightTexture());
    }

    public static void drawBuffers(Framebuffer framebuffer) {
        if (framebuffer == null) {
            GL20.glDrawBuffers(GL11.GL_NONE);
        } else {
            IntBuffer db = BufferUtils.createIntBuffer(framebuffer.getDrawBuffers().length);
            db.put(framebuffer.getDrawBuffers()).flip();
            GL20.glDrawBuffers(db);
        }
    }

    public static void bindNone(int width, int height) {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL11.glViewport(0, 0, width, height);
    }

}
