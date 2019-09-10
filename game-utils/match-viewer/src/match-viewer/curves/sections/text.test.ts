import {Curve} from "../curve";
import {TextSection} from "./text";

test('curve textDisplay sections', () => {
    let curve = new Curve<string>();
    curve.add(new TextSection(0, "image1"));
    curve.add(new TextSection(1, "image2"));
    curve.add(new TextSection(2, "image3"));

    expect(curve.getValue(0)).toBe("image1");
    expect(curve.getValue(0.5)).toBe("image1");
    expect(curve.getValue(1.8)).toBe("image2");
    expect(curve.getValue(3)).toBe("image3");
});

test('TextSection parse', () => {
    let cases = [
        {"dict": {"endTime": 1, "text": "warrior.png"}, "ok": true},
        {"dict": {}, "ok": false},
    ];

    for (let c of cases) {
        if (c.ok) {
            let section = TextSection.parse(c.dict);

            expect(section.endTime).toBe(c.dict.endTime);
            expect(section.endRangeValue).toBe(c.dict.text);
        } else {
            expect(() => TextSection.parse(c.dict)).toThrow(Error);
        }
    }
});

test('TextSection toDict', () => {
    expect(JSON.stringify(TextSection.toDict("100", "TEXTURE", 1, "warrior.png")))
        .toBe(JSON.stringify({
            "type": TextSection.name,
            "entityId": "100",
            "attribute": "TEXTURE",
            "endTime": 1,
            "text": "warrior.png"
        }))
});