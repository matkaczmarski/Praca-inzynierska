
precision mediump float;

uniform sampler2D u_Texture;
uniform float u_Opacity;

varying vec2 v_TextureCoordinates;
varying float v_Light;


void main(){
    gl_FragColor = vec4(vec3(texture2D(u_Texture, v_TextureCoordinates) * v_Light), u_Opacity);
}