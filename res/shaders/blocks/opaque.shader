# shader opaque_vert
#version 330 core

layout (location = 0) in vec4 position;
layout (location = 1) in vec4 color;

out vec4 v_Color;

void main()
{
    gl_Position = position;
    v_Color = vec4(color.rgb, 1.0);
}

# shader opaque_frag
#version 330 core

layout (location = 0) out vec4 color;

in vec4 v_Color;

void main()
{
    color = v_Color;
}
