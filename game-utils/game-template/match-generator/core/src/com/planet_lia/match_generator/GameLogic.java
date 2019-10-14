package com.planet_lia.match_generator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.planet_lia.match_generator.libs.BotCommand;
import com.planet_lia.match_generator.libs.BotResponse;
import com.planet_lia.match_generator.libs.Timer;
import com.planet_lia.match_generator.logic.Assets;
import com.planet_lia.match_generator.logic.GameConfig;
import com.planet_lia.match_generator.logic.api.InitialMessage;
import com.planet_lia.match_generator.logic.api.MatchStateMessage;
import com.planet_lia.match_generator.logic.api.MoveCommand;
import com.planet_lia.match_generator.logic.api.UnitData;
import com.planet_lia.match_generator.logic.entities.Background;
import com.planet_lia.match_generator.logic.entities.Coin;
import com.planet_lia.match_generator.logic.entities.Unit;
import com.planet_lia.match_generator.logic.MatchTools;

public class GameLogic {

    MatchTools tools;
    SpriteBatch batch;

    Background background;
    Unit[] units = new Unit[2];
    Coin coin;

    int updatesCount = 0;

    /**
     * Setup your game logic
     * @param tools match related details
     */
    public GameLogic(MatchTools tools) {
        this.tools = tools;

        // Register supported bot commands
        Class[] supportedBotCommands = new Class[]{MoveCommand.class};
        this.tools.server.registerBotCommands(supportedBotCommands);

        // If this is a debug mode, setup graphics
        if (tools.args.debug) {
            setupGraphics();
        }

        // Create game entities
        background = new Background();
        float unitSize = GameConfig.values.unitSize;
        units[0] = new Unit(unitSize / 2f, unitSize / 2f);
        units[1] = new Unit(
                GameConfig.values.mapWidth - unitSize / 2f,
                GameConfig.values.mapHeight - unitSize / 2f);
        coin = new Coin(units);

        // Register entities so that their details will be displayed when they will be clicked on
        if (tools.entityDetailsSystem != null) {
            for (Unit unit : units) {
                tools.entityDetailsSystem.registerEntity(unit);
            }
            tools.entityDetailsSystem.registerEntity(coin);
        }

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
    public void update(Timer timer, float delta) {
        checkIfMatchIsFinished(timer);

        // Update coin
        coin.update(timer, units);

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
                handleBotResponse(botIndex, botResponse);
            }
        }

        updatesCount++;
    }

    /** In debug mode it draws the match state to the screen */
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

    private void checkIfMatchIsFinished(Timer timer) {
        for (int botIndex = 0; botIndex < tools.botsDetails.length; botIndex++) {
            Unit unit = units[botIndex];
            if (coin.x == unit.getX() && coin.y == unit.getY()) {
                // Unit has won (to make this example simple we just ignore what
                // happens if both units are at the same position as the coin
                // at the same time)
                System.out.printf("Bot '%s' has won!\n", tools.botsDetails[botIndex].botName);
                Gdx.app.exit();
            }
        }
    }

    private boolean shouldSendRequestsToBots(int updateIndex) {
        return updateIndex % GameConfig.values.gameUpdatesPerBotRequest == 0;
    }

    private void sendMatchState(float time, int botIndex) {
        UnitData unitData = new UnitData(units[botIndex].getX(), units[botIndex].getY());
        MatchStateMessage message = new MatchStateMessage(time, unitData);
        tools.server.send(botIndex, message);
    }

    private void handleBotResponse(int botIndex, BotResponse botResponse) {
        for (BotCommand command : botResponse.commands) {
            if (command instanceof MoveCommand) {
                // Handle move command
                MoveCommand moveCommand = (MoveCommand) command;
                Unit unit = units[botIndex];
                unit.move(moveCommand.direction);
            }
            else {
                System.err.printf("Bot '%s' provided an invalid command\n", tools.botsDetails[botIndex].botName);
            }
        }
    }

    /** Cleanup necessary stuff before exiting */
    public void dispose() {
        Assets.dispose();
    }
}
