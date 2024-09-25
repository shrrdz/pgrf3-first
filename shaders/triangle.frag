#version 330 core

in vec3 color;

uniform bool alt_color;

out vec4 frag_color;

void main()
{
    frag_color = alt_color ? vec4(0.0, 0.0, 1.0, 1.0) : vec4(color, 1.0);
}