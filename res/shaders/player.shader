#shader vertex player_vert
#version 330 core

layout(location = 0) in vec3 position;

out vec2 v_coords;

uniform mat4 projection = mat4(1.0);
uniform mat4 view       = mat4(1.0);
uniform mat4 model      = mat4(1.0);

uniform int animation = 0;
uniform int frame = 0;
uniform int frame_width = 0;
uniform int frame_height = 0;

void main()
{
    v_coords
}

#shader fragment player_frag