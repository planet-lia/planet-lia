import {GameDetails} from "./gameDetails";
import {MatchDetail} from "./matchDetails";
import {Entity} from "./entity";
import {Camera, isCamera} from "./camera";
import {Curve} from "./curves/curve";
import {Chart, ChartSeriesElement} from "./chart";
import {errorIfUndefined, Section} from "./curves/sections/section";
import {isTextEntity, TextEntity} from "./textEntity";
import {TextureEntity} from "./textureEntity";
import {StepSection} from "./curves/sections/step";
import {LinearSection} from "./curves/sections/linear";
import {SineSection} from "./curves/sections/sine";
import {TextSection} from "./curves/sections/text";
import {AttachSection} from "./attachSection";
import {BooleanSection} from "./curves/sections/boolean";
import {isParticleEntity, ParticleEntity} from "./particleEntity";
import {BotDetails} from "./botDetails";


const SUPPORTED_SECTION_TYPES = [
    BooleanSection,
    StepSection,
    LinearSection,
    SineSection,
    TextSection,
    AttachSection
];

const STANDALONE_CURVE_ID_PREFIX = "CURVE_";

export class ParsedReplay {
    gameDetails: GameDetails;
    matchDetails: MatchDetail[];
    botDetails: BotDetails[];
    teamsFinalOrder: number[];
    charts: Chart[] = [];
    entities = new Map<string, Entity>();
    cameras = new Map<string, Camera>();
    standaloneCurves = new Map<string, Curve<any>>();

    constructor(gameDetails: GameDetails,
                matchDetails: MatchDetail[],
                botDetails: BotDetails[],
                teamsFinalOrder: number[],
                charts: Chart[],
                entities: Map<string, Entity>,
                cameras: Map<string, Camera>,
                standaloneCurves: Map<string, Curve<any>>) {
        this.gameDetails = gameDetails;
        this.matchDetails = matchDetails;
        this.botDetails = botDetails;
        this.teamsFinalOrder = teamsFinalOrder;
        this.entities = entities;
        this.cameras = cameras;
        this.standaloneCurves = standaloneCurves;
        this.charts = charts;
    }

    static parse(dict: any): ParsedReplay {
        errorIfUndefined([dict.gameDetails, dict.matchDetails, dict.charts, dict.sections]);

        let gameDetails = GameDetails.parse(dict.gameDetails);

        // Parse sections
        let [cameraWidth, cameraHeight] = [gameDetails.camera.width, gameDetails.camera.height];
        let [entities, cameras, standaloneCurves] = parseSections(dict.sections, cameraWidth, cameraHeight);

        // Parse charts
        let charts: Chart[] = [];
        dict.charts.forEach(chart => {
            charts.push(Chart.parse(chart));
        });
        linkChartsWithCurves(charts, standaloneCurves, entities);

        // Parse match details
        let matchDetails: MatchDetail[] = [];
        dict.matchDetails.forEach(detail => {
            matchDetails.push(MatchDetail.parse(detail));
        });

        // Parse bot details
        let botDetails: BotDetails[] = [];
        dict.botDetails.forEach(detail => {
            botDetails.push(BotDetails.parse(detail));
        });

        let teamsFinalOrderLength = (dict.teamsFinalOrder as number[]).length;
        if (teamsFinalOrderLength !== botDetails.length) {
            console.warn(`Length of teamsFinalOrder (${teamsFinalOrderLength}) should be the same `
                + `as the length of the botsDetails (${botDetails.length})`);
        }

        return new ParsedReplay(
            gameDetails,
            matchDetails,
            botDetails,
            dict.teamsFinalOrder,
            charts,
            entities,
            cameras,
            standaloneCurves
        );
    }

    getUniqueTexturePaths(): Set<string> {
        return getUniqueTexturePaths(this.entities);
    }

    getUniqueParticleEffects(): Set<string> {
        return getUniqueParticleEffects(this.entities);
    }

    getEndTime(): number {
        return getEndTime(this.entities, this.cameras, this.standaloneCurves);
    }
}

