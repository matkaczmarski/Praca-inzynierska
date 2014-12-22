precision mediump float;

uniform vec4 u_Color;

varying float v_Light;

void main()
{
    gl_FragColor = u_Color * v_Light;//v_Light;//u_Color * v_diffuse;
}