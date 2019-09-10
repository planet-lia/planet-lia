import {Curve} from "../curve";
import {StepSection} from "./step";

test('curve step sections', () => {
    let curve = new Curve<number>();
    curve.add(new StepSection(0, 10));
    curve.add(new StepSection(1, 20));
    curve.add(new StepSection(2, 30));
    curve.add(new StepSection(3, 5));

    expect(curve.getValue(0)).toBe(10);
    expect(curve.getValue(0.5)).toBe(10);
    expect(curve.getValue(1.8)).toBe(20);
    expect(curve.getValue(3)).toBe(5);
    expect(curve.getValue(4)).toBe(5);
});

test('StepSection parse', () => {
    let cases = [
        {"dict": {"endTime": 1, "endRangeValue": 10}, "ok": true},
        {"dict": {}, "ok": false},
    ];

    for (let c of cases) {
        if (c.ok) {
            let section = StepSection.parse(c.dict);

            expect(section.endTime).toBe(c.dict.endTime);
            expect(section.endRangeValue).toBe(c.dict.endRangeValue);
        } else {
            expect(() => StepSection.parse(c.dict)).toThrow(Error);
        }
    }
});

test('StepSection toDict', () => {
    expect(JSON.stringify(StepSection.toDict("100", "X", 1, 10)))
        .toBe(JSON.stringify({
            "type": StepSection.name,
            "entityId": "100",
            "attribute": "X",
            "endTime": 1,
            "endRangeValue": 10
        }))
});