precision highp float;

varying vec4 v_Position;

vec4 pack (float depth){
	const vec4 bitSh = vec4(256.0 * 256.0 * 256.0, 256.0 * 256.0, 256.0, 1.0);
	const vec4 bitMsk = vec4(0, 1.0 / 256.0, 1.0 / 256.0, 1.0 / 256.0);
	vec4 comp = fract(depth * bitSh);
	comp -= comp.xxyz * bitMsk;
	return comp;
}

void main() {
	float normalizedDistance  = v_Position.z / v_Position.w;
	normalizedDistance = (normalizedDistance + 1.0) / 2.0;
	//normalizedDistance += 0.0005;
	gl_FragColor = pack(normalizedDistance);
}