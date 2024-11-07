#version 330 core

layout (location = 0) in vec2 in_position;

uniform mat4x4 model;
uniform mat4x4 view;
uniform mat4x4 projection;

out vec3 frag_normal;
out vec2 frag_texcoord;

const float PI = 3.1415927F;

const float minor_r = 1;    // radius of the inner circle
const float major_r = 0.5;  // radius of the outer circle

vec3 calculate_coordinates(float tube_angle, float azimuthal_angle)
{
    // Cartesian coordinate system
    float x = (minor_r + major_r * cos(tube_angle)) * cos(azimuthal_angle);
    float y = (minor_r + major_r * cos(tube_angle)) * sin(azimuthal_angle);
    float z = major_r * sin(tube_angle);

    return vec3(x, y, z);
}

vec3 calculate_normal(float tube_angle, float azimuthal_angle)
{
    float x = cos(tube_angle) * cos(azimuthal_angle);
    float y = cos(tube_angle) * sin(azimuthal_angle);
    float z = sin(tube_angle);

    return vec3(x, y, z);
}

void main()
{
    vec2 position = in_position * 2 * PI;

    frag_normal = transpose(inverse(mat3(model))) * calculate_normal(position.x, position.y);
    frag_texcoord = in_position;

    gl_Position = projection * view * model * vec4(calculate_coordinates(position.x, position.y),  1.0);
}