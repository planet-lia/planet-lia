package com.planet_lia.match_generator.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.google.gson.Gson;
import com.planet_lia.match_generator.game.api.InitialMessage;
import com.planet_lia.match_generator.game.api.MatchStateMessage;
import com.planet_lia.match_generator.game.api.commands.SendUnitCommand;
import com.planet_lia.match_generator.game.api.commands.SpawnUnitCommand;
import com.planet_lia.match_generator.game.entities.Background;
import com.planet_lia.match_generator.game.entities.Owner;
import com.planet_lia.match_generator.game.entities.Planet;
import com.planet_lia.match_generator.game.entities.Unit;
import com.planet_lia.match_generator.libs.*;
import com.planet_lia.match_generator.libs.replays.*;

import java.util.ArrayList;

public class GameLogic extends GameLogicBase {

    final Owner[] botOwnerByBotIndex = new Owner[]{Owner.GREEN, Owner.RED};

    Replay replay = ReplayManager.newReplay();

    SpriteBatch batch;

    Background background;
    ArrayList<Planet> planets = new ArrayList<>();

    ArrayList<Unit> greenUnits = new ArrayList<>();
    ArrayList<Unit> redUnits = new ArrayList<>();
    public ArrayList<Unit> greenUnitsToRemove = new ArrayList<>();
    public ArrayList<Unit> redUnitsToRemove = new ArrayList<>();

    private int nextRedUnitId = 0;
    private int nextGreenUnitId = 0;

    int updatesCount = 0;

    Gson gson = new Gson();

    float previousTimePrint = 0f;
    long startTime;

    /**
     * Setup the game logic
     * @param tools match related details
     */
    @Override
    public void setup(MatchTools tools) {
        super.setup(tools);
        startTime = TimeUtils.millis();

        // Register supported bot commands
        Class[] supportedBotCommands = new Class[]{SpawnUnitCommand.class, SendUnitCommand.class};
        this.tools.server.registerBotCommands(supportedBotCommands);

        // If this is a debug mode, setup graphics
        if (Args.values.debug) {
            setupGraphics();
        }

        // Create the default camera and set its position in replay file
        int mapWidth = GameConfig.values.mapWidth;
        int mapHeight = GameConfig.values.mapHeight;
        String cameraId = "CAMERA_1";
        replay.sections.add(new StepSection(cameraId, CameraAttribute.X, 0f, mapWidth / 2f));
        replay.sections.add(new StepSection(cameraId, CameraAttribute.Y, 0f, mapHeight / 2f));

        // Setup background
        background = new Background(replay);

        // Setup planets
        int gapHeight = GameConfig.values.planetGapHeight;
        int gapWidth = GameConfig.values.planetGapWidth;
        int offsetY = (int) ((mapHeight - gapHeight * 6) / 2f);
        int offsetX = (int) ((mapWidth - gapWidth * 5) / 2f);
        int planetId = 0;
        for (int y = 0; y <= 6 * gapHeight; y += gapHeight) {
            for (int x = 0; x <= 5 * gapWidth; x += gapWidth) {
                if (GameConfig.values.planetPositionsOnGrid.contains(planetId)) {
                    Planet planet = new Planet(replay, planetId, offsetX + x, offsetY + y, Owner.NONE);
                    planets.add(planet);
                    // Register it so that it will display data on click
                    if (Args.values.debug) {
                        tools.entityDetailsSystem.registerEntity(planet);
                    }
                }
                planetId++;
            }
        }

        // Spawn starting units
        for (int i = 0; i < GameConfig.values.numberOfWorkersOnStart; i++) {
            createUnit(Unit.Type.WORKER, Owner.RED, GameConfig.values.redPlanetOnStart, 0f);
            createUnit(Unit.Type.WORKER, Owner.GREEN, GameConfig.values.greenPlanetOnStart, 0f);
        }

        // Send initial information to bots
//        tools.server.send(0, InitialMessage.create(botOwnerByBotIndex[0], planets, redUnits, greenUnits));
//        tools.server.send(1, InitialMessage.create(botOwnerByBotIndex[1], planets, redUnits, greenUnits));
//        tools.server.waitForBotsToRespond();
    }

