precision mediump float;

uniform sampler2D u_Texture;
uniform float u_Opacity;
uniform sampler2D u_ShadowTexture;
uniform float u_LightsAmbient;
uniform float u_LightsDiffusion;

varying vec2 v_TextureCoordinates;
varying vec4 v_ShadowCoord;
varying vec3 v_modelViewVertex;
varying vec3 v_modelViewNormal;
varying vec3 v_LightVector;


float unpack (vec4 color){
    const vec4 bitShifts = vec4(1.0 / (256.0 * 256.0 * 256.0), 1.0 / (256.0 * 256.0), 1.0 / 256.0, 1);
    float res = dot(color , bitShifts);
    return res;
}

float calcBias(){
	float cosTheta = clamp( dot( v_modelViewNormal,v_LightVector ), 0.0, 1.0 );
 	float bias =  0.0001*tan(acos(cosTheta));// 0.0000617*tan(acos(cosTheta));
	bias = clamp(bias, 0.0, 0.01);
 	return bias;
}


float shadowSimple(){
	vec4 shadowMapPosition = v_ShadowCoord / v_ShadowCoord.w;
	shadowMapPosition = (shadowMapPosition + 1.0) /2.0;
	vec4 packedZValue = texture2D(u_ShadowTexture, shadowMapPosition.st);
	float distanceFromLight = unpack(packedZValue);
	float bias = calcBias();

	//1.0 - nie w cieniu
	//0.0 - w cieniu
	float res = float(distanceFromLight > shadowMapPosition.z - bias);
	return res;
}

void main(){
        float diffuseComponent = dot(v_modelViewNormal, v_LightVector);
    	if(diffuseComponent < 0.0){
             diffuseComponent = 0.0;
        }
        float lightFactor =  diffuseComponent * u_LightsDiffusion;
        lightFactor = lightFactor + u_LightsAmbient;

    	float shadow = 1.0;
    	if (v_ShadowCoord.w > 0.0){ //gdy piksel jest we frustumie promieni światła
    		shadow = shadowSimple();
    		shadow = (shadow * 0.6) + 0.4;
    	}

    gl_FragColor = vec4(vec3(texture2D(u_Texture, v_TextureCoordinates)) * lightFactor * shadow, u_Opacity);
}