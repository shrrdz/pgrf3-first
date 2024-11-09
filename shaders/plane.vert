#version 330 core

layout (location = 0) in vec2 in_position;

uniform mat4x4 model;
uniform mat4x4 view;
uniform mat4x4 projection;

out vec3 frag_normal;
out vec2 frag_texcoord;

void main()
{
    // convert from <0;1> to <-1;1>
    vec2 position = in_position * 2 - 1;

    frag_normal = vec3(0, 0, 1);
    frag_texcoord = in_position;

    gl_Position = projection * view * model * vec4(vec3(position, 0.0),  1.0);
}