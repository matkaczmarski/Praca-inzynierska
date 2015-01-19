precision highp float;

uniform mat4 u_ShadowVPMatrix;
uniform mat4 u_MMatrix;

attribute vec4 a_Position;

varying vec4 v_Position;

void main() {
    v_Position = u_ShadowVPMatrix * (u_MMatrix * a_Position);
	gl_Position = v_Position;
}