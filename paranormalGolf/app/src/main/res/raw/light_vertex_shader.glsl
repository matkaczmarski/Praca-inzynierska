uniform mat4 u_MVPMatrix;
uniform mat4 u_MVMatrix;
uniform mat4 u_itMVMatrix;

uniform vec3 u_LightPos;
uniform float u_LightsAmbient;
uniform float u_LightsDiffusion;

attribute vec4 a_Position;
attribute vec3 a_Normal;

varying float v_Light;

void main()
{
    vec3 modelViewVertex = vec3(u_MVMatrix * a_Position);
    vec3 lightVector = normalize(u_LightPos - modelViewVertex);
    vec3 modelViewNormal = vec3(normalize(u_itMVMatrix * vec4(a_Normal,0)));

    float tmp = dot(modelViewNormal, lightVector);
    if(tmp < 0.0){
        tmp = 0.0;
    }
    v_Light =  tmp * u_LightsDiffusion;
    v_Light = v_Light + u_LightsAmbient;

    gl_Position = u_MVPMatrix * a_Position;
}