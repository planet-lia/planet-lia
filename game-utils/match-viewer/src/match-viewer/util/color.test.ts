import {colorToNumber, colorToString} from "./color";

test('color', () => {
    let cases = [
        {"str": "#FF0000", "num": 0xFF0000},
        {"str": "#FA4", "num": 0xFA4},
    ];

    for (let c of cases) {
        let strOut = colorToString(c.num);
        let numOut = colorToNumber(c.str);

        expect(strOut).toBe(c.str);
        expect(numOut).toBe(c.num);
    }
});