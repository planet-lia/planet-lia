# Game: Planetization


## Run

Guide for IntelliJ IDEA:

1. Clone this repository
2. Open the match-generator-base with IntelliJ IDEA
3. After indexing is finished, open up *Run > Edit Configuration*
4. Find a *+* button and click on *Application* option
5. Configure the application
    - Choose a name
    - Set *Main class* to `DesktopLauncher`
    - Set *Working directory* to `<path-to-planet-lia>/planet-lia/game-utils/planetization/`
    - Set *Use Classpath of module* to `desktop_main`
    - Click *Apply*
6. Run the application
 
# Test
Run `./gradlew test`.