import {LinearSection} from "./linear";
import {Curve} from "../curve";

test('curve linear sections', () => {
    let curve = new Curve<number>();
    curve.add(new LinearSection(0, 10));
    curve.add(new LinearSection(1, 20));
    curve.add(new LinearSection(2, 30));
    curve.add(new LinearSection(3, 100));

    expect(curve.getValue(0)).toBe(10); // At the beginning
    expect(curve.getValue(0.5)).toBe(15);
    expect(curve.getValue(1.8)).toBe(28);
    expect(curve.getValue(3)).toBe(100);
    expect(curve.getValue(4)).toBe(100); // After all elements returns last value
});

test('LinearSection parse', () => {
    let cases = [
        {"dict": {"endTime": 1, "endRangeValue": 10}, "ok": true},
        {"dict": {}, "ok": false},
    ];

    for (let c of cases) {
        if (c.ok) {
            let section = LinearSection.parse(c.dict);

            expect(section.endTime).toBe(c.dict.endTime);
            expect(section.endRangeValue).toBe(c.dict.endRangeValue);
        } else {
            expect(() => LinearSection.parse(c.dict)).toThrow(Error);
        }
    }
});

test('LinearSection toDict', () => {
    expect(JSON.stringify(LinearSection.toDict("100", "X", 1, 10)))
        .toBe(JSON.stringify({
            "type": LinearSection.name,
            "entityId": "100",
            "attribute": "X",
            "endTime": 1,
            "endRangeValue": 10
        }))
});