function parseSections(sections: JSON, cameraWidth: number, cameraHeight: number):
    [Map<string, Entity>, Map<string, Camera>, Map<string, Curve<any>>] {

    let entities = new Map<string, Entity>();
    let cameras = new Map<string, Camera>();
    let standaloneCurves = new Map<string, Curve<any>>();

    (sections as any).forEach(sectionRaw => {

        // Try parsing this section
        try {
            // Find the correct Section
            let section: Section<any>;
            for (let sectionType of SUPPORTED_SECTION_TYPES) {
                if (sectionRaw.type === sectionType.NAME) {
                    section = sectionType.parse(sectionRaw);
                    break;
                }
            }

            let entityId = sectionRaw.entityId;

            if (isStandaloneCurve(entityId)) {
                let curveId = entityId;
                if (!standaloneCurves.has(curveId)) {
                    standaloneCurves.set(curveId, new Curve<any>());
                }
                let curve = standaloneCurves.get(curveId)!;
                curve.add(section!);
            } else if (isCamera(entityId)) {
                // Camera

                // If the cameraIndex with provided id does not exist, create it
                if (!cameras.has(entityId)) {
                    let camera = new Camera(entityId, cameraWidth, cameraHeight);
                    cameras.set(entityId, camera);
                }

                // Add curve sections to entity
                let camera = cameras.get(entityId)!;
                camera.addSection(sectionRaw.attribute, section!);
            } else {
                // Entity

                // If the entity with provided id does not exist, create it
                if (!entities.has(entityId)) {
                    let getEntity = (id: string) => {
                        return entities.get(id)!.attachable
                    };


                    let entity: Entity;
                    if (isTextEntity(entityId)) entity = new TextEntity(entityId, getEntity);
                    else if (isParticleEntity(entityId)) entity = new ParticleEntity(entityId, getEntity);
                    else entity = new TextureEntity(entityId, getEntity);

                    entities.set(entityId, entity);
                }

                // Add curve sections to entity
                let entity = entities.get(entityId)!;
                entity.addSection(sectionRaw.attribute, section!);
            }
        }
        catch (e) {
            e.message += `\nRaw section: ${JSON.stringify(sectionRaw)}`;
            throw e;
        }
    });

    return [entities, cameras, standaloneCurves];
}

function isStandaloneCurve(entityId: string): boolean {
    return entityId.startsWith(STANDALONE_CURVE_ID_PREFIX)
}

function linkChartsWithCurves(statistics: Chart[],
                              standaloneCurves: Map<string, Curve<any>>,
                              entities: Map<string, Entity>) {
    statistics.forEach((statistic: Chart) => {
        statistic.series.forEach(((s: ChartSeriesElement) => {
            let ref = s.curveRef;
            if (isStandaloneCurve(ref.entityId)) {
                s.curve = standaloneCurves.get(ref.entityId)!;
            } else {
                let entity = entities.get(ref.entityId)!;
                s.curve = entity.getCurve(ref.attribute);
            }
        }));
    });
}

export function getUniqueTexturePaths(entities: Map<string, Entity>): Set<string> {
    let paths = new Set<string>();

    entities.forEach((entity: Entity) => {
        if (entity instanceof TextureEntity) {
            entity.texture.sections.forEach((section: Section<string>) => {
                if (!paths.has(section.endRangeValue)) {
                    paths.add(section.endRangeValue);
                }
            })
        }
    });

    return paths;
}

export function getUniqueParticleEffects(entities: Map<string, Entity>): Set<string> {
    let paths = new Set<string>();

    entities.forEach((entity: Entity) => {
        if (entity instanceof ParticleEntity) {
            entity.effect.sections.forEach((section: Section<string>) => {
                if (!paths.has(section.endRangeValue)) {
                    paths.add(section.endRangeValue);
                }
            })
        }
    });

    return paths;
}


function getEndTime(entities: Map<string, Entity>,
                    cameras: Map<string, Camera>,
                    standaloneCurves: Map<string, Curve<any>>): number {
    let endTimeMax = 0;
    entities.forEach((entity: Entity) => {
        let endTime = entity.getEndTime();
        if (endTime > endTimeMax) endTimeMax = endTime;
    });
    cameras.forEach((camera: Camera) => {
        let endTime = camera.getEndTime();
        if (endTime > endTimeMax) endTimeMax = endTime;
    });
    standaloneCurves.forEach((curve: Curve<any>) => {
        let endTime = curve.getEndTime();
        if (endTime > endTimeMax) endTimeMax = endTime;
    });
    return endTimeMax;
}