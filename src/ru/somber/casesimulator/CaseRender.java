package ru.somber.casesimulator;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL33;
import ru.somber.casesimulator.menu.CaseMode;
import ru.somber.casesimulator.tape.CaseTape;
import ru.somber.casesimulator.tape.CaseTapeOFTMode;
import ru.somber.casesimulator.util.Helper;
import ru.somber.opengl.Shader;
import ru.somber.opengl.ShaderProgram;
import ru.somber.opengl.texture.Texture;

import java.nio.FloatBuffer;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

public class CaseRender {
    private CaseStorage caseStorage;
    private CaseTape caseTape;

    private final float WIDTH_TEXTURE_ON_ITEM = 1.0F / CaseRun.COUNT_ITEM_IN_WIDTH;
    private final int COUNT_ITEM_ON_SIDE = CaseRun.COUNT_ITEM_IN_WIDTH / 2 + 2;

    private final CaseMode renderMode;
    private final int countItemWidth;
    private final int countItemHeight;
    private final float itemTexWidth;
    private final float itemTexHeight;

    private ShaderProgram shaderProgram;
    private int samplerID;
    private Texture centerLineTexture;
    private Texture tapeTexture;
    private Texture backgroundTexture;


    public CaseRender(CaseStorage caseStorage, CaseMode renderMode, int countItemWidth, int countItemHeight) {
        this.caseStorage = caseStorage;
        this.renderMode = renderMode;
        this.countItemWidth = countItemWidth;
        this.countItemHeight = countItemHeight;

        itemTexWidth = 1.0F / countItemWidth;
        itemTexHeight = 1.0F / countItemHeight;
    }

    public void loadResource() {
        centerLineTexture = Helper.loadTexture("resource\\texture\\center_line.png");
        backgroundTexture = Helper.loadTexture("resource\\texture\\background.png");
        tapeTexture = Helper.loadTexture("resource\\texture\\ruletka.png");

        try {
            Shader diffuseVertShader = new Shader(GL20.GL_VERTEX_SHADER);
            diffuseVertShader.setSourceCode(Helper.loadShaderCode(Paths.get("resource\\shader\\vert.glsl")));
            diffuseVertShader.compileShader();
            if (! diffuseVertShader.isCompile()) {
                throw new Exception(diffuseVertShader.getInfoLog());
            }

            Shader diffuseFragShader = new Shader(GL20.GL_FRAGMENT_SHADER);
            diffuseFragShader.setSourceCode(Helper.loadShaderCode(Paths.get("resource\\shader\\frag.glsl")));
            diffuseFragShader.compileShader();
            if (! diffuseFragShader.isCompile()) {
                throw new Exception(diffuseFragShader.getInfoLog());
            }

            shaderProgram = new ShaderProgram();
            shaderProgram.attachShader(diffuseVertShader);
            shaderProgram.attachShader(diffuseFragShader);
            shaderProgram.linkProgram();
            if (! shaderProgram.isLink()) {
                throw new Exception(shaderProgram.getInfoLog());
            }
            shaderProgram.detachAllShader();
            diffuseVertShader.deleteShader();
            diffuseFragShader.deleteShader();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        samplerID = GL33.glGenSamplers();
        GL33.glSamplerParameteri(samplerID, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL33.glSamplerParameteri(samplerID, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL33.glBindSampler(GL13.GL_TEXTURE0, samplerID);
    }

    public void setCaseTape(CaseTape caseTape) {
        this.caseTape = caseTape;
    }

    public void render() {
        renderBackground();
        renderFullCaseDrop();

        if (renderMode == CaseMode.OFT) {
            renderTapeOFT();
            renderCenterLineOFT();
        } else {
            renderTapePRT();
        }
    }

    public void destroy() {
        GL33.glBindSampler(GL13.GL_TEXTURE0, 0);

        GL33.glDeleteSamplers(samplerID);
        GL11.glDeleteTextures(centerLineTexture.getTextureID());
        GL11.glDeleteTextures(tapeTexture.getTextureID());
        GL11.glDeleteTextures(backgroundTexture.getTextureID());
        GL20.glDeleteProgram(shaderProgram.getShaderProgramID());
    }


    private void renderFullCaseDrop() {
        List<ItemDrop> sortItems = caseStorage.getSortByPercentItemDrop();

        GL13.glActiveTexture(GL13.GL_TEXTURE0);

        ShaderProgram.useProgram(shaderProgram);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        Iterator<ItemDrop> iteratorItemDrop = sortItems.iterator();
        for (int y = 0; y < countItemHeight; y++) {
            for (int x = 0; x < countItemWidth; x++) {
                if (iteratorItemDrop.hasNext()) {
                    ItemDrop itemDrop = iteratorItemDrop.next();

                    float xw = ((float) x) / countItemWidth;
                    float yw = 1 - ((float) y) / countItemHeight - itemTexHeight;

                    float[] coord = {
                            xw,                   yw,
                            xw + itemTexWidth,    yw,
                            xw + itemTexWidth,    yw + itemTexHeight,
                            xw,                   yw + itemTexHeight
                    };
                    FloatBuffer coordBuffer = BufferUtils.createFloatBuffer(coord.length);
                    coordBuffer.put(coord);
                    coordBuffer.flip();

                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, itemDrop.getTexture().getTextureID());

                    GL20.glVertexAttribPointer(0, 2, true, 0, coordBuffer);
                    GL20.glVertexAttribPointer(1, 2, true, 0, Helper.getDefaultTexCoordBuffer());

                    GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);
                }
            }
        }

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        ShaderProgram.useNone();

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    private void renderTapeOFT() {
        CaseTapeOFTMode tapeOFTMode = (CaseTapeOFTMode) caseTape;

        float position = tapeOFTMode.getPosition();
        float offsetOfCenter = WIDTH_TEXTURE_ON_ITEM - (position % 1) * WIDTH_TEXTURE_ON_ITEM;
        final float constantOffset = 0.5F;
        
        ItemDrop[] drops = tapeOFTMode.getItemDropAroundPosition(position, COUNT_ITEM_ON_SIDE);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);

        ShaderProgram.useProgram(shaderProgram);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        for (int i = 0; i < drops.length; i++) {
            ItemDrop itemDrop = drops[i];

            float localReferenceX = constantOffset - WIDTH_TEXTURE_ON_ITEM * (COUNT_ITEM_ON_SIDE - i + 1) + offsetOfCenter;
            float[] coord = {
                    localReferenceX,                            0,
                    localReferenceX + WIDTH_TEXTURE_ON_ITEM,    0,
                    localReferenceX + WIDTH_TEXTURE_ON_ITEM,    itemTexHeight,
                    localReferenceX,                            itemTexHeight
            };
            FloatBuffer coordBuffer = BufferUtils.createFloatBuffer(coord.length);
            coordBuffer.put(coord);
            coordBuffer.flip();

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, itemDrop.getTexture().getTextureID());

            GL20.glVertexAttribPointer(0, 2, true, 0, coordBuffer);
            GL20.glVertexAttribPointer(1, 2, true, 0, Helper.getDefaultTexCoordBuffer());

            GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);

        }

