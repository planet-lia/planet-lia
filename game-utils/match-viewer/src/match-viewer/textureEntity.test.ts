import {TextureEntity, TextureEntityAttribute, stringify} from "./textureEntity";
import {StepSection} from "./curves/sections/step";
import {TextSection} from "./curves/sections/text";

test('TextureEntity addSection', () => {
    let dummyGetEntity = (_: string) => {
        return undefined
    };

    let entity = new TextureEntity("1", dummyGetEntity);

    let cases = [
        {
            "attribute": TextureEntityAttribute.X, "section": new StepSection(0, 0),
            "curve": entity.x, "expected": 1,
        },
        {
            "attribute": TextureEntityAttribute.Y, "section": new StepSection(0, 0),
            "curve": entity.y, "expected": 1,
        },
        {
            "attribute": TextureEntityAttribute.WIDTH, "section": new StepSection(0, 0),
            "curve": entity.width, "expected": 1,
        },
        {
            "attribute": TextureEntityAttribute.HEIGHT, "section": new StepSection(0, 0),
            "curve": entity.height, "expected": 1,
        },
        {
            "attribute": TextureEntityAttribute.ROTATION_DEG, "section": new StepSection(0, 0),
            "curve": entity.rotation, "expected": 1,
        },
        {
            "attribute": TextureEntityAttribute.LAYER, "section": new StepSection(0, 0),
            "curve": entity.layer, "expected": 1,
        },
        {
            "attribute": TextureEntityAttribute.TEXTURE, "section": new TextSection(0, "warrior.png"),
            "curve": entity.texture, "expected": 1,
        },
        {
            "attribute": TextureEntityAttribute.OPACITY, "section": new StepSection(0, 0),
            "curve": entity.opacity, "expected": 1,
        },
        {
            "attribute": TextureEntityAttribute.VISIBILITY, "section": new StepSection(0, 0),
            "curve": entity.visibility, "expected": 1,
        },
    ];

    for (let c of cases) {
        entity.addSection(stringify(c.attribute), c.section);
        expect(c.curve.numberOfSections()).toBe(c.expected);
    }
});