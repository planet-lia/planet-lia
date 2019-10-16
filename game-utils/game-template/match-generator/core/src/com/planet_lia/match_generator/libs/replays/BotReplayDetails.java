package com.planet_lia.match_generator.libs.replays;

public class BotReplayDetails {
    public String botName;
    public int teamIndex;
    public String color;
    public int rank;
    public float totalCpuTime;
    public int numberOfTimeouts;
    public boolean disqualified;
    public float disqualificationTime;
    public String disqualificationReason;

    public BotReplayDetails(String botName,
                            int teamIndex,
                            int rank,
                            String color,
                            float totalCpuTime,
                            int numberOfTimeouts,
                            boolean disqualified,
                            float disqualificationTime,
                            String disqualificationReason) {
        this.botName = botName;
        this.teamIndex = teamIndex;
        this.rank = rank;
        this.color = color;
        this.totalCpuTime = totalCpuTime;
        this.numberOfTimeouts = numberOfTimeouts;
        this.disqualified = disqualified;
        this.disqualificationTime = disqualificationTime;
        this.disqualificationReason = disqualificationReason;
    }
}
