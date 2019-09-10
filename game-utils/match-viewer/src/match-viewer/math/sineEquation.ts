/**
 * A partial sine equation curveRef structure in format A + r * sin(B * (t - t0) + C).
 *
 * A - offsets in y axis
 * r - radius of the function
 * B - frequency of the sine
 * t0 - time when the equation started
 * C - offset in x axis
 */
export class SineEquation {
    t0: number;
    A: number;
    r: number;
    B: number;
    C: number;

    constructor(t0: number, A: number, r: number, B: number, C: number) {
        this.t0 = t0;
        this.A = A;
        this.r = r;
        this.B = B;
        this.C = C;
    }

    // Returns a value on sine function.
    //
    // time - time based on the start of the function (time at start
    //        of the function is 0)
    getValue(time: number): number {
        return this.A + this.r * Math.sin(this.B * (time - this.t0) + this.C);
    }
}


