const REPO_BASE_URL = "https://raw.githubusercontent.com/liagame"
const SDK_VERSION = "v1.0.0"

export const programmingLanguages = {
  python3: {
      highlighting: "python",
      baseBotUrl: REPO_BASE_URL + "/python3-bot/" + SDK_VERSION + "/my_bot.py"
  },
  java: {
      highlighting: "java",
      baseBotUrl: REPO_BASE_URL + "/java-bot/" + SDK_VERSION + "/src/MyBot.java"
  },
  kotlin: {
      highlighting: "java",
      baseBotUrl: REPO_BASE_URL + "/kotlin-bot/" + SDK_VERSION + "/src/MyBot.kt"
  }
}
