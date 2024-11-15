#version 330 core

in vec3 frag_normal;
in vec2 frag_texcoord;
in vec4 frag_shadowcoord;

uniform bool display[3];
uniform bool receive_shadows;

uniform sampler2D bitmap;
uniform sampler2D shadowmap;

out vec4 frag_color;

// depth buffer constants
const float near = 0.1;
const float far  = 100.0;

// lighting constants
const vec3 ambient_color = vec3(0.8, 0.8, 0.8);
const float ambient_intensity = 0.8;

const vec3 directional_direction = vec3(-1, 0, -1);
const vec3 directional_color = vec3(1, 1, 1);
const float directional_intensity = 1;

vec3 base_color;

float linearize_depth(float depth)
{
    // convert to NDC
    float z = depth * 2.0 - 1.0;

    return (2.0 * near * far) / (far + near - z * (far - near));
}

vec3 calculate_ambient(vec3 color, float intensity)
{
    return color * intensity * base_color;
}

vec3 calculate_directional(vec3 direction, vec3 color, float intensity)
{
    vec3 normal = normalize(frag_normal);

    vec3 light_direction = normalize(-direction);

    // Lambert's cosine law
    float lambertian = max(0.0, dot(normal, light_direction));

    // intensity of the specular highlight
    float specular_exponent = 48.0;

    float specular_highlight = 0.0;

    // if there is no diffuse lighting hitting the surface, don't bother calculating the specular
    if (lambertian != 0.0)
    {
        vec3 view_direction = normalize(vec3(0, -4, 4) - vec3(0, 0, 0));
//      vec3 reflect_direction = reflect(-light_direction, normal);
        vec3 halfway = normalize(view_direction + light_direction);

        specular_highlight = pow(max(dot(halfway, normal), 0.0), specular_exponent);
//      specular_highlight = pow(max(0.0, dot(reflect_direction, view_direction)), specular_exponent);
    }

    vec3 diffuse  = color * intensity * lambertian * base_color;
    vec3 specular = color * intensity * specular_highlight;

    return diffuse + specular;
}

void main()
{
    base_color = display[0] ? vec3(0.5, 0.5, 0.5) : vec3(texture(bitmap, frag_texcoord));

    vec3 total = calculate_ambient(ambient_color, ambient_intensity);

    total += calculate_directional(directional_direction, directional_color, directional_intensity);

    if (display[1]) // depth buffer
    {
        float depth = linearize_depth(gl_FragCoord.z) / far;

        frag_color = vec4(vec3(depth), 1.0);
    }
    else if (display[2]) // normals
    {
        frag_color = vec4(frag_normal, 1.0);
    }
    else
    {
        float visibility = 1;
        float bias = 0.005;

        if(receive_shadows && texture(shadowmap, frag_shadowcoord.xy).z < frag_shadowcoord.z - bias) {
            visibility = 0.5;
        }

        frag_color = visibility * vec4(total, 1.0);
    }
}