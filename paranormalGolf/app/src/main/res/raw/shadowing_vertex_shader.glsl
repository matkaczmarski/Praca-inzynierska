
precision mediump float;

uniform mat4 u_MVPMatrix;
uniform mat4 u_MVMatrix;
uniform mat4 u_itMVMatrix;

uniform vec3 u_LightPos;
//uniform float u_LightsAmbient;
//uniform float u_LightsDiffusion;

attribute vec4 a_Position;
attribute vec3 a_Normal;
attribute vec2 a_TextureCoordinates;

varying vec2 v_TextureCoordinates;
//varying float v_Light;
varying vec3 v_LightVector;

uniform mat4 u_ShadowProjMatrix;

varying vec4 v_ShadowCoord;
 varying vec3 v_modelViewVertex;
 varying vec3 v_modelViewNormal;


void main()                    
{
    v_modelViewVertex = vec3(u_MVMatrix * a_Position);
    v_LightVector = normalize(u_LightPos - v_modelViewVertex);
    v_modelViewNormal = vec3(normalize(u_itMVMatrix * vec4(a_Normal,0)));

    // diffuseComponent = dot(v_modelViewNormal, v_LightVector);
    //if(diffuseComponent < 0.0){
    //    diffuseComponent = 0.0;
    //}
    //v_Light =  diffuseComponent * u_LightsDiffusion;
    //v_Light = v_Light + u_LightsAmbient;

    v_ShadowCoord = u_ShadowProjMatrix * u_MVMatrix * a_Position;

    v_TextureCoordinates = a_TextureCoordinates;	  	  
    gl_Position = u_MVPMatrix * a_Position;
}
