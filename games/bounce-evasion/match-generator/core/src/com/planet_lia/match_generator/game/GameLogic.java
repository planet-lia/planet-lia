package com.planet_lia.match_generator.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.planet_lia.match_generator.game.api.commands.Direction;
import com.planet_lia.match_generator.game.entities.Saw;
import com.planet_lia.match_generator.libs.*;
import com.planet_lia.match_generator.libs.replays.*;
import com.planet_lia.match_generator.game.api.InitialMessage;
import com.planet_lia.match_generator.game.api.MatchStateMessage;
import com.planet_lia.match_generator.game.api.commands.MoveCommand;
import com.planet_lia.match_generator.game.entities.Background;
import com.planet_lia.match_generator.game.entities.Coin;
import com.planet_lia.match_generator.game.entities.Unit;

import java.util.ArrayList;

public class GameLogic extends GameLogicBase {

    Replay replay = ReplayManager.newReplay();

    SpriteBatch batch;
    SpriteBatch batchHud;

    Background background;
    Unit[] units = new Unit[2];
    Coin[] coins = new Coin[GameConfig.values.numberOfCoins];
    ArrayList<Saw> saws = new ArrayList<>();

    int updatesCount = 0;
    long startTime;

    Hud hud;

    /**
     * Setup your game logic
     * @param tools match related details
     */
    @Override
    public void setup(MatchTools tools) {
        super.setup(tools);
        startTime = TimeUtils.millis();

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
        units[0] = new Unit(0, 0, 0, replay, Assets.unitYellow, Direction.RIGHT);
        units[1] = new Unit(1, mapWidth - 1, mapHeight -1, replay, Assets.unitGreen, Direction.LEFT);

        for (int i = 0; i < coins.length; i += 2) {
            int[] pos1 = getNewCoinPosition();
            int[] pos2 = getSymmetricalPosition(pos1[0], pos1[1]);
            coins[i] = new Coin(i, replay, pos1[0], pos1[1]);
            coins[i + 1] = new Coin(i + 1, replay, pos2[0], pos2[1]);
        }

        //spawn2Saws(0f);

        // Register entities so that their details will be displayed
        // when they will be clicked on
        if (tools.entityDetailsSystem != null) {
            for (Unit unit : units) {
                tools.entityDetailsSystem.registerEntity(unit);
            }
            for (Coin coin : coins) {
                tools.entityDetailsSystem.registerEntity(coin);
            }
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

        hud = new Hud(replay, tools.botsDetails);

        // Send initial information to bots
        tools.server.send(0, InitialMessage.create(background, units[0], units[1], coins, saws));
        tools.server.send(1, InitialMessage.create(background, units[1], units[0], coins, saws));
        tools.server.waitForBotsToRespond();
    }

    /** Load assets, prepare graphics objects, camera etc. */
    private void setupGraphics() {
        Assets.load();
        batch = new SpriteBatch();
        batchHud = new SpriteBatch();

        // Center camera over the middle of the map
        tools.gameViewport.getCamera().position.x = GameConfig.values.mapWidth / 2f;
        tools.gameViewport.getCamera().position.y = GameConfig.values.mapHeight / 2f;
    }

    private void spawn2Saws(float time) {
        saws.add(new Saw(time, saws.size(), replay, 5, 0, Saw.SawDirection.UP_RIGHT));
        int[] saw2Pos = getSymmetricalPosition(5, 0);
        saws.add(new Saw(time,saws.size(), replay, saw2Pos[0], saw2Pos[1], Saw.SawDirection.DOWN_LEFT));

        if (tools.entityDetailsSystem != null) {
            tools.entityDetailsSystem.registerEntity(saws.get(saws.size() - 2));
            tools.entityDetailsSystem.registerEntity(saws.get(saws.size() - 1));
        }
    }

    /** Called repeatedly to update match state */
    @Override
    public void update(Timer timer, float delta) {
        hud.updateTime(timer.getTime());

        // Print out time every 10 seconds
        if (updatesCount % 20 == 0) {
            System.out.printf("%d ticks generated.\n", updatesCount);
        }

        // Update entities
        for (Unit unit : units) {
            unit.update(timer.getTime(), delta);
        }

        for (Saw saw : saws) {
            saw.update(timer.getTime(), delta);
        }

        if (updatesCount % GameConfig.values.sawSpawnUpdatesDelay == 0) {
            spawn2Saws(timer.getTime());
        }

        checkForCoinPickUp(timer.getTime());
        checkForSawUnitCollisions(timer.getTime());

        int winningTeamIndex = getWinningTeamIndex();
        if (winningTeamIndex != -1) {
            matchOver(winningTeamIndex, timer.getTime());
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
                if (!tools.server.isDisqualified(botIndex)) {
                    BotResponse botResponse = tools.server.getLastResponseData(botIndex, BotResponse.class);
                    handleBotResponse(botIndex, botResponse, timer.getTime());
                }
            }
        }

        updatesCount++;
    }

    private void checkForSawUnitCollisions(float time) {
        for (int botIndex = 0; botIndex < units.length; botIndex++) {
            Unit unit = units[botIndex];
            for (Saw saw : saws) {
                if (unit.x == saw.x && unit.y == saw.y) {
                    unit.removeLife(time);
                    if (botIndex == 0) hud.decreaseYellowLives();
                    else hud.decreaseGreenLives();
                }
            }
        }
    }

