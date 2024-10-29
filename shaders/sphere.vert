#version 330 core

layout (location = 0) in vec2 in_position;

uniform mat4x4 model;
uniform mat4x4 view;
uniform mat4x4 projection;

out vec3 frag_normal;
out vec2 frag_texcoord;

const float PI = 3.1415927F;

const float r = 1;

vec3 calculate_coordinates(float azimuth, float zenith)
{
    // Cartesian coordinate system conversion
    float x = r * sin(zenith) * cos(azimuth);
    float y = r * sin(zenith) * sin(azimuth);
    float z = r * cos(zenith);

    return vec3(x, y, z);
}

void main()
{
    // spherical coordinate system
    float azimuth = in_position.x * 2 * PI; // azimuthal angle (phi)
    float zenith  = in_position.y * PI;     // polar angle (theta)

    frag_normal = transpose(inverse(mat3(model))) * calculate_coordinates(azimuth, zenith);
    frag_texcoord = in_position;

    gl_Position = projection * view * model * vec4(calculate_coordinates(azimuth, zenith),  1.0);
}