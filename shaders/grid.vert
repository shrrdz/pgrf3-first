#version 330 core

layout (location = 0) in vec2 in_position;

uniform mat4x4 view;
uniform mat4x4 projection;

uniform float theta;

out vec2 frag_texcoord;

void main()
{
    // convert from <0;1> to <-1;1>
    vec2 position = in_position * 2 - 1;

    float z = 0.5 * cos(sqrt(20 * pow(position.x, 2) + 20 * pow(position.y, 2)) + theta);

    frag_texcoord = in_position;

    gl_Position = projection * view * vec4(position, z,  1.0);
}