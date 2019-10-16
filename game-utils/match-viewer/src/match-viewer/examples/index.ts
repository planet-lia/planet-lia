// @ts-ignore
const empty = () => {};

// import {Application, Graphics, Texture} from "pixi.js";
// import {TextureEntity} from "../curves/entity";
// import {plotNumberCurve, PlotOptions, plotStringCurve} from "../curves/plotCurve";
// import {GameDetails} from "../curves/metaData";
// import {LinearMovementEntity} from "./linearMovement";
// import {CircularMovementEntity} from "./circularMovement";
// import {PeriodicStorageMovementEntity} from "./periodicStorageMovement";
// import {ComplexMovementEntity} from "./complexMovement";
// import {GameEntity} from "./gameEntity";
// import {getUniqueTexturePaths, parseReplay} from "../curves/parse";
//
//
// function createApplication(width: number, height: number, backgroundColor: number): Application {
//     let app = new Application({
//         backgroundColor: backgroundColor,
//         width: width,
//         height: height,
//         autoStart: true
//     });
//     document.body.appendChild(app.view);
//
//     return app;
// }
//
// class Location {
//     time: number;
//     x: number;
//     y: number;
//
//     constructor(time: number, x: number, y: number) {
//         this.time = time;
//         this.x = x;
//         this.y = y;
//     }
// }
//
// function plotEntityGraphs(entity: TextureEntity, duration: number) {
//     let options = new PlotOptions();
//     options.startTime = 0;
//     options.endTime = duration;
//
//     plotNumberCurve(entity.x,'x', 'charts', options);
//     plotNumberCurve(entity.y,'y', 'charts', options);
//     plotNumberCurve(entity.width,'width', 'charts', options);
//     plotNumberCurve(entity.height,'height', 'charts', options);
//     plotNumberCurve(entity.rotation,'rotation', 'charts', options);
//     plotNumberCurve(entity.layer,'layer', 'charts', options);
//     plotStringCurve(entity.texture,'texture', 'charts', options);
//     plotNumberCurve(entity.opacity,'opacity', 'charts', options);
//     plotNumberCurve(entity.visibility,'visibility', 'charts', options);
// }
//
// // Draw (x,y) positions of the entity
// function drawReproducedPositions(time: number, graphics: Graphics, enitity: TextureEntity, color: number, radius: number) {
//     graphics.clear();
//     graphics.beginFill(color);
//     for (let i = 0; i < time; i += 0.05) {
//         graphics.drawCircle(enitity.x.getValue(i), enitity.y.getValue(i), radius);
//     }
//     graphics.endFill();
// }
//
// // Draw actual visited locations of the entity
// function drawOriginalPositions(time: number, graphics: Graphics, locations: Location[], color: number, radius: number) {
//     graphics.beginFill(color);
//     locations.forEach(location => {
//         if (location.time <= time) {
//             graphics.drawCircle(location.x, location.y, radius);
//         }
//     });
//     graphics.endFill();
// }
//
//
// function play() {
//     const app = createApplication(960, 540, 0x333333);
//
//     let time = 0;
//     let timeScale = 1;
//     let duration = 10;
//     let generating = true;
//
//     // Replay file to store our entity
//     let replayElements: object[] = [];
//
//     // Add basic curveRef to replay
//     let gameDetails = new GameDetails("examples", "1.0", "#333333");
//     replayElements.push(gameDetails);
//
//     let path = `${gameDetails.gameName}/${gameDetails.assetsVersion}/`;
//
//     // TextureEntity and textures as loaded from the replay file
//     let loadedEntity: TextureEntity;
//     let loadedTextures = new Map<string, Texture>();
//
//     // Locations that the entity will visit in the generation period.
//     // Used as a reference if replayed path equals the actual path.
//     let locations: Location[] = [];
//
//     // Draws shapes to the screen
//     let graphics = new Graphics();
//     app.stage.addChild(graphics);
//
//     let xMiddle = app.screen.width / 2;
//     let yMiddle = app.screen.height / 2;
//     let entityImplementations = {
//         "linear": new LinearMovementEntity(replayElements, path, xMiddle, yMiddle),
//         "circular": new CircularMovementEntity(replayElements, path, xMiddle, yMiddle),
//         "periodic": new PeriodicStorageMovementEntity(replayElements, path, xMiddle, yMiddle),
//         "complex": new ComplexMovementEntity(replayElements, path, xMiddle, yMiddle),
//     };
//
//     // Choose your implementation of the entity and set it up
//     let gameEntity: GameEntity = entityImplementations.complex;
//     gameEntity.generationStart(time);
//     app.stage.addChild(gameEntity.getSprite());
//
//     // Speed control bar
//     let speedBar = document.getElementById('speedBar') as HTMLInputElement;
//
//     // Time control bar
//     let timeBar = document.getElementById('timeBar') as HTMLInputElement;
//     timeBar.max = duration + "";
//     timeBar.value = time + "";
//
//     // Main loop
//     app.ticker.add((deltaScale) => {
//         let delta = (1 / 60) * deltaScale * timeScale;
//         time += delta;
//
//         // SIMULATE the movement of the entity
//         if (time <= duration && generating) {
//             // Update gameName entity
//             gameEntity.move(time, delta);
//
//             // Store a location point for reference
//             locations.push(new Location(time, gameEntity.getX(), gameEntity.getY()));
//         }
//         // PREPARE the replay
//         else if (generating) {
//             gameEntity.generationEnd(time);
//
//             generating = false;
//             time = 0;
//
//             app.stage.removeChild(gameEntity.getSprite());
//
//             // Parse replay
//             let replayJson = JSON.stringify(replayElements);
//             console.log(replayJson);
//             let [entities, gameDetails] = parseReplay(replayJson);
//             let attachToEntityId = "1";
//             loadedEntity = entities.get(attachToEntityId);
//
//             // Load required textures
//             let texturesToLoad = getUniqueTexturePaths(entities);
//             let path = `${gameDetails.gameName}/${gameDetails.assetsVersion}/`;
//             texturesToLoad.forEach(texture => loadedTextures.set(texture, Texture.from(path + texture)));
//
//             // Use only the entity with id 1
//             let entity = entities.get("1");
//
//             entity.finishSetup();
//             app.stage.addChildAt(entity.sprite, 0);
//
//             plotEntityGraphs(entity, duration);
//
//             // Setup timeBar
//             timeBar.hidden = false;
//             timeBar.addEventListener("input", function(event: Event) {
//                 time = Number((<HTMLInputElement>event.target).value);
//             });
//
//             // Reset playbackSpeed bar
//             speedBar.hidden = false;
//             speedBar.addEventListener("input", function(event: Event) {
//                 timeScale = Number((<HTMLInputElement>event.target).value);
//             });
//         }
//         // REPLAY the movement of the entity
//         else {
//             if (time > duration) time = duration;
//             if (time < 0) time = 0;
//
//             timeBar.value = `${time}`;
//
//             loadedEntity.update(time, loadedTextures, app.renderer.height, 1);
//
//             drawReproducedPositions(time, graphics, loadedEntity, 0x019170, 5);
//             drawOriginalPositions(time, graphics, locations, 0xff0000, 2);
//         }
//     });
// }
//
//
// play();
