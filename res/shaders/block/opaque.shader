# shader vertex opaque_vert
#version 330 core

layout (location = 0) in vec2 position;
layout (location = 1) in vec2 coords;
layout (location = 2) in vec4 color;

out vec2 v_Coords;
out vec4 v_Color;

uniform mat4 matrix = mat4(1.0);

void main()
{
    gl_Position = matrix * vec4(position, 0.0, 1.0);
    v_Color = color;
    v_Coords = vec2(coords.x, 1.0 - coords.y);
}

# shader fragment opaque_frag
#version 330 core

in vec2 v_Coords;
in vec4 v_Color;

layout (location = 0) out vec4 color;

uniform sampler2D sampler;

void main()
{
    color = vec4((texture(sampler, v_Coords) * v_Color).rgb, 1.0);
}
