uniform mat4 u_MVPMatrix;
uniform mat4 u_MMatrix;
uniform mat4 u_NMatrix;

uniform vec3 u_LightPosition;
uniform float u_LightsAmbient;
uniform float u_LightsDiffusion;

attribute vec4 a_Position;
attribute vec3 a_Normal;

varying float v_Light;

void main(){
    vec3 worldVertex = vec3(u_MMatrix * a_Position);
    vec3 lightVector = normalize(u_LightPosition - worldVertex);
    vec3 worldNormal = normalize(vec3(u_NMatrix * vec4(a_Normal,0)));

    float tmp = dot(worldNormal, lightVector);
    if(tmp < 0.0){
        tmp = 0.0;
    }
    v_Light =  tmp * u_LightsDiffusion;
    v_Light = v_Light + u_LightsAmbient;

    gl_Position = u_MVPMatrix * a_Position;
}