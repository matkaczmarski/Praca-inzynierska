precision mediump float;

uniform vec4 u_Color;

varying float v_Light;

void main(){
    gl_FragColor = vec4(vec3(u_Color * v_Light), u_Color.w);
}