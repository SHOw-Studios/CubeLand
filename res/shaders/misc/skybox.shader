# shader vertex skybox_vert
#version 330 core

layout (location = 0) in vec2 position;

out vec2 v_Position;

uniform mat4 model = mat4(1.0);

void main()
{
    gl_Position = vec4(position, 0.0, 1.0);
    v_Position = position;
}

# shader fragment skybox_frag
#version 330 core

layout (location = 0) out vec4 color;

in vec2 v_Position;

void main()
{
    color = vec4(mix(mix(vec3(0.5, 0.5, 0.5), vec3(0.5, 0.7, 1.0), v_Position.y * 0.5 + 0.5), mix(vec3(0.0), vec3(1.0), v_Position.y * 0.5 + 0.5), v_Position.x * 0.5 + 0.5), 1.0);
}
