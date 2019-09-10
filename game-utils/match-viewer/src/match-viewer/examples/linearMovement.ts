// @ts-ignore
const empty = () => {};
// import {Sprite} from 'pixi.js';
// import {LinearSection} from "../curves/sections/linear";
// import {TextureEntityAttribute, stringify} from "../textureEntity";
// import {StepSection} from "../curves/sections/step";
// import {TextSection} from "../curves/sections/text";
// import {GameEntity} from "./gameEntity";
//
// export class LinearMovementEntity implements GameEntity {
//     ENTITY_ID = "1";
//
//     assetPath: string;
//     sprite = new Sprite();
//
//     velocity = 0;
//     rotationVelocity = 0;
//     replay: object[];
//
//     // Define when the movement pattern will change
//     movementChanges = [
//         {"time": 1, "rotationVelocity": 30, "velocity": 0},
//         {"time": 2, "rotationVelocity": 0, "velocity": 30},
//         {"time": 5, "rotationVelocity": -30, "velocity": 0},
//         {"time": 7, "rotationVelocity": 0, "velocity": 50},
//     ];
//
//     constructor(replayElements: object[], assetsPath: string, x: number, y: number) {
//         this.replay = replayElements;
//         this.assetPath = assetsPath + "entity.png";
//         this.sprite.width = 30;
//         this.sprite.height = 30;
//         this.sprite.anchor.set(0.5);
//         this.sprite.x = x;
//         this.sprite.y = y;
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
//                 // Change rotation
//                 if (this.rotationVelocity != change.rotationVelocity) {
//                     this.rotationVelocity = change.rotationVelocity;
//                     // Since the movement pattern has changed, add a section
//                     this.replay.push(LinearSection.toDict(this.ENTITY_ID, stringify(TextureEntityAttribute.ROTATION_DEG), time,
//                         this.sprite.angle));
//                 }
//
//                 // Change velocity
//                 if (this.velocity != change.velocity) {
//                     this.velocity = change.velocity;
//                     // Since the movement pattern has changed, add a section
//                     this.replay.push(LinearSection.toDict(this.ENTITY_ID, stringify(TextureEntityAttribute.X), time, this.sprite.x));
//                     this.replay.push(LinearSection.toDict(this.ENTITY_ID, stringify(TextureEntityAttribute.Y), time, this.sprite.y));
//                 }
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
//         this.replay.push(LinearSection.toDict(this.ENTITY_ID, stringify(TextureEntityAttribute.X), time, this.sprite.x));
//         this.replay.push(LinearSection.toDict(this.ENTITY_ID, stringify(TextureEntityAttribute.Y), time, this.sprite.y));
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
