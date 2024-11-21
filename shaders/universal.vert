#version 330 core

layout (location = 0) in vec2 in_position;

uniform mat4x4 model;
uniform mat4x4 view;
uniform mat4x4 projection;

uniform mat4 light_view_projection;

uniform bool plane;
uniform bool sphere;
uniform bool fountain;
uniform bool torus;
uniform bool wave;

uniform float theta;

out vec3 frag_position;
out vec3 frag_normal;
out vec2 frag_texcoord;
out vec4 frag_shadowcoord;

const float PI = 3.1415927F;

const mat4 bias_matrix = mat4
(
    0.5, 0.0, 0.0, 0.0,
    0.0, 0.5, 0.0, 0.0,
    0.0, 0.0, 0.5, 0.0,
    0.5, 0.5, 0.5, 1.0
);

float r = 1;

vec3 calculate_sphere(float azimuth, float zenith)
{
    // Cartesian coordinate system conversion
    float x = r * sin(zenith) * cos(azimuth);
    float y = r * sin(zenith) * sin(azimuth);
    float z = r * cos(zenith);

    return vec3(x, y, z);
}

const float minor_r = 1;    // radius of the inner circle
const float major_r = 0.5;  // radius of the outer circle

vec3 calculate_torus(float tube_angle, float azimuthal_angle)
{
    // Cartesian coordinate system
    float x = (minor_r + major_r * cos(tube_angle)) * cos(azimuthal_angle);
    float y = (minor_r + major_r * cos(tube_angle)) * sin(azimuthal_angle);
    float z = major_r * sin(tube_angle);

    return vec3(x, y, z);
}

vec3 calculate_torus_normal(float tube_angle, float azimuthal_angle)
{
    float x = cos(tube_angle) * cos(azimuthal_angle);
    float y = cos(tube_angle) * sin(azimuthal_angle);
    float z = sin(tube_angle);

    return vec3(x, y, z);
}

float calculate_wave(float x, float y)
{
    return 0.5 * cos(sqrt(20 * pow(x, 2) + 20 * pow(y, 2)) + theta);
}

vec3 calculate_wave_normal(float x, float y, float z)
{
    vec3 dx = vec3(x + 0.001, y, calculate_wave(x + 0.001, y)) - vec3(x, y, z);
    vec3 dy = vec3(x, y + 0.001, calculate_wave(x, y + 0.001)) - vec3(x, y, z);

    return normalize(cross(dx, dy));
}

void main()
{
    // spherical coordinate system
    float azimuth = in_position.x * 2 * PI; // azimuthal angle (phi)
    float zenith  = in_position.y * PI;     // polar angle (theta)

    vec3 position = vec3(in_position * 2 - 1, 0);
    vec2 position_twopi = in_position * 2 * PI;

    if (plane)
    {
        frag_normal = transpose(inverse(mat3(model))) * vec3(0, 0, 1);
    }
    else if (sphere)
    {
        position = calculate_sphere(azimuth, zenith);
        frag_normal = transpose(inverse(mat3(model))) * calculate_sphere(azimuth, zenith);
    }
    else if (fountain)
    {
        r = 1.0 * cos(4.0 * zenith);

        position = calculate_sphere(azimuth, zenith);
        frag_normal = transpose(inverse(mat3(model))) * calculate_sphere(azimuth, zenith);
    }
    else if (torus)
    {
        position = calculate_torus(position_twopi.x, position_twopi.y);
        frag_normal = transpose(inverse(mat3(model))) * calculate_torus_normal(position_twopi.x, position_twopi.y);
    }
    else if (wave)
    {
        float z = calculate_wave(position.x, position.y);

        position.z = z;
        frag_normal = transpose(inverse(mat3(model))) * calculate_wave_normal(position.x, position.y, position.z);
    }

    frag_position = vec3(model * vec4(position, 1.0));
    frag_texcoord = in_position;

    gl_Position = projection * view * model * vec4(position,  1.0);

    mat4 depth_bias_matrix = bias_matrix * light_view_projection;

    frag_shadowcoord = depth_bias_matrix * (model * vec4(position, 1));
}