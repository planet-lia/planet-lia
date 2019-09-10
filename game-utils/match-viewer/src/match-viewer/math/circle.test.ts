import {
    getCircleCenter, getCircularMovementParametricEquations, normalizeAngleDeg, normalizeAngleRad,
    toRadians
} from "./circle";

test('getCircularMovementParametricEquations', () => {
    let cases = [
        {
            "in": {"time": 4.5, "velocity": 80, "rotationVelocity": -40, "angle": 255, "x": 409.9, "y": 391.3},
            "sinX": {"t0": 4.5, "A": 299.2, "r": 114.5, "B": -0.6, "C": 1.3},
            "sinY": {"t0": 4.5, "A": 420.8, "r": 114.5, "B": -0.6, "C": -0.2}
        },
        {
            "in": {"time": 7, "velocity": 90, "rotationVelocity": 127, "angle": 155, "x": 250.4, "y": 317},
            "sinX": {"t0": 7, "A": 233.2, "r": 40.6, "B": 2.2, "C": 2.7},
            "sinY": {"t0": 7, "A": 280, "r": 40.6, "B": 2.2, "C": 1.1}
        },
    ];

    for (let c of cases) {
        let [sineX, sineY] = getCircularMovementParametricEquations(c.in.time, c.in.velocity, c.in.rotationVelocity,
            c.in.angle, c.in.x, c.in.y);

        expect(sineX.t0).toBeCloseTo(c.sinX.t0, 0);
        expect(sineX.A).toBeCloseTo(c.sinX.A, 0);
        expect(sineX.r).toBeCloseTo(c.sinX.r, 0);
        expect(sineX.B).toBeCloseTo(c.sinX.B, 0);
        expect(sineX.C).toBeCloseTo(c.sinX.C, 0);

        expect(sineY.t0).toBeCloseTo(c.sinY.t0, 0);
        expect(sineY.A).toBeCloseTo(c.sinY.A, 0);
        expect(sineY.r).toBeCloseTo(c.sinY.r, 0);
        expect(sineY.B).toBeCloseTo(c.sinY.B, 0);
        expect(sineY.C).toBeCloseTo(c.sinY.C, 0);
    }
});

test('getCircleCenter', () => {
    let cases = [
        // Circle above
        {
            "angle": 0, "rotationVelocity": 60, "velocity": 10,
            "x": 100, "y": 200, "centerX": 100, "centerY": 209.5
        },
        // Circle below
        {
            "angle": 0, "rotationVelocity": -60, "velocity": 10,
            "x": 100, "y": 200, "centerX": 100, "centerY": 190.5
        },
        // Circle left
        {
            "angle": 90, "rotationVelocity": 60, "velocity": 10,
            "x": 100, "y": 200, "centerX": 90.5, "centerY": 200
        },
        {
            "angle": 270, "rotationVelocity": -60, "velocity": 10,
            "x": 100, "y": 200, "centerX": 90.5, "centerY": 200
        },
        // Circle right
        {
            "angle": 90, "rotationVelocity": -60, "velocity": 10,
            "x": 100, "y": 200, "centerX": 109.5, "centerY": 200
        },
        {
            "angle": 270, "rotationVelocity": 60, "velocity": 10,
            "x": 100, "y": 200, "centerX": 109.5, "centerY": 200
        },
        // 60 degrees angle
        {
            "angle": 60, "rotationVelocity": 60, "velocity": 10,
            "x": 100, "y": 200, "centerX": 91.7, "centerY": 204.7
        },
        {
            "angle": 60, "rotationVelocity": -60, "velocity": 10,
            "x": 100, "y": 200, "centerX": 108.3, "centerY": 195.2
        },
        // 120 degrees angle
        {
            "angle": 120, "rotationVelocity": 60, "velocity": 10,
            "x": 100, "y": 200, "centerX": 91.7, "centerY": 195.2
        },
        {
            "angle": 120, "rotationVelocity": -60, "velocity": 10,
            "x": 100, "y": 200, "centerX": 108.3, "centerY": 204.7
        },
        // 200 degrees angle
        {
            "angle": 200, "rotationVelocity": 60, "velocity": 10,
            "x": 100, "y": 200, "centerX": 103.2, "centerY": 191
        },
        {
            "angle": 200, "rotationVelocity": -60, "velocity": 10,
            "x": 100, "y": 200, "centerX": 96.7, "centerY": 208.9
        },
    ];

    for (let c of cases) {
        let [centerX, centerY] = getCircleCenter(c.velocity, c.rotationVelocity, c.angle, c.x, c.y);

        expect(centerX).toBeCloseTo(c.centerX, 0);
        expect(centerY).toBeCloseTo(c.centerY, 0);
    }
});

test('normalize angles', () => {
    let cases = [
        {'angleIn': -40, 'angleOut': 320},
        {'angleIn': 0, 'angleOut': 0},
        {'angleIn': 10, 'angleOut': 10},
        {'angleIn': -180, 'angleOut': 180},
        {'angleIn': -1840, 'angleOut': 320},
        {'angleIn': 1840, 'angleOut': 40},
        {'angleIn': 360, 'angleOut': 0},
        {'angleIn': -360, 'angleOut': -0},
    ];

    for (let c of cases) {
        let deg = normalizeAngleDeg(c.angleIn);
        let rad = normalizeAngleRad(toRadians(c.angleIn));

        expect(deg).toBe(c.angleOut);
        expect(rad).toBeCloseTo(toRadians(c.angleOut), 4);
    }
});