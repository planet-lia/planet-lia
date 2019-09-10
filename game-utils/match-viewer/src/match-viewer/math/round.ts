export function round(value: number, numDecimals: number): number {
    let factor = Math.pow(10, numDecimals);
    return Math.round(value * factor) / factor;
}