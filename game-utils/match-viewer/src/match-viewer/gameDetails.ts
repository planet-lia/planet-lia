import {errorIfUndefined} from "./curves/sections/section";
import {colorToNumber} from "./util/color";


export class GameDetails {

    game: string;
    version: string;
    backgroundColor: string;

    camera: CameraSettings;
    showEntityPath: ShowEntityPathSettings | null;

    constructor(game: string, version: string, backgroundColor: string,
                camera: CameraSettings, showEntityPath: ShowEntityPathSettings | null) {
        this.game = game;
        this.version = version;
        this.backgroundColor = backgroundColor;
        this.camera = camera;
        this.showEntityPath = showEntityPath;
    }

    static parse(dict: any): GameDetails {
        errorIfUndefined([dict.game, dict.version, dict.backgroundColor, dict.camera, dict.showEntityPath]);

        let showEntityPath = (dict.showEntityPath == null)
            ? null
            : ShowEntityPathSettings.parse(dict.showEntityPath);

        return new GameDetails(
            dict.game, dict.version, dict.backgroundColor,
            CameraSettings.parse(dict.camera),
            showEntityPath
        );
    }

    static toDict(game: string, version: string, backgroundColor: string,
                  cameraWidth: number, cameraHeight: number): object {
        return {
            "type": GameDetails.name,
            "game": game,
            "version": version,
            "backgroundColor": backgroundColor,
            "cameraWidth": cameraWidth,
            "cameraHeight": cameraHeight
        };
    }
}

export class CameraSettings {

    width: number;
    height: number;

    constructor(width: number, height: number) {
        this.width = width;
        this.height = height;
    }

    static parse(dict: any): CameraSettings {
        errorIfUndefined([dict.width, dict.height]);
        return new CameraSettings(dict.width, dict.height);
    }
}

export class ShowEntityPathSettings {

    pathColor: number;
    pathAlpha: number;
    clickedEntityTint: number;
    pathWidth: number;
    drawingTimeInterval: number;

    constructor(pathColor: string, pathAlpha: number, clickedEntityTint: string, pathWidth: number, drawingTimeInterval: number) {
        this.pathColor = colorToNumber(pathColor);
        this.pathAlpha = pathAlpha;
        this.clickedEntityTint = colorToNumber(clickedEntityTint);
        this.pathWidth = pathWidth;
        this.drawingTimeInterval = drawingTimeInterval;
    }

    static parse(dict: any): ShowEntityPathSettings {
        errorIfUndefined([dict.pathColor, dict.pathAlpha, dict.clickedEntityTint,
            dict.pathWidth, dict.drawingTimeInterval]);
        return new ShowEntityPathSettings(dict.pathColor, dict.pathAlpha, dict.clickedEntityTint,
            dict.pathWidth, dict.drawingTimeInterval);
    }
}