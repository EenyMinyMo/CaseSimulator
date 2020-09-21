#version 420

in vec2 texturePos;

layout (binding = 0) uniform sampler2D itemTexture;

out vec4 fragColor;

void main() {
    fragColor = texture(itemTexture, texturePos);
}
