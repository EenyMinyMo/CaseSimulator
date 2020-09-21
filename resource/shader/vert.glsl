#version 420

layout (location = 0) in vec2 pos;
layout (location = 1) in vec2 texPos;

out vec2 texturePos;

void main() {
    texturePos = texPos;

    gl_Position = vec4(pos * 2 - 1, 0, 1);
}
