package com.planet_lia.match_generator.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.planet_lia.match_generator.libs.*;
import com.planet_lia.match_generator.libs.replays.*;
import com.planet_lia.match_generator.game.api.InitialMessage;
import com.planet_lia.match_generator.game.api.MatchStateMessage;
import com.planet_lia.match_generator.game.api.MoveCommand;
import com.planet_lia.match_generator.game.api.UnitData;
import com.planet_lia.match_generator.game.entities.Background;
import com.planet_lia.match_generator.game.entities.Coin;
import com.planet_lia.match_generator.game.entities.Unit;

public class GameLogic extends GameLogicBase {

    Replay replay = ReplayManager.newReplay();

    SpriteBatch batch;

    Background background;
    Unit[] units = new Unit[2];
    Coin coin;

    int updatesCount = 0;

    /**
     * Setup your game logic
     * @param tools match related details
     */
    @Override
    public void setup(MatchTools tools) {
        super.setup(tools);

        // Register supported bot commands
        Class[] supportedBotCommands = new Class[]{MoveCommand.class};
        this.tools.server.registerBotCommands(supportedBotCommands);

        // If this is a debug mode, setup graphics
        if (Args.values.debug) {
            setupGraphics();
        }

        int mapWidth = GameConfig.values.mapWidth;
        int mapHeight = GameConfig.values.mapHeight;

        // Create game entities
        background = new Background(replay);
        float unitSize = GameConfig.values.unitSize;
        units[0] = new Unit(0, unitSize / 2f, unitSize / 2f,
                replay, Assets.unitYellow);
        units[1] = new Unit(1,
                mapWidth - unitSize / 2f,
                mapHeight - unitSize / 2f,
                replay, Assets.unitGreen);
        coin = new Coin(replay);

        // Register entities so that their details will be displayed
        // when they will be clicked on
        if (tools.entityDetailsSystem != null) {
            for (Unit unit : units) {
                tools.entityDetailsSystem.registerEntity(unit);
            }
            tools.entityDetailsSystem.registerEntity(coin);
        }

        // Create the default camera and set its position in replay file
        String cameraId = "CAMERA_1";
        replay.sections.add(new StepSection(cameraId, CameraAttribute.X, 0f, mapWidth / 2f));
        replay.sections.add(new StepSection(cameraId, CameraAttribute.Y, 0f, mapHeight / 2f));

        // Store the size of the map as a match detail
        // Check the documentation for all the details about the replay files format:
        // - https://github.com/planet-lia/planet-lia/blob/master/game-utils/match-viewer/docs/writing_replay_files.md#replay-file-format
        replay.matchDetails.add(new MatchDetail(
                "Map size",
                GameConfig.values.mapWidth + " x " + GameConfig.values.mapHeight));

        // Send initial information to bots
        tools.server.sendToAll(new InitialMessage());
        tools.server.waitForBotsToRespond();
    }

    /** Load assets, prepare graphics objects, camera etc. */
    private void setupGraphics() {
        Assets.load();
        batch = new SpriteBatch();

        // Center camera over the middle of the map
        tools.gameViewport.getCamera().position.x = GameConfig.values.mapWidth / 2f;
        tools.gameViewport.getCamera().position.y = GameConfig.values.mapHeight / 2f;
    }

    /** Called repeatedly to update match state */
    @Override
    public void update(Timer timer, float delta) {
        int winningTeamIndex = getWinningTeamIndex();
        if (winningTeamIndex != -1) {
            matchOver(winningTeamIndex);
        }

        // Update entities
        for (Unit unit : units) {
            unit.update(timer.getTime(), delta);
        }

        // Send new state to bots
        if (shouldSendRequestsToBots(updatesCount)) {
            // Send match state to all bots
            for (int botIndex = 0; botIndex < tools.botsDetails.length; botIndex++) {
                sendMatchState(timer.getTime(), botIndex);
            }

            // Wait for all bots to respond
            tools.server.waitForBotsToRespond();

            // Handle bot responses
            for (int botIndex = 0; botIndex < tools.botsDetails.length; botIndex++) {
                BotResponse botResponse = tools.server.getLastResponseData(botIndex, BotResponse.class);
                handleBotResponse(botIndex, botResponse, timer.getTime());
            }
        }

        updatesCount++;
    }

    /** In debug mode it draws the match state to the screen */
    @Override
    public void draw() {
        // Draw your match here
        // IMPORTANT: Do not update any logic here as this method will
        //            not be called when the debug mode is not enabled

        batch.setProjectionMatrix(tools.gameViewport.getCamera().combined);

        batch.begin();

        // Draw background
        background.draw(batch);

        coin.draw(batch);

        // Draw units
        for (Unit unit : units) {
            unit.draw(batch);
        }

        batch.end();
    }

    private int getWinningTeamIndex() {
        for (int botIndex = 0; botIndex < tools.botsDetails.length; botIndex++) {
            Unit unit = units[botIndex];
            if (coin.x == unit.getX() && coin.y == unit.getY()) {
                // Team has won (to make this example simple we simply ignore what
                // happens if both units are at the same position as the coin
                // at the same time)
                return tools.botsDetails[botIndex].teamIndex;
            }
        }
        return -1;
    }

    private void matchOver(int winningTeamIndex) {
        // We have found a winner
        System.out.printf("Bot '%s' has won!\n", tools.botsDetails[winningTeamIndex].botName);

        // Demonstrate how to add charts to the replay file
        addUnitPositionXChartToReplay();

        // Create a final order of the teams
        int loosingTeamIndex = (winningTeamIndex == 0) ? 1 : 0;
        int[] teamsFinalOrder = new int[]{winningTeamIndex, loosingTeamIndex};

        // Save replay file
        ReplayManager.saveReplayFile(replay, teamsFinalOrder, tools.server, Args.values.replay);

        System.exit(0);
    }

    private void addUnitPositionXChartToReplay() {
        String chartName = "Unit x position";
        Chart chart = new Chart(chartName);

        for (int botIndex = 0; botIndex < tools.botsDetails.length; botIndex++) {
            Unit unit = units[botIndex];
            chart.series.add(new ChartSeriesElement(
                    tools.botsDetails[botIndex].botName,
                    GameConfig.values.general.botColors[botIndex],
                    new CurveRef(unit.eid, TextureEntityAttribute.X)));
        }

        replay.charts.add(chart);
    }

    private boolean shouldSendRequestsToBots(int updateIndex) {
        return updateIndex % GameConfig.values.gameUpdatesPerBotRequest == 0;
    }

    private void sendMatchState(float time, int botIndex) {
        UnitData unitData = new UnitData(units[botIndex].getX(), units[botIndex].getY());
        MatchStateMessage message = new MatchStateMessage(time, unitData);
        tools.server.send(botIndex, message);
    }

    private void handleBotResponse(int botIndex, BotResponse botResponse, float time) {
        for (BotCommand command : botResponse.commands) {
            if (command instanceof MoveCommand) {
                // Handle move command
                MoveCommand moveCommand = (MoveCommand) command;
                Unit unit = units[botIndex];
                unit.move(moveCommand.direction, time);
            }
            else {
                System.err.printf("Bot '%s' provided an invalid command\n", tools.botsDetails[botIndex].botName);
            }
        }
    }

    /** Cleanup necessary stuff before exiting */
    @Override
    public void dispose() {
        Assets.dispose();
    }
}
