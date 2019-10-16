import {errorIfUndefined} from "./curves/sections/section";

export class BotDetails {

    botName: string;
    teamIndex: number;
    color: string;
    rank: number;
    totalCpuTime: number;
    numberOfTimeouts: number;
    disqualified: boolean;
    disqualificationTime: number;
    disqualificationReason: string;

    constructor(botName: string,
                teamIndex: number,
                color: string,
                rank: number,
                totalCpuTime: number,
                numberOfTimeouts: number,
                disqualified: boolean,
                disqualificationTime: number,
                disqualificationReason: string) {
        this.botName = botName;
        this.teamIndex = teamIndex;
        this.color = color;
        this.rank = rank;
        this.totalCpuTime = totalCpuTime;
        this.numberOfTimeouts = numberOfTimeouts;
        this.disqualified = disqualified;
        this.disqualificationTime = disqualificationTime;
        this.disqualificationReason = disqualificationReason;
    }

    static parse(dict: any): BotDetails {
        errorIfUndefined([dict.botName, dict.teamIndex, dict.color, dict.rank, dict.totalCpuTime,
            dict.numberOfTimeouts, dict.disqualified, dict.disqualificationTime, dict.disqualificationReason]);
        return new BotDetails(dict.botName, dict.teamIndex, dict.color, dict.rank, dict.totalCpuTime,
            dict.numberOfTimeouts, dict.disqualified, dict.disqualificationTime, dict.disqualificationReason);
    }
}