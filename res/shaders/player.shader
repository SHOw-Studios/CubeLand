# shader vertex player_vert
#version 330 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 coords;

out vec2 v_coords;

uniform mat4 projection = mat4(1.0);
uniform mat4 view       = mat4(1.0);
uniform mat4 model      = mat4(1.0);

uniform int animation = 0;
uniform int frame = 0;
uniform int frame_width = 0;
uniform int frame_height = 0;
uniform int atlas_width = 0;
uniform int atlas_height = 0;

void main()
{
    gl_Position = projection * view * model * vec4(position, 1.0);

    vec2 vframe = vec2(float(frame_width), float(frame_height));
    vec2 vatlas = vec2(float(atlas_width), float(atlas_height));

    vec2 offset = vec2(float(frame), float(animation)) + coords;

    v_coords = offset * vframe / vatlas;

    // v_coords = coords;
}

# shader fragment player_frag
#version 330 core

layout (location = 0) out vec4 color;

in vec2 v_coords;

uniform sampler2D sampler;

void main()
{
    vec4 texcolor = texture(sampler, v_coords);
    if(texcolor.a == 0.0)
    {
        // color = vec4(v_coords, 0.0, 1.0);
        discard;
        return;
    }
    float depth = 1.0 - (gl_FragCoord.z / gl_FragCoord.w) * 0.5;
    color = vec4(depth * texcolor.rgb, texcolor.a);
}