    private void createUnit(Unit.Type type, Owner owner, int planetId, float time) {
        if (owner == Owner.RED && redUnits.size() >= GameConfig.values.maxNumberOfUnitsPerTeam
                || owner == Owner.GREEN && greenUnits.size() >= GameConfig.values.maxNumberOfUnitsPerTeam ) {
            return;
        }
        int unitId = generateUnitId(owner);
        Planet planet = findPlanetWithId(planetId);
        Unit unit = new Unit(this, replay, unitId, type, owner, planet, time);
        if (Args.values.debug) {
            tools.entityDetailsSystem.registerEntity(unit);
        }
        if (owner == Owner.RED) redUnits.add(unit);
        else greenUnits.add(unit);
        planet.unitArrived(unit, time);
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
        // Print out time every 10 seconds
        if (previousTimePrint + 10 <= timer.getTime()) {
            System.out.printf("%d seconds generated.\n", (int) timer.getTime());
            previousTimePrint = timer.getTime();
        }

        int winningTeamIndex = getWinningTeamIndex();
        if (winningTeamIndex != -1) {
            matchOver(winningTeamIndex, timer.getTime());
        }

        // Update green units
        for (Unit unit : greenUnits) {
            unit.update(timer.getTime(), delta);
        }

        // Update red units
        for (Unit unit : redUnits) {
            unit.update(timer.getTime(), delta);
        }

        // Check for unit collision
        for (Unit greenUnit : greenUnits) {
            for (Unit redUnit : redUnits) {
                if (greenUnit.health > 0 && redUnit.health > 0) {
                    float unitSize = GameConfig.values.unitSize;
                    if (Vector2.dst(greenUnit.x, greenUnit.y, redUnit.x, redUnit.y) < unitSize * 0.5f) {
                        greenUnit.battle(redUnit.type, timer.getTime());
                        redUnit.battle(greenUnit.type, timer.getTime());
                    }
                }
            }
        }

        // Remove dead units
        for (Unit unit : greenUnitsToRemove) greenUnits.remove(unit);
        greenUnitsToRemove.clear();
        for (Unit unit : redUnitsToRemove) redUnits.remove(unit);
        redUnitsToRemove.clear();

        // Update planets
        for (Planet planet : planets) {
            planet.update(delta);
        }

        if (shouldSendRequestsToBots(updatesCount)) {
            // Send match state to all bots
//            for (int botIndex = 0; botIndex < tools.botsDetails.length; botIndex++) {
//                tools.server.send(botIndex, MatchStateMessage.create(
//                        timer.getTime(), botOwnerByBotIndex[botIndex], planets, redUnits, greenUnits)
//                );
//            }
//
//            // Wait for all bots to respond
//            tools.server.waitForBotsToRespond();
//
//            // Handle bot responses
//            for (int botIndex = 0; botIndex < tools.botsDetails.length; botIndex++) {
//                BotResponse botResponse = tools.server.getLastResponseData(botIndex, BotResponse.class);
//                handleBotResponse(botIndex, botResponse, timer.getTime());
//            }
        }

        updatesCount++;
    }

    private void matchOver(int winningTeamIndex, float time) {
        // We have found a winner
        System.out.printf("Bot '%s' has won!\n", tools.botsDetails[winningTeamIndex].botName);
        System.out.println("Time elapsed in seconds: " + TimeUtils.timeSinceMillis(startTime) / 1000f);

        // Create a final order of the teams
        int loosingTeamIndex = (winningTeamIndex == 0) ? 1 : 0;
        int[] teamsFinalOrder = new int[]{winningTeamIndex, loosingTeamIndex};

        for (Unit unit : greenUnits) {
            unit.saveLocationAndRotation(time);
        }
        for (Unit unit : redUnits) {
            unit.saveLocationAndRotation(time);
        }

        // Save replay file
        ReplayManager.saveReplayFile(replay, teamsFinalOrder, tools.server, Args.values.replay);

        System.exit(0);
    }

