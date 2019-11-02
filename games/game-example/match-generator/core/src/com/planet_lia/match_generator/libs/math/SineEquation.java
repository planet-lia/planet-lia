package com.planet_lia.match_generator.libs.math;

public class SineEquation {
    public float t0;
    public float A;
    public float r;
    public float B;
    public float C;

    public SineEquation(float t0, float a, float r, float b, float c) {
        this.t0 = t0;
        A = a;
        this.r = r;
        B = b;
        C = c;
    }

    public float getValue(float time) {
        return (float) (this.A + this.r * Math.sin(this.B * (time - this.t0) + this.C));
    }
}
