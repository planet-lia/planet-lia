import {Curve, findEndTime} from "./curve";
import {LinearSection} from "./sections/linear";
import {StepSection} from "./sections/step";

test('curve empty', () => expect(() => (new Curve()).getValue(1)).toThrow(RangeError));

test('curve len 1', () => {
    let curve = new Curve<number>();
    curve.add(new LinearSection(0, 10));

    expect(curve.getValue(0)).toBe(10);
    expect(curve.getValue(1)).toBe(10);
});

test('curve order', () => {
    let curve = new Curve<number>();
    curve.add(new LinearSection(0, 10));
    curve.add(new LinearSection(5, 10));

    expect(() => curve.add(new LinearSection(2, 20))).toThrow(Error);
});

test('curve time to small', () => {
    let curve = new Curve<number>();
    curve.add(new LinearSection(0, 10));

    // toThrow expects a wrapper function
    expect(() => curve.getValue(-1)).toThrow(RangeError);
});

test('curve start and end time', () => {
    let curve = new Curve<number>();

    expect(() => curve.getStartTime()).toThrow(Error);
    expect(() => curve.getEndTime()).toThrow(Error);

    curve.add(new LinearSection(5, 10));
    curve.add(new LinearSection(6, 10));

    expect(curve.getStartTime()).toBe(5);
    expect(curve.getEndTime()).toBe(6);
});

test('curve mixed sections', () => {
    let curve = new Curve<number>();
    curve.add(new LinearSection(0, 10));
    curve.add(new LinearSection(1, 20));
    curve.add(new StepSection(2, 30));
    curve.add(new LinearSection(3, 40));

    expect(curve.getValue(0)).toBe(10);
    expect(curve.getValue(0.5)).toBe(15);
    expect(curve.getValue(1.8)).toBe(20);
    expect(curve.getValue(2.5)).toBe(35);
    expect(curve.getValue(4)).toBe(40);
});

test('curve findEndTime', () => {
    let x = new Curve<number>();
    let y = new Curve<number>();
    let opacity = new Curve<number>();
    let visibility = new Curve<number>();

    x.add(new StepSection(0, 0));
    x.add(new StepSection(1, 0));
    y.add(new StepSection(1, 0));
    opacity.add(new StepSection(3.2, 0));
    visibility.add(new StepSection(2, 0));

    expect(findEndTime([x, y, opacity, visibility])).toBe(3.2);
});