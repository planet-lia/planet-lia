// @ts-ignore
const empty = () => {};
// import {Sprite} from 'pixi.js';
// import {LinearSection} from "../curves/sections/linear";
// import {TextureEntityAttribute, stringify} from "../textureEntity";
// import {StepSection} from "../curves/sections/step";
// import {TextSection} from "../curves/sections/text";
// import {GameEntity} from "./gameEntity";
// import {SineSection} from "../curves/sections/sine";
// import {getCircularMovementParametricEquations} from "../math/circle";
// import {SineEquation} from "../math/sineEquation";
//
//
// export class ComplexMovementEntity implements GameEntity {
//     ENTITY_ID = "1";
//
//     assetPath: string;
//     sprite: Sprite;
//
//     velocity = 0;
//     rotationVelocity = 0;
//     replay: object[];
//
//     // Store while the movement is being generated.
//     // Initialized as a zero function
//     sineX: SineEquation;
//     sineY: SineEquation;
//
//     // Define when the movement pattern will change
//     movementChanges = [
//         {"time": 1, "rotationVelocity": 0, "velocity": 70},
//         {"time": 3, "rotationVelocity": 90, "velocity": 0},
//         {"time": 4.5, "rotationVelocity": -40, "velocity": 80},
//         {"time": 7, "rotationVelocity": 127, "velocity": 90},
//         {"time": 9, "rotationVelocity": 0, "velocity": -90},
//     ];
//
//     constructor(replayElements: object[], assetsPath: string, x: number, y: number) {
//         this.replay = replayElements;
//         this.assetPath = assetsPath + "entity.png";
//         this.sprite = Sprite.from(this.assetPath);
//         this.sprite.width = 30;
//         this.sprite.height = 30;
//         this.sprite.anchor.set(0.5);
//         this.sprite.x = x;
//         this.sprite.y = y;
//         this.sprite.angle = 120;
//
//         // Default last
//         this.sineX = new SineEquation(0, x, 0, 0, 0);
//         this.sineY = new SineEquation(0, y, 0, 0, 0);
//     }
//
//     generationStart(time: number) {
//         // Store starting values of the entity to replay
//         this.storeMovementPattern(time);
//         this.storeRotationPattern(time);
//         this.replay.push(StepSection.toDict(this.ENTITY_ID, stringify(TextureEntityAttribute.WIDTH), time, this.sprite.width));
//         this.replay.push(TextSection.toDict(this.ENTITY_ID, stringify(TextureEntityAttribute.TEXTURE), time, this.assetPath));
//     }
//
//     // Move the unit and store movement changes in replay
//     move(time: number, delta: number) {
//
//         // Each used movement change gets removed from the movementChanges list
//         if (this.movementChanges.length != 0) {
//
//             if (this.movementChanges[0].time <= time) {
//                 // Remove first movement change and get it
//                 let change = this.movementChanges.shift()!;
//
//                 // CHANGE MOVEMENT PATTERN
//                 if (this.velocity != change.velocity || this.rotationVelocity != change.rotationVelocity) {
//
//                     // Store old movement pattern
//                     this.storeMovementPattern(time);
//
//                     // Setup new movement pattern
//                     this.setupNewMovementPattern(time, change.velocity, change.rotationVelocity);
//                 }
//
//                 // Store old rotation pattern
//                 if (this.rotationVelocity != change.rotationVelocity) {
//                     this.storeRotationPattern(time);
//                 }
//
//                 // Update values
//                 this.velocity = change.velocity;
//                 this.rotationVelocity = change.rotationVelocity;
//             }
//         }
//
//         // Move the entity
//         this.sprite.angle += this.rotationVelocity * delta;
//
//         let velocityX = this.velocity * Math.cos(this.sprite.rotation);
//         let velocityY = this.velocity * Math.sin(this.sprite.rotation);
//
//         this.sprite.x += velocityX * delta;
//         this.sprite.y += velocityY * delta;
//     }
//
//     storeMovementPattern(time: number) {
//         if (isCircularMovementPattern(this.velocity, this.rotationVelocity)) {
//             // Circular
//             this.replay.push(SineSection.toDict(this.ENTITY_ID, stringify(TextureEntityAttribute.X), time, this.sineX));
//             this.replay.push(SineSection.toDict(this.ENTITY_ID, stringify(TextureEntityAttribute.Y), time, this.sineY));
//         } else {
//             // Linear
//             this.replay.push(LinearSection.toDict(this.ENTITY_ID, stringify(TextureEntityAttribute.X), time, this.sprite.x));
//             this.replay.push(LinearSection.toDict(this.ENTITY_ID, stringify(TextureEntityAttribute.Y), time, this.sprite.y));
//         }
//     }
//
//     setupNewMovementPattern(time: number, velocity: number, rotationVelocity: number) {
//         if (isCircularMovementPattern(velocity, rotationVelocity)) {
//             // Circular
//             [this.sineX, this.sineY] = getCircularMovementParametricEquations(time, velocity, rotationVelocity,
//                 this.sprite.angle, this.sprite.x, this.sprite.y);
//         } else {
//             // Linear, nothing to do
//         }
//     }
//
//     storeRotationPattern(time: number) {
//         this.replay.push(LinearSection.toDict(this.ENTITY_ID, stringify(TextureEntityAttribute.ROTATION_DEG), time,
//             this.sprite.angle));
//     }
//
//     // Store last entity curveRef to replay
//     generationEnd(time: number) {
//         this.storeMovementPattern(time);
//         this.storeRotationPattern(time);
//     }
//
//     getX(): number {
//         return this.sprite.x;
//     }
//
//     getY(): number {
//         return this.sprite.y;
//     }
//
//     getSprite(): Sprite {
//         return this.sprite;
//     }
// }
//
// function isCircularMovementPattern(velocity: number, rotationVelocity: number): boolean {
//     return velocity != 0 && rotationVelocity != 0;
// }