    /** In debug mode it draws the match state to the screen */
    @Override
    public void draw() {
        batch.setProjectionMatrix(tools.gameViewport.getCamera().combined);
        batchHud.setProjectionMatrix(tools.gameHudViewport.getCamera().combined);

        batch.begin();

        background.draw(batch);

        for (Coin coin : coins) {
            coin.draw(batch);
        }

        for (Unit unit : units) {
            unit.draw(batch);
        }

        for (Saw saw : saws) {
            saw.draw(batch);
        }
        hud.draw(batch);

        batch.end();


        batchHud.begin();
        hud.drawFont(batchHud, tools.gameHudViewport);
        batchHud.end();
    }

    private void checkForCoinPickUp(float time) {
        for (int botIndex = 0; botIndex < tools.botsDetails.length; botIndex++) {
            Unit unit = units[botIndex];

            for (Coin coin : coins) {
                if (coin.x == unit.x && coin.y == unit.y) {
                    // Pick up the coin
                    unit.addPoint(time);
                    if (botIndex == 0) hud.increaseYellowPoints();
                    else hud.increaseGreenPoints();
                    coin.isPicked = true;
                }
            }
        }

        // Move coin here so that multiple units can pick the same coin if
        // they come to the tile of the coin at the same time
        for (Coin coin : coins) {
            if (coin.isPicked) {
                coin.isPicked = false;

                int[] newPosition = getNewCoinPosition();
                coin.changeLocation(time, newPosition[0], newPosition[1]);
            }
        }
    }

    private int getWinningTeamIndex() {
        Unit unit1 = units[0];
        Unit unit2 = units[1];

        // If both units are dead choose the bot with more points
        // or randomly if both have the same number of points
        if (unit1.lives == 0 && unit2.lives == 0) {
            int winnerIndex = GameConfig.values.random.nextInt(2);
            if (unit1.points > unit2.points) winnerIndex = 0;
            else if (unit2.points > unit1.points) winnerIndex = 1;

            return tools.botsDetails[winnerIndex].teamIndex;
        }
        // If unit1 is dead and unit2 has more or the same amount of points
        // then unit 2 won
        else if (unit1.lives == 0 && unit2.points >= unit1.points) {
            return tools.botsDetails[1].teamIndex;
        }
        // If unit2 is dead and unit1 has more or the same amount of points
        // then unit 1 won
        else if (unit2.lives == 0 && unit1.points >= unit2.points) {
            return tools.botsDetails[0].teamIndex;
        }

        return -1;
    }

    private int[] getNewCoinPosition() {
        while (true) {
            int x = GameConfig.values.random.nextInt(GameConfig.values.mapWidth);
            int y = GameConfig.values.random.nextInt(GameConfig.values.mapHeight);
            if (background.tiles.get(y).get(x) != null) {
                if (isPositionFarEnoughFromUnits(x, y) &&
                        isPositionFarEnoughFromCoins(x, y)) {
                    return new int[]{x, y};
                }
            }
        }
    }

    private int[] getSymmetricalPosition(int x, int y) {
        return new int[]{GameConfig.values.mapWidth - x - 1, GameConfig.values.mapHeight - y - 1};
    }

    private boolean isPositionFarEnoughFromUnits(float x, float y) {
        for (Unit unit : units) {
            if (Vector2.dst(unit.getX(), unit.getY(), x, y) < GameConfig.values.minSpawnCoinDistance) {
                return false;
            }
        }
        return true;
    }

    private boolean isPositionFarEnoughFromCoins(float x, float y) {
        for (Coin coin : coins) {
            if (coin == null) continue;
            if (Vector2.dst(coin.getX(), coin.getY(), x, y) < GameConfig.values.minSpawnCoinDistance) {
                return false;
            }
        }
        return true;
    }

    private void matchOver(int winningTeamIndex, float time) {
        // We have found a winner
        System.out.printf("Bot '%s' (index: %d) has won!\n", tools.botsDetails[winningTeamIndex].botName, winningTeamIndex);
        System.out.printf("Bot '%s' (index: %d) had %d points and %d lives left.\n",
                tools.botsDetails[0].botName, 0, units[0].points, units[0].lives);
        System.out.printf("Bot '%s' (index: %d) had %d points and %d lives left.\n",
                tools.botsDetails[1].botName, 1, units[1].points, units[1].lives);
        System.out.println("Time elapsed in seconds: " + TimeUtils.timeSinceMillis(startTime) / 1000f);

        // Create a final order of the teams
        int loosingTeamIndex = (winningTeamIndex == 0) ? 1 : 0;
        int[] teamsFinalOrder = new int[]{winningTeamIndex, loosingTeamIndex};

        for (Saw saw : saws) {
            saw.savePositionAndRotation(time);
        }

        hud.writeEndGameAnimation(winningTeamIndex);

        // Save replay file
        ReplayManager.saveReplayFile(replay, teamsFinalOrder, tools.server, Args.values.replay);

        System.exit(0);
    }

    private boolean shouldSendRequestsToBots(int updateIndex) {
        return updateIndex % GameConfig.values.gameUpdatesPerBotRequest == 0;
    }

    private void sendMatchState(float time, int botIndex) {
        int opponentBotIndex = (botIndex == 0) ? 1 : 0;
        MatchStateMessage message = MatchStateMessage.create(time, units[botIndex], units[opponentBotIndex], coins, saws);
        tools.server.send(botIndex, message);
    }

    private void handleBotResponse(int botIndex, BotResponse botResponse, float time) {
        for (BotCommand command : botResponse.commands) {
            if (command instanceof MoveCommand) {
                // Handle move command
                MoveCommand moveCommand = (MoveCommand) command;
                Unit unit = units[botIndex];
                unit.move(moveCommand.direction, background);
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
        batchHud.dispose();
        batch.dispose();
    }
}
