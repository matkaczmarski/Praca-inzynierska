precision mediump float;

//uniform vec4 u_Color;
varying float v_diffuse;

varying vec4 v_color;

void main()
{
    gl_FragColor = v_color;//u_Color * v_diffuse;
}