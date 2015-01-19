precision mediump float;

uniform mat4 u_MVPMatrix;
uniform mat4 u_MMatrix;
uniform mat4 u_NMatrix;
uniform mat4 u_ShadowVPMatrix;
uniform vec3 u_LightPosition;

attribute vec4 a_Position;
attribute vec3 a_Normal;
attribute vec2 a_TextureCoordinates;

varying vec2 v_TextureCoordinates;
varying vec3 v_LightVector;


varying vec4 v_ShadowCoord;
 varying vec3 v_modelViewVertex;
 varying vec3 v_modelViewNormal;


void main() {
    v_modelViewVertex = vec3(u_MMatrix * a_Position);
    v_LightVector = normalize(u_LightPosition - v_modelViewVertex);
    v_modelViewNormal = vec3(normalize(u_NMatrix * vec4(a_Normal,0)));
    v_ShadowCoord = u_ShadowVPMatrix * u_MMatrix * a_Position;
    v_TextureCoordinates = a_TextureCoordinates;	  	  
    gl_Position = u_MVPMatrix * a_Position;
}
