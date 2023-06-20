# shader vertex opaque_vert
#version 330 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 coords;

out vec2 v_coords;

uniform mat4 projection = mat4(1.0);
uniform mat4 view       = mat4(1.0);
uniform mat4 model      = mat4(1.0);

void main()
{
    gl_Position = projection * view * model * vec4(position, 1.0);
    v_coords = vec2(coords.x, coords.y);
}

# shader fragment opaque_frag
#version 330 core

in vec2 v_coords;

layout (location = 0) out vec4 color;

uniform sampler2D sampler;

void main()
{
    float depth = 1.0 - (gl_FragCoord.z / gl_FragCoord.w) * 0.5;
    vec4 texcolor = texture(sampler, v_coords);
    color = vec4(depth * texcolor.rgb, texcolor.a);
}
