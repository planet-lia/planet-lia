import {errorIfUndefined} from "./curves/sections/section";

export class MatchDetail {

    description: string;
    value: any;

    constructor(description: string, value: any) {
        this.description = description;
        this.value = value;
    }

    static parse(dict: any): MatchDetail {
        errorIfUndefined([dict.description, dict.value]);
        return new MatchDetail(dict.description, dict.value);
    }
}