#version 150

uniform sampler2D Sampler0;
uniform float MyAnimTime;

in vec2 texCoord0;
in vec4 vertexColor;
out vec4 fragColor;

void main() {
    float time = MyAnimTime * 2.0;

    vec2 screenUV = gl_FragCoord.xy * 0.05;

    vec3 deepDark = vec3(0.05, 0.02, 0.01);
    vec3 amberRed = vec3(0.35, 0.12, 0.03);

    float wave1 = sin(screenUV.x * 2.0 + screenUV.y * 1.5 - time * 1.5);
    float wave2 = cos(screenUV.x * 0.8 - screenUV.y * 3.0 + time * 1.0);

    float mixFactor = ((wave1 + wave2) * 0.25) + 0.5;
    vec3 liquidColor = mix(deepDark, amberRed, mixFactor);

    vec4 texColor = texture(Sampler0, texCoord0);

    fragColor = vec4(liquidColor, 0.95) * texColor * vertexColor;
}