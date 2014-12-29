
uniform mat4 u_MVPMatrix;
uniform mat4 u_MVMatrix;
uniform mat4 u_itMVMatrix;

uniform vec3 u_LightPos;
uniform float u_LightsAmbient;
uniform float u_LightsDiffusion;

attribute vec4 a_Position;
attribute vec3 a_Normal;
attribute vec2 a_TextureCoordinates;

varying vec2 v_TextureCoordinates;
varying float v_Light;


void main()                    
{
    vec3 modelViewVertex = vec3(u_MVMatrix * a_Position);
    vec3 lightVector = normalize(u_LightPos - modelViewVertex);
    vec3 modelViewNormal = vec3(normalize(u_itMVMatrix * vec4(a_Normal,0)));

    float diffuseComponent = dot(modelViewNormal, lightVector);
    if(diffuseComponent < 0.0){
        diffuseComponent = 0.0;
    }
    v_Light =  diffuseComponent * u_LightsDiffusion;
    v_Light = v_Light + u_LightsAmbient;

    v_TextureCoordinates = a_TextureCoordinates;	  	  
    gl_Position = u_MVPMatrix * a_Position;
}
