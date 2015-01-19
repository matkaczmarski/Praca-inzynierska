precision mediump float;

uniform mat4 u_MVPMatrix;
uniform mat4 u_MMatrix;
uniform mat4 u_NMatrix;

uniform vec3 u_LightPosition;
uniform float u_LightsAmbient;
uniform float u_LightsDiffusion;

attribute vec4 a_Position;
attribute vec3 a_Normal;
attribute vec2 a_TextureCoordinates;

varying vec2 v_TextureCoordinates;
varying float v_Light;


void main() {
    vec3 worldVertex = vec3(u_MMatrix * a_Position);
    vec3 lightVector = normalize(u_LightPosition - worldVertex);
    vec3 worldNormal = normalize(vec3(u_NMatrix * vec4(a_Normal,0)));

    float diffuseComponent = dot(worldNormal, lightVector);
    if(diffuseComponent < 0.0){
        diffuseComponent = 0.0;
    }
    v_Light =  diffuseComponent * u_LightsDiffusion;
    v_Light = v_Light + u_LightsAmbient;

    v_TextureCoordinates = a_TextureCoordinates;	  	  
    gl_Position = u_MVPMatrix * a_Position;
}
