/**
 * Abstract class that different Sections need to implement
 * in order to be used in curves.
 */
export abstract class Section<T> {

    // Cannot use YourClassName.name due to minification problems in build
    // Override it in a class that extends this
    static NAME: string;

    // The time at the end of the sections.
    endTime: number;

    // Holds the last value in the sections, that the next sections
    // in the curve will use to calculate intermediate values.
    endRangeValue: T;

    constructor(endTime: number, endRangeValue: T) {
        this.endTime = endTime;
        this.endRangeValue = endRangeValue;
    }

    // Return a value within this sections based on custom logic.
    abstract getValue(time: number, prevSection: Section<any>): T;
}

export function errorIfUndefined(values: any[]) {
    for (let i = 0; i < values.length; i++) {
        let value = values[i];

        if (value === undefined) {
            throw new Error(`Value with index ${i} is undefined.`);
        }
    }
}