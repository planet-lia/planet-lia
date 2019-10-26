import {Application, Container, Graphics} from "pixi.js";
import {registerOnEntityClickShowPath, TextureEntity} from "./textureEntity";
import {colorToNumber} from './util/color';
import {Camera, CAMERA_HUD_ID, CAMERA_DEFAULT_ID, createCenteredCamera} from "./camera";
import {Entity, isOnHUD} from "./entity";
import {TextEntity} from "./textEntity";
import {Chart} from "./chart";
import {MatchDetail} from "./matchDetails";
import {ParsedReplay} from "./parsedReplay";
import {Assets, loadAssets} from "./assets";
import {ParticleEntity} from "./particleEntity";
import {BotDetails} from "./botDetails";

export class MatchViewerApplication extends Application {
    matchDetails: MatchDetail[] | null = null;
    botDetails: BotDetails[] | null = null;
    teamsFinalOrder: number[] | null = null;
    matchDuration: number = 0;
    time: number = 0;
    playbackSpeed: number = 1;
    gameCameras: Camera[] = [];
    currentCamera: Camera | undefined;
    charts: Chart[] = [];
}

// Camera array ordered alphabetically.
function toOrderedArrayOfCameras(camerasMap: Map<string, Camera>): Camera[] {
    // Create array of cameras and sort it
    let cameras: Camera[] = [];
    camerasMap.forEach((camera: Camera) => cameras.push(camera));
    cameras.sort((a: Camera, b: Camera) => a.id.toUpperCase().localeCompare(b.id.toUpperCase()));
    return cameras;
}

export function startGame(replayRaw: JSON, assetsBaseUrl: string): MatchViewerApplication {
    const app = new MatchViewerApplication({
        backgroundColor: 0x000,
        width: 1920,
        height: 1080
    });
    app.renderer.view.style.width = "100%";

    // Game viewport holds all entities in
    // the gameName and displays them
    const gameViewport = new Container();
    gameViewport.sortableChildren = true;
    app.stage.addChild(gameViewport);

    // Hud viewport holds all gameName UI
    // entities and displays them.
    const hudViewport = new Container();
    hudViewport.sortableChildren = true;
    app.stage.addChild(hudViewport);

    // Load replay
    let replay = ParsedReplay.parse(replayRaw);
    let [entities, gameDetails] = [replay.entities, replay.gameDetails!];
    app.matchDetails = replay.matchDetails;
    app.botDetails = replay.botDetails;
    app.teamsFinalOrder = replay.teamsFinalOrder;
    app.charts = replay.charts;
    app.renderer.backgroundColor = colorToNumber(gameDetails.backgroundColor);

    // Setup alphabetically ordered array of gameCameras
    app.gameCameras = toOrderedArrayOfCameras(replay.cameras);
    app.gameCameras.forEach((camera: Camera) => camera.finishSetup());

    // If no cameras were provided, create default camera and add it to cameras
    if (app.gameCameras.length === 0) {
        let defaultCamera = createCenteredCamera(CAMERA_DEFAULT_ID, gameDetails.camera.width, gameDetails.camera.height);
        app.gameCameras.push(defaultCamera);
    }

    app.currentCamera = app.gameCameras[0];

    // Create HUD camera
    let hudCamera = createCenteredCamera(CAMERA_HUD_ID, gameDetails.camera.width, gameDetails.camera.height);
    let screenHeight = app.renderer.height;
    let hudToScreen = screenHeight / gameDetails.camera.height;
    hudCamera!.update(app.time, hudViewport, screenHeight, hudToScreen);

    let worldToScreen = screenHeight / gameDetails.camera.height;

    // Get gameName duration
    app.matchDuration = replay.getEndTime();

    let assetPath = `${assetsBaseUrl}/${gameDetails.gameName}/assets/${gameDetails.assetsVersion}`;
    let assets = new Assets(assetPath);

    let onAssetsLoaded = () => {
        try {
            // Setup entities and add them to gameName or HUD viewport
            entities.forEach((entity: Entity) => {
                entity.finishSetup(assets);

                let viewport = (isOnHUD(entity.id)) ? hudViewport : gameViewport;
                if (entity instanceof TextureEntity) {
                    viewport.addChild((entity.sprite));
                } else if (entity instanceof TextEntity) {
                    viewport.addChild((entity.textDisplay));
                } else if (entity instanceof ParticleEntity) {
                    viewport.addChild(entity.container);
                }
            });

            // When TextureEntity is clicked, draw its path on the screen
            const graphics = new Graphics();
            gameViewport.addChild(graphics);
            if (gameDetails.showEntityPath != null) {
                let settings = gameDetails.showEntityPath!;
                registerOnEntityClickShowPath(settings, entities, graphics, screenHeight, worldToScreen);
            }

            let prevTime = 0;

            // Add main gameName function to be called every tick
            app.ticker.add((deltaScale) => {
                let delta = 0;

                // If user manually changed time
                if (app.time !== prevTime) {
                    delta = app.time - prevTime;
                }
                // Normal gameName iteration
                else {
                    delta = (1 / 60) * deltaScale * app.playbackSpeed;
                    app.time += delta;
                }
                // Check time bounds
                if (app.time > app.matchDuration) app.time = app.matchDuration;
                if (app.time < 0) app.time = 0;

                prevTime = app.time;

                // Update current gameName camera
                app.currentCamera!.update(app.time, gameViewport, screenHeight, worldToScreen);

                // Update entities
                entities.forEach((entity: Entity) => {
                    entity.update(app.time, delta, assets, screenHeight, worldToScreen)
                });
            });

            // Start the ticker
            app.start();
        }
        catch (e) {
            console.error(`Failed to view replay: ${e}`)
        }
    };

    // Load assets and call onAssetsLoaded when finished
    loadAssets(replay, assets, onAssetsLoaded);

    return app;
}