// @ts-ignore
const empty = () => {};

// import {Sprite} from 'pixi.js';
// import {LinearSection} from "../curves/sections/linear";
// import {TextureEntityAttribute, stringify} from "../textureEntity";
// import {StepSection} from "../curves/sections/step";
// import {TextSection} from "../curves/sections/text";
// import {GameEntity} from "./gameEntity";
// import {SineEquation} from "../math/sineEquation";
// import {getCircularMovementParametricEquations} from "../math/circle";
// import {SineSection} from "../curves/sections/sine";
//
//
// export class CircularMovementEntity implements GameEntity {
//     ENTITY_ID = "1";
//
//     assetPath: string;
//     sprite: Sprite;
//
//     velocity = 0;
//     rotationVelocity = 0;
//     replay: object[];
//
//     // Store while the movement is being generated
//     sineX: SineEquation | null = null;
//     sineY: SineEquation | null = null;
//
//
//     // Define when the movement pattern will change
//     movementChanges = [
//         {"time": 0, "rotationVelocity": 30, "velocity": 50},
//         {"time": 2, "rotationVelocity": -60, "velocity": 50},
//         {"time": 5, "rotationVelocity": -27, "velocity": 67},
//         {"time": 7, "rotationVelocity": 67, "velocity": 40},
//         {"time": 9, "rotationVelocity": -15, "velocity": 140},
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
//     }
//
//     // Store the beginning values of the entity to replay
//     generationStart(time: number) {
//         this.replay.push(StepSection.toDict(this.ENTITY_ID, stringify(TextureEntityAttribute.X), time, this.sprite.x));
//         this.replay.push(StepSection.toDict(this.ENTITY_ID, stringify(TextureEntityAttribute.Y), time, this.sprite.y));
//         this.replay.push(StepSection.toDict(this.ENTITY_ID, stringify(TextureEntityAttribute.WIDTH), time, this.sprite.width));
//         this.replay.push(LinearSection.toDict(this.ENTITY_ID, stringify(TextureEntityAttribute.ROTATION_DEG), time, this.sprite.angle));
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
//
//                 // CHANGE MOVEMENT PATTERN
//                 if (this.velocity != change.velocity || this.rotationVelocity != change.rotationVelocity) {
//
//                     // Store old x and y movement pattern to replay
//                     if (this.sineX != null && this.sineY != null) {
//                         this.replay.push(SineSection.toDict(this.ENTITY_ID, stringify(TextureEntityAttribute.X), time, this.sineX!));
//                         this.replay.push(SineSection.toDict(this.ENTITY_ID, stringify(TextureEntityAttribute.Y), time, this.sineY!));
//                     }
//
//                     // Prepare new velocity
//                     [this.sineX, this.sineY] = getCircularMovementParametricEquations(time, change.velocity,
//                         change.rotationVelocity, this.sprite.angle, this.sprite.x, this.sprite.y);
//                 }
//
//                 // CHANGE ROTATION PATTERN
//                 if (this.rotationVelocity != change.rotationVelocity) {
//                     // Store old rotation pattern to replay
//                     this.replay.push(LinearSection.toDict(this.ENTITY_ID, stringify(TextureEntityAttribute.ROTATION_DEG), time,
//                         this.sprite.angle));
//                 }
//
//                 // Update values
//                 this.velocity = change.velocity;
//                 this.rotationVelocity = change.rotationVelocity;
//             }
//         }
//
//         // Update the entity
//         this.sprite.angle += this.rotationVelocity * delta;
//
//         let velocityX = this.velocity * Math.cos(this.sprite.rotation);
//         let velocityY = this.velocity * Math.sin(this.sprite.rotation);
//
//         this.sprite.x += velocityX * delta;
//         this.sprite.y += velocityY * delta;
//     }
//
//     // Store last entity curveRef to replay
//     generationEnd(time: number) {
//         this.replay.push(LinearSection.toDict(this.ENTITY_ID, stringify(TextureEntityAttribute.ROTATION_DEG), time, this.sprite.angle));
//         this.replay.push(SineSection.toDict(this.ENTITY_ID, stringify(TextureEntityAttribute.X), time, this.sineX!));
//         this.replay.push(SineSection.toDict(this.ENTITY_ID, stringify(TextureEntityAttribute.Y), time, this.sineY!));
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
