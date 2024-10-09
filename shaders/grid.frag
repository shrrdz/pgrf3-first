#version 330 core

in vec3 frag_normal;
in vec2 frag_texcoord;

uniform sampler2D bricks;

out vec4 frag_color;

void main()
{
    frag_color = texture(bricks, frag_texcoord);
}