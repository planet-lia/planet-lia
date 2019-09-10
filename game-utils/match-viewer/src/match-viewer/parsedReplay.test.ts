import {LinearSection} from "./curves/sections/linear";
import {TextureEntity} from "./textureEntity";
import {TextSection} from "./curves/sections/text";
import {getUniqueTexturePaths, ParsedReplay} from "./parsedReplay";
import {colorToNumber} from "./util/color";

test('ParsedReplay.parse', () => {
    let data = {
        "gameDetails": {
            "game": "super_game",
            "version": "1.0",
            "backgroundColor": "#FF0000",
            "camera": {
                "width": 1,
                "height": 2,
            },
            "showEntityPath": {
                "pathColor": "#FF00FF",
                "pathAlpha": 0.5,
                "clickedEntityTint": "#FF00FF",
                "pathWidth": 2,
                "drawingTimeInterval": 0.1,
            }
        },
        "matchDetails": [
            {"description": "Map seed", "value": 12},
            {"description": "Game seed", "value": 3}
        ],
        "charts": [
            {
                "name": "Power",
                "series": [
                    {
                        "name": "Player 1",
                        "color": "#FFFF00",
                        "curveRef": {
                            "entityId": "CURVE_1",
                            "attribute": "NONE"
                        }
                    },
                    {
                        "name": "Player 2",
                        "color": "#FF00FF",
                        "curveRef": {
                            "entityId": "CURVE_2",
                            "attribute": "NONE"
                        }
                    }
                ]
            }
        ],
        "sections": [
            {"type": LinearSection.name, "entityId": "1", "attribute": "X", "endTime": 0, "endRangeValue": 10},
            {"type": LinearSection.name, "entityId": "1", "attribute": "X", "endTime": 1, "endRangeValue": 20},
            {"type": TextSection.name, "entityId": "1", "attribute": "TEXTURE", "endTime": 10, "text": "warrior.png"},
            {"type": LinearSection.name, "entityId": "2", "attribute": "X", "endTime": 1, "endRangeValue": 2},
            {"type": LinearSection.name, "entityId": "CAMERA_1", "attribute": "X", "endTime": 1, "endRangeValue": 2},
            {"type": LinearSection.name, "entityId": "CURVE_1", "attribute": "NONE", "endTime": 0, "endRangeValue": 5},
            {"type": LinearSection.name, "entityId": "CURVE_1", "attribute": "NONE", "endTime": 2, "endRangeValue": 10},
            {"type": LinearSection.name, "entityId": "CURVE_2", "attribute": "NONE", "endTime": 0, "endRangeValue": 11},
            {"type": LinearSection.name, "entityId": "CURVE_2", "attribute": "NONE", "endTime": 11, "endRangeValue": 13}
        ]
    };

    let jsonData = JSON.parse(JSON.stringify(data));
    let replay = ParsedReplay.parse(jsonData);

    let gameDetails = replay.gameDetails;
    expect(gameDetails.game).toBe("super_game");
    expect(gameDetails.version).toBe("1.0");
    expect(gameDetails.backgroundColor).toBe("#FF0000");
    expect(gameDetails.camera.width).toBe(1);
    expect(gameDetails.camera.height).toBe(2);
    expect(gameDetails.showEntityPath!.pathColor).toBe(colorToNumber("#FF00FF"));
    expect(gameDetails.showEntityPath!.pathAlpha).toBe(0.5);
    expect(gameDetails.showEntityPath!.clickedEntityTint).toBe(colorToNumber("#FF00FF"));
    expect(gameDetails.showEntityPath!.pathWidth).toBe(2);
    expect(gameDetails.showEntityPath!.drawingTimeInterval).toBe(0.1);

    let matchDetails = replay.matchDetails;
    expect(matchDetails[0].description).toBe("Map seed");
    expect(matchDetails[0].value).toBe(12);
    expect(matchDetails[1].description).toBe("Game seed");
    expect(matchDetails[1].value).toBe(3);

    let charts = replay.charts;
    expect(charts[0].name).toBe("Power");
    expect(charts[0].series[0].name).toBe("Player 1");
    expect(charts[0].series[0].color).toBe("#FFFF00");
    expect(charts[0].series[0].curve!.getEndTime()).toBe(2);
    expect(charts[0].series[1].curve!.getEndTime()).toBe(11);

    expect(replay.entities.size).toBe(2);

    expect(replay.cameras.size).toBe(1);

    let entity1 = replay.entities.get("1")! as TextureEntity;
    expect(entity1.id).toBe("1");
    expect(entity1.x.numberOfSections()).toBe(2);
    expect(entity1.texture.numberOfSections()).toBe(1);
    expect(entity1.x.getValue(0.5)).toBe(15);

    let entity2 = replay.entities.get("2")! as TextureEntity;
    expect(entity2.id).toBe("2");
    expect(entity2.x.numberOfSections()).toBe(1);
});

test('getUniqueTexturePaths', () => {
    let entities = new Map<string, TextureEntity>();

    expect(getUniqueTexturePaths(entities).size).toBe(0);

    let getEntity = (id: string) => {
        return entities.get(id)!.attachable
    };

    let entity1 = new TextureEntity("1", getEntity);
    entity1.texture.add(new TextSection(0, "a"));
    entity1.texture.add(new TextSection(2, "b"));
    entity1.texture.add(new TextSection(3, "a"));
    entities.set("1", entity1);

    let entity2 = new TextureEntity("2", getEntity);
    entity2.texture.add(new TextSection(0, "c"));
    entity2.texture.add(new TextSection(2, "b"));
    entity2.texture.add(new TextSection(3, "d"));
    entities.set("2", entity2);

    expect(getUniqueTexturePaths(entities).size).toBe(4);
    expect(getUniqueTexturePaths(entities).has("a")).toBe(true);
    expect(getUniqueTexturePaths(entities).has("b")).toBe(true);
    expect(getUniqueTexturePaths(entities).has("c")).toBe(true);
    expect(getUniqueTexturePaths(entities).has("d")).toBe(true);
});