import {Section} from "./sections/section";

/**
 * A curve built out of sections. You need to specify of which type all the sections must be.
 */
export class Curve<T = never> {
    sections: Section<T>[] = [];

    add(section: Section<T>) {
        if (this.sections.length > 0) {

            let lastSection = this.sections[this.sections.length - 1];

            if (lastSection.endTime > section.endTime) {
                throw new Error(`Section time ${section.endTime} must be > than ` +
                    `previous times in this curve.`);
            }
        }

        this.sections.push(section);
    }

    /**
     * Returns value represented in the curve at specified time.
     * Note that the first sections can only be queried by it's endTime as it
     * has no previous Section and thus can't calculate the intermediate values.
     *
     * @param {number} time
     * @returns {T} - a value in the curve represented by specified time
     */
    getValue(time: number): T {
        let sectionIndex = this.getSectionIndex(time);
        let section = this.sections[sectionIndex];

        // If first we don't do interpolation
        if (sectionIndex === 0) {
            return section.endRangeValue;
        }
        // This only happens when section is last
        if (section.endTime <= time) {
            return section.endRangeValue;
        }

        return section.getValue(time, this.sections[sectionIndex - 1]);
    }

    getPreviousSectionEndTime(time: number): number {
        let sectionIndex = this.getSectionIndex(time);
        let section = this.sections[sectionIndex];

        if (sectionIndex === 0) {
            return section.endTime;
        }
        if (section.endTime <= time) {
            return section.endTime;
        }
        return this.sections[sectionIndex - 1].endTime;
    }

    /**
     * Returns current section time or null if the time is larger
     * than the last section end time
     */
    getCurrentSectionEndTime(time: number): number | null {
        let sectionIndex = this.getSectionIndex(time);
        let section = this.sections[sectionIndex];
        if (section.endTime < time) return null;
        return section.endTime;
    }

    getSectionIndex(time: number): number {
        if (this.sections.length === 0) {
            throw new RangeError(`Curve is empty.`);
        }
        // Time to small
        if (this.sections[0].endTime > time) {
            throw new RangeError(`Current time ${time} is smaller than the end time of the first section` +
                ` ${this.sections[0].endTime}.`)
        }
        // Time to large
        let lastSection = this.sections[this.sections.length - 1];
        if (lastSection.endTime <= time) {
            return this.sections.length - 1;
        }
        // First section
        let firstSection = this.sections[0];
        if (firstSection.endTime === time) {
            return 0;
        }

        let leftIndex = 0;
        let rightIndex = this.sections.length - 1;

        // Bisection (binary search)
        while (rightIndex - leftIndex > 1) {
            let middleIndex = Math.floor(leftIndex + (rightIndex - leftIndex) / 2);

            let middleSection = this.sections[middleIndex];

            if (middleSection.endTime === time) return middleIndex;
            else if (middleSection.endTime < time) leftIndex = middleIndex;
            else rightIndex = middleIndex;
        }

        let correctIndex = (time <= this.sections[leftIndex].endTime) ? leftIndex : rightIndex;
        return correctIndex;
    }

    /** Returns the time when the curve starts. */
    getStartTime(): number {
        if (this.sections.length === 0) throw new Error(`Curve is empty.`);
        else return this.sections[0].endTime;
    }

    /** Returns the end time of the last sections in the curve. */
    getEndTime(): number {
        if (this.sections.length === 0) throw new Error(`Curve is empty.`);
        else return this.sections[this.sections.length - 1].endTime;
    }

    /** Returns end times of all sections in the curve. */
    getSectionsEndTimes(): number[] {
        let endTimes: number[] = [];
        this.sections.forEach(section => endTimes.push(section.endTime));
        return endTimes;
    }

    numberOfSections(): number {
        return this.sections.length;
    }

    isEmpty(): boolean {
        return this.sections.length === 0;
    }
}

export function findEndTime(curves: Curve<any>[]): number {
    let endTime = 0;
    curves.forEach(curve => {
        if (!curve.isEmpty()) {
            let curveEndTime = curve.getEndTime();
            if (curveEndTime > endTime) endTime = curveEndTime;
        }
    });
    return endTime;
}

export function findStartTime(curves: Curve<any>[]): number {
    let startTime = -1;
    curves.forEach(curve => {
        if (!curve.isEmpty()) {
            let curveStartTime = curve.getStartTime();
            if (curveStartTime < startTime || startTime === -1) startTime = curveStartTime;
        }
    });
    return startTime;
}

export function throwErrorIfEmpty(curve: Curve<any>, curveName: string) {
    if (curve.isEmpty()) {
        throw new Error(`Curve ${curveName} should not be empty.`);
    }
}