#version 330 core

in vec3 frag_normal;
in vec2 frag_texcoord;

uniform sampler2D bricks;

out vec4 frag_color;

const vec3 ambient_color = vec3(0.8, 0.8, 0.8);
const float ambient_intensity = 0.8;

const vec3 directional_direction = vec3(-1, 0, -1);
const vec3 directional_color = vec3(1, 1, 1);
const float directional_intensity = 1;

vec3 calculate_ambient(vec3 color, float intensity)
{
    return color * intensity * vec3(texture(bricks, frag_texcoord));
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

    vec3 diffuse  = color * intensity * lambertian * vec3(texture(bricks, frag_texcoord));
    vec3 specular = color * intensity * specular_highlight;

    return diffuse + specular;
}

void main()
{
    vec3 total = calculate_ambient(ambient_color, ambient_intensity);

    total += calculate_directional(directional_direction, directional_color, directional_intensity);

    frag_color = vec4(total, 1.0);
}