import {SineEquation} from "./sineEquation";

export function getCircularMovementParametricEquations(time: number, velocity: number, rotationVelocity: number,
                                                       angle: number, x: number, y: number): [SineEquation, SineEquation] {
    angle = normalizeAngleDeg(angle);
    let rotationVelocityRad = toRadians(rotationVelocity);

    // Radius of the circle
    let r = getCircleRadius(velocity, rotationVelocity);

    // Center points of the circle
    let [centerX, centerY] = getCircleCenter(velocity, rotationVelocity, angle, x, y);

    // Time that the object needs to finish one full lap
    let T = (2 * Math.PI) / Math.abs(rotationVelocityRad);

    // Offsets where the object begins the movement
    let timeOffset = Math.atan((y - centerY) / (x - centerX)) / rotationVelocityRad;
    if ((rotationVelocityRad < 0 && angle > 0 && angle < 180) ||
        (rotationVelocityRad >= 0 && angle > 180 && angle < 360)) {
        timeOffset += T / 2;
    }
    let offsetX = rotationVelocityRad * timeOffset + Math.PI / 2;
    let offsetY = rotationVelocityRad * timeOffset;

    // Build sine equations for both coordinates
    let sineX = new SineEquation(time, centerX, r, rotationVelocityRad, offsetX);
    let sineY = new SineEquation(time, centerY, r, rotationVelocityRad, offsetY);

    return [sineX, sineY];
}

export function getCircleCenter(velocity: number, rotationVelocityDeg: number,
                                angle: number, x: number, y: number): [number, number] {

    angle = normalizeAngleDeg(angle);
    let r = getCircleRadius(velocity, rotationVelocityDeg);

    // Parametrize velocity
    let velocityVectorX = velocity * Math.cos(toRadians(angle));
    let velocityVectorY = velocity * Math.sin(toRadians(angle));

    // Make a unit vector from velocity vector
    let velocityUnitVectorX = velocityVectorX / velocity;
    let velocityUnitVectorY = velocityVectorY / velocity;

    let invert = (rotationVelocityDeg < 0) ? 1 : -1;

    // Rotate the velocity unit vector towards the center of the circle
    let eX = velocityUnitVectorY * invert;
    let eY = -velocityUnitVectorX * invert;

    // Calculate the center of the circle
    let centerX = x + r * eX;
    let centerY = y + r * eY;

    return [centerX, centerY];
}

export function getCircleRadius(velocity: number, rotationVelocity: number) {
    return velocity / Math.abs(toRadians(rotationVelocity));
}

export function toRadians(degrees: number): number {
    return degrees * (Math.PI / 180);
}

export function normalizeAngleDeg(angle: number): number {
    angle %= 360;
    if (angle < 0) angle += 360;
    return angle;
}

export function normalizeAngleRad(angle: number): number {
    angle %= (Math.PI * 2);
    if (angle < 0) angle += Math.PI * 2;
    return angle;
}