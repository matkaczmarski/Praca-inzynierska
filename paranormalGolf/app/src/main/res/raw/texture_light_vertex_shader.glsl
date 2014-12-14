
uniform mat4 u_MVPMatrix;
uniform mat4 u_MVMatrix;
uniform vec3 u_LightPos;




attribute vec4 a_Position;
 attribute vec3 a_Normal;
attribute vec2 a_TextureCoordinates;

varying vec2 v_TextureCoordinates;

varying float v_Light;


//vec3 getPointLighting();

void main()                    
{

        float ka = 0.4;
        float kd = 0.5;
        float ks = 0.5;
        int n = 10;

        float Ia = 1.0;
        float Id = 1.0;
        float Is = 1.0;

    vec3 modelViewVertex = vec3(u_MVMatrix * a_Position);
    float distance = length(u_LightPos - modelViewVertex);
    vec3 lightVector = normalize(u_LightPos - modelViewVertex);
    vec3 modelViewNormal = vec3(normalize(u_MVMatrix * vec4(a_Normal,0)));
    //v_Light = max(dot(a_Normal, lightVector), 0.0) * kd;

    float tmp = dot(modelViewNormal, lightVector);
    tmp = tmp > 0.0 ? tmp : 0.0;

    v_Light =  tmp * (1.0 / (1.0 + (0.10 * distance)));
    v_Light = v_Light + ka;

    v_TextureCoordinates = a_TextureCoordinates;	  	  
    gl_Position = u_MVPMatrix * a_Position;

}
