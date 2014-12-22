uniform mat4 u_MVPMatrix;
uniform mat4 u_MVMatrix;
uniform mat4 u_itMVMatrix;
uniform vec3 u_LightPos;

attribute vec4 a_Position;
attribute vec3 a_Normal;

//uniform vec4 u_Color;

varying float v_Light;

void main()
{
/*
     vec3 modelViewVertex = vec3(u_MVMatrix * a_Position);
     vec3 modelViewNormal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));

     modelViewNormal = modelViewNormal / length(modelViewNormal);
     float distance = length(u_LightPos - modelViewVertex);
     vec3 lightVector = normalize(u_LightPos - modelViewVertex);

     float diffuse = max(dot(modelViewNormal, lightVector), 0.0);
     v_diffuse = diffuse * (1.0 / (0.1 + (0.25 * distance * distance)));
     v_color = u_Color * v_diffuse;

*/

/*

vec3 modelViewVertex = vec3(u_MVMatrix * a_Position);
    float distance = length(u_LightPos - modelViewVertex);

	// Get a lighting direction vector from the light to the vertex.
    vec3 lightVector = normalize(u_LightPos - modelViewVertex);

	// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
	// pointing in the same direction then it will get max illumination.
	float diffuse;

	//UWAGA PRZY ROTACJI Z a_Normal!!!!!!!!!!!

	//if (gl_FrontFacing) {
        diffuse = dot(a_Normal, lightVector);
        diffuse = diffuse > 0.0 ? diffuse : 0.0;
    //} else {
   	//diffuse = max(dot(-a_Normal, lightVector), 0.0);
    //}

	// Add attenuation.
    diffuse = diffuse * (1.0 / (1.0 + (0.10 * distance)));

    // Add ambient lighting
    diffuse = diffuse + 0.4;

       v_color = u_Color * diffuse;

    gl_Position = u_MVPMatrix * a_Position;
    */


        float ka = 0.6;
        float kd = 0.6;
        float ks = 0.5;
        int n = 10;

        float Ia = 1.0;
        float Id = 1.0;
        float Is = 1.0;

    vec3 modelViewVertex = vec3(u_MVMatrix * a_Position);
    float distance = length(u_LightPos - modelViewVertex);
    vec3 lightVector = normalize(u_LightPos - modelViewVertex);
    vec3 modelViewNormal = vec3(normalize(u_itMVMatrix * vec4(a_Normal,0)));
    //v_Light = max(dot(a_Normal, lightVector), 0.0) * kd;

    float tmp = dot(modelViewNormal, lightVector);
    if(tmp < 0.0){
        tmp = 0.0;
    }
    v_Light =  tmp * kd;//(1.0 / (1.0 + (0.10 * distance)));
    v_Light = v_Light + ka;

    gl_Position = u_MVPMatrix * a_Position;
}