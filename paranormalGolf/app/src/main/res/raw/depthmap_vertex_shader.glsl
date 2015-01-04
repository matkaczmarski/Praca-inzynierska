// Vertex shader to generate the Depth Map
// Used for shadow mapping - generates depth map from the light's viewpoint
precision highp float;

// model-view projection matrix
uniform mat4 u_MVPMatrix;
uniform mat4 u_MVMatrix;

// position of the vertices
attribute vec4 a_Position;

varying vec4 v_Position;

void main() {
	vec4 modelViewPosition = u_MVMatrix * a_Position;
	v_Position = u_MVPMatrix * modelViewPosition;
	gl_Position = v_Position;
}