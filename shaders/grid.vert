#version 330 core

layout (location = 0) in vec2 in_position;

uniform mat4x4 model;
uniform mat4x4 view;
uniform mat4x4 projection;

out vec3 frag_normal;
out vec2 frag_texcoord;

const float PI = 3.1415927F;

void main()
{
    // convert from <0;1> to <-1;1>
    vec2 position = in_position * 2 - 1;

    // explicit function
    float z = 0.5 * cos(sqrt(20 * pow(position.x, 2) + 20 * pow(position.y, 2)) + theta);

    frag_normal = normalize(vec3(position, 1.0));
    frag_texcoord = in_position;

    gl_Position = projection * view * model * vec4(position, z,  1.0);
}