        float[] coord = {
                0,  0,
                1,  0,
                1,  itemTexHeight,
                0,  itemTexHeight
        };
        FloatBuffer coordBuffer = BufferUtils.createFloatBuffer(coord.length);
        coordBuffer.put(coord);
        coordBuffer.flip();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, tapeTexture.getTextureID());

        GL20.glVertexAttribPointer(0, 2, true, 0, coordBuffer);
        GL20.glVertexAttribPointer(1, 2, true, 0, Helper.getDefaultTexCoordBuffer());

        GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        ShaderProgram.useNone();

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    private void renderCenterLineOFT() {
        float[] coord = {
                0.495F,   0,
                0.505F,   0,
                0.505F,   itemTexHeight,
                0.495F,   itemTexHeight
        };
        FloatBuffer coordBuffer = BufferUtils.createFloatBuffer(coord.length);
        coordBuffer.put(coord);
        coordBuffer.flip();

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, centerLineTexture.getTextureID());

        ShaderProgram.useProgram(shaderProgram);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glVertexAttribPointer(0, 2, true, 0, coordBuffer);
        GL20.glVertexAttribPointer(1, 2, true, 0, Helper.getDefaultTexCoordBuffer());

        GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        ShaderProgram.useNone();

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    private void renderTapePRT() {
        List<ItemDrop> drops = caseTape.getItemDropList();

        GL13.glActiveTexture(GL13.GL_TEXTURE0);

        ShaderProgram.useProgram(shaderProgram);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        for (int i = 0; i < drops.size(); i++) {
            ItemDrop itemDrop = drops.get(i);

            float offset = itemTexWidth * 0.3F;
            float[] coord = {
                    offset,                     0,
                    offset + itemTexWidth,      0,
                    offset + itemTexWidth,      itemTexHeight,
                    offset,                     itemTexHeight
            };
            FloatBuffer coordBuffer = BufferUtils.createFloatBuffer(coord.length);
            coordBuffer.put(coord);
            coordBuffer.flip();

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, itemDrop.getTexture().getTextureID());

            GL20.glVertexAttribPointer(0, 2, true, 0, coordBuffer);
            GL20.glVertexAttribPointer(1, 2, true, 0, Helper.getDefaultTexCoordBuffer());

            GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);
        }

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        ShaderProgram.useNone();

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    private void renderBackground() {
        ShaderProgram.useProgram(shaderProgram);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, backgroundTexture.getTextureID());

        GL20.glVertexAttribPointer(0, 2, true, 0, Helper.getDefaultTexCoordBuffer());
        GL20.glVertexAttribPointer(1, 2, true, 0, Helper.getDefaultTexCoordBuffer());

        GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        ShaderProgram.useNone();

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

}
