
precision mediump float;

uniform sampler2D u_TextureUnit;
uniform float u_Opacity;
uniform sampler2D u_ShadowTexture;

varying vec2 v_TextureCoordinates;
//varying float v_Light;

varying vec4 v_ShadowCoord;
 varying vec3 v_modelViewVertex;
 varying vec3 v_modelViewNormal;
 varying vec3 v_LightVector;


uniform float u_LightsAmbient;
uniform float u_LightsDiffusion;


float unpack (vec4 colour)
{
    const vec4 bitShifts = vec4(1.0 / (256.0 * 256.0 * 256.0),
                                1.0 / (256.0 * 256.0),
                                1.0 / 256.0,
                                1);
    float res = dot(colour , bitShifts);
    return res;
}

//Calculate variable bias - from http://www.opengl-tutorial.org/intermediate-tutorials/tutorial-16-shadow-mapping
float calcBias()
{
	float bias;

	// Cosine of the angle between the normal and the light direction,
	// clamped above 0
	//  - light is at the vertical of the triangle -> 1
	//  - light is perpendiular to the triangle -> 0
	//  - light is behind the triangle -> 0
	float cosTheta = clamp( dot( v_modelViewNormal,v_LightVector ), 0.0, 1.0 );

 	bias = 0.0001*tan(acos(cosTheta));
	bias = clamp(bias, 0.0, 0.01);

 	return bias;
}

//Simple shadow mapping
float shadowSimple()
{
	vec4 shadowMapPosition = v_ShadowCoord / v_ShadowCoord.w;

	shadowMapPosition = (shadowMapPosition + 1.0) /2.0;

	vec4 packedZValue = texture2D(u_ShadowTexture, shadowMapPosition.st);

	float distanceFromLight = unpack(packedZValue);

	//add bias to reduce shadow acne (error margin)
	float bias = calcBias();

	//1.0 = not in shadow (fragmant is closer to light than the value stored in shadow map)
	//0.0 = in shadow
	float res = float(distanceFromLight > shadowMapPosition.z - bias);
	return res;
}

void main()
{

        float diffuseComponent = dot(v_modelViewNormal, v_LightVector);
    	if(diffuseComponent < 0.0){
             diffuseComponent = 0.0;
        }
        float lightFactor =  diffuseComponent * u_LightsDiffusion;
        lightFactor = lightFactor + u_LightsAmbient;

    	float shadow = 1.0;
    	//if the fragment is not behind light view frustum
    	if (v_ShadowCoord.w > 0.0){

    		shadow = shadowSimple();

    		//scale 0.0-1.0 to 0.4-1.0
    		//otherways everything in shadow would be black
    		shadow = (shadow * 0.6) + 0.4;
    	}



    gl_FragColor = vec4(vec3(texture2D(u_TextureUnit, v_TextureCoordinates)) * lightFactor * shadow, u_Opacity);
}