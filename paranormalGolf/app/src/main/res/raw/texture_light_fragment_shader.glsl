
precision mediump float;

uniform sampler2D u_TextureUnit;

varying vec2 v_TextureCoordinates;
varying float v_diffuse;

void main()
{
    gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates) * v_diffuse;
}