#version 330 core

layout (location = 0) in vec3 in_position;
layout (location = 1) in vec3 in_color;

uniform mat4x4 view;
uniform mat4x4 projection;

out vec3 color;

void main()
{
    color = in_color;

    gl_Position = projection * view * vec4(in_position, 1.0);
}