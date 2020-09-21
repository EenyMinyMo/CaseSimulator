package ru.somber.casesimulator;

import ru.somber.opengl.texture.Texture;

import java.util.Objects;

public class ItemDrop {
    private final String name;
    private final String texturePath;
    private final float dropNumberValue;

    private Texture texture;


    public ItemDrop(String name, String texturePath, float dropNumberValue) {
        this.name = name;
        this.texturePath = texturePath;
        this.dropNumberValue = dropNumberValue;
    }

    public ItemDrop(String name, String colorName, String texturePath, float dropNumberValue) {
        this.name = name;
        this.texturePath = texturePath;
        this.dropNumberValue = dropNumberValue;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public String getName() {
        return name;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public float getDropNumberValue() {
        return dropNumberValue;
    }

    public Texture getTexture() {
        return texture;
    }

    public void destroy() {
        texture.deleteTexture();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemDrop itemDrop = (ItemDrop) o;
        return Float.compare(itemDrop.dropNumberValue, dropNumberValue) == 0 &&
                Objects.equals(name, itemDrop.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, dropNumberValue);
    }

    @Override
    public String toString() {
        return "ItemDrop{" +
                "name='" + name + '\'' +
                ", texturePath='" + texturePath + '\'' +
                ", dropNumberValue=" + dropNumberValue +
                '}';
    }

}
