import {round} from "./round";


test('round', () => {
    let cases = [
        {"value": 1.000, "numDecimals": 2, "result": 1.00},
        {"value": 1.2222, "numDecimals": 2, "result": 1.22},
        {"value": 1.2222, "numDecimals": 1, "result": 1.2},
        {"value": 1.2222, "numDecimals": 0, "result": 1},
        {"value": 1.6, "numDecimals": 0, "result": 2},

    ];
    for (let c of cases) {
        let result = round(c.value, c.numDecimals);

        expect(result).toBe(c.result);
    }
});