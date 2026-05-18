#version 150

uniform sampler2D Sampler0;
uniform float MyAnimTime;

in vec2 texCoord0;
in vec4 vertexColor;
out vec4 fragColor;

vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

float hash(vec2 p) {
    vec3 p3  = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}

void main() {
    float time = MyAnimTime;
    vec2 screenUV = gl_FragCoord.xy * 0.015;

    float bgPulse = sin(time * 0.5) * 0.1 + 0.15;
    vec3 bgColor = hsv2rgb(vec3(fract(screenUV.x * 0.1 + time * 0.1), 1.0, bgPulse));
    vec3 col = bgColor;

    for (int i = 0; i < 6; i++) {
        float layerScale = 1.0 + float(i) * 0.3;
        vec2 gridUV = screenUV * layerScale;
        gridUV.x -= time * (0.15 + float(i) * 0.08);
        gridUV.y += time * (0.05 + float(i) * 0.03);

        vec2 cellID = floor(gridUV);
        vec2 localUV = fract(gridUV) - 0.5;

        float randVal = hash(cellID + float(i) * 12.34);

        if (randVal < 0.15) {
            float angle = time * (1.0 + randVal * 3.0) * (randVal > 0.075 ? 1.0 : -1.0) + randVal * 10.0;
            float s = sin(angle);
            float c = cos(angle);
            mat2 rot = mat2(c, -s, s, c);
            vec2 rotatedUV = rot * localUV;

            if (abs(rotatedUV.x) < 0.4 && abs(rotatedUV.y) < 0.4) {
                vec2 texUV = (rotatedUV / 0.8) + 0.5;
                vec4 texColor = texture(Sampler0, texUV);

                float fade = sin(time * 3.0 + randVal * 20.0) * 0.4 + 0.6;
                vec3 tint = hsv2rgb(vec3(fract(randVal * 10.0 + time * 0.1), 0.5, 1.0));

                col += texColor.rgb * tint * fade * texColor.a;
            }
        }
    }

    col = clamp(col, 0.0, 1.0);
    fragColor = vec4(col, 1.0) * vertexColor;
}