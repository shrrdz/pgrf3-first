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
    // spherical coordinate system
    float azimuth = in_position.x * 2 * PI; // azimuthal angle (phi)
    float zenith  = in_position.y * PI;     // polar angle (theta)

    // sphere
    float r = 1;

    // Cartesian coordinate system conversion
    float x = r * sin(zenith) * cos(azimuth);
    float y = r * sin(zenith) * sin(azimuth);
    float z = r * cos(zenith);

    frag_normal = vec3(x, y, z);
    frag_texcoord = in_position;

    gl_Position = projection * view * model * vec4(x, y, z,  1.0);
}