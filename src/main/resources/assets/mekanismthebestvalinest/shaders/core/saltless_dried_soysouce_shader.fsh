#version 150

uniform sampler2D Sampler0;
uniform float MyAnimTime;

in vec2 texCoord0;
in vec4 vertexColor;
out vec4 fragColor;

float rand(vec2 n) {
    return fract(sin(dot(n, vec2(12.9898, 4.1414))) * 43758.5453);
}

float noise(vec2 p) {
    vec2 ip = floor(p);
    vec2 u = fract(p);
    u = u * u * (3.0 - 2.0 * u);

    float res = mix(
        mix(rand(ip), rand(ip + vec2(1.0, 0.0)), u.x),
        mix(rand(ip + vec2(0.0, 1.0)), rand(ip + vec2(1.0, 1.0)), u.x), u.y);
    return res * res;
}

void main() {
    float time = MyAnimTime;
    vec2 screenUV = gl_FragCoord.xy * 0.1;

    vec2 uv1 = screenUV + vec2(time * 0.5, time * 0.3);
    vec2 uv2 = screenUV + vec2(time * -0.3, time * 0.4);
    vec2 uv3 = screenUV + vec2(time * 0.1, time * -0.6);

    float n = noise(uv1 * 1.5) * 0.5 + noise(uv2 * 2.5) * 0.3 + noise(uv3 * 4.0) * 0.2;

    vec3 darkSoy = vec3(0.02, 0.01, 0.005);
    vec3 lightSoy = vec3(0.35, 0.15, 0.05);

    vec3 finalColor = mix(darkSoy, lightSoy, n);

    if (rand(floor(screenUV * 2.0) + vec2(floor(time * 15.0), 0.0)) < 0.08) {
        finalColor += vec3(0.12, 0.06, 0.02);
    }

    fragColor = vec4(finalColor, 1.0) * vertexColor;
}