    private int getWinningTeamIndex() {
        for (int botIndex = 0; botIndex < tools.botsDetails.length; botIndex++) {
            Owner owner = botOwnerByBotIndex[botIndex];
            // If both teams lost all units then pick random winner
            if (redUnits.isEmpty() && greenUnits.isEmpty()) {
                return (int) (Math.random() * 2);
            }

            if (owner == Owner.GREEN && redUnits.isEmpty()
                    || owner == Owner.RED && greenUnits.isEmpty()) {
                return tools.botsDetails[botIndex].teamIndex;
            }
        }
        return -1;
    }

    private void handleBotResponse(int botIndex, BotResponse botResponse, float time) {
        Owner owner = botOwnerByBotIndex[botIndex];

        for (BotCommand command : botResponse.commands) {
            try {
                if (command instanceof SpawnUnitCommand) {
                    // Handle move command
                    SpawnUnitCommand spawnUnitCommand = (SpawnUnitCommand) command;
                    Planet planet = findPlanetById(spawnUnitCommand.planetId);
                    if (planet == null) {
                        System.err.printf("Planet with id %d does not exist\n", spawnUnitCommand.planetId);
                        continue;
                    }
                    if (planet.owner == owner && planet.resources >= GameConfig.values.resourcesForNewUnit) {
                        createUnit(spawnUnitCommand.type, owner, planet.planetId, time);
                        planet.resources = 0;
                    } else {
                        System.err.printf("Can't spawn unit on planet (id: %d) that is not owned by the team %s\n",
                                planet.planetId, owner.toString());
                    }

                } else if (command instanceof SendUnitCommand) {
                    SendUnitCommand sendUnitCommand = (SendUnitCommand) command;
                    Unit unit = findUnitById(sendUnitCommand.unitId, owner);
                    if (unit == null) {
                        System.err.printf("Unit with id %d does not exist in team %s\n",
                                sendUnitCommand.unitId, owner.toString());
                        continue;
                    }
                    Planet planet = findPlanetById(sendUnitCommand.destinationPlanetId);
                    if (planet == null) {
                        System.err.printf("Planet with id %d does not exist\n", sendUnitCommand.destinationPlanetId);
                        continue;
                    }
                    unit.goTo(planet, time);
                } else {
                    System.err.printf("Bot '%s' provided an invalid command\n", tools.botsDetails[botIndex].botName);
                }
            }
            catch (Exception e) {
                System.err.printf("Failed to execute command sent from bot for a team %s. Command %s\n",
                        owner.toString(), gson.toJson(command));
            }
        }
    }

    private Unit findUnitById(int unitId, Owner owner) {
        ArrayList<Unit> list = (owner == Owner.RED) ? redUnits : greenUnits;
        for (Unit unit : list) {
            if (unit.unitId == unitId) {
                return unit;
            }
        }
        System.err.printf("Failed to find bot with id %d for team %s\n", unitId, owner.toString());
        return null;
    }

    private Planet findPlanetById(int planetId) {
        for (Planet planet : planets) {
            if (planet.planetId == planetId) {
                return planet;
            }
        }
        System.err.printf("Failed to find planet with id %d\n", planetId);
        return null;
    }

    /** In debug mode it draws the match state to the screen */
    @Override
    public void draw() {
        batch.setProjectionMatrix(tools.gameViewport.getCamera().combined);

        batch.begin();

        // Draw scene
        background.draw(batch);

        for (Unit unit : greenUnits) {
            unit.draw(batch);
        }
        for (Unit unit : redUnits) {
            unit.draw(batch);
        }

        for (Planet planet : planets) {
            planet.draw(batch);
        }

        batch.end();
    }

    private boolean shouldSendRequestsToBots(int updateIndex) {
        return updateIndex % GameConfig.values.gameUpdatesPerBotRequest == 0;
    }

    private Planet findPlanetWithId(int planetId) {
        for (Planet planet : planets) {
            if (planet.planetId == planetId) {
                return planet;
            }
        }
        return null;
    }

    private int generateUnitId(Owner owner) {
        switch (owner) {
            case RED: {
                int id = nextRedUnitId;
                nextRedUnitId++;
                return id;
            }
            case GREEN:{
                int id = nextGreenUnitId;
                nextGreenUnitId++;
                return id;
            }
            default: return -1;
        }
    }

    /** Cleanup necessary stuff before exiting */
    @Override
    public void dispose() {
        Assets.dispose();
    }
}
