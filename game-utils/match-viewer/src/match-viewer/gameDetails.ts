import {errorIfUndefined} from "./curves/sections/section";
import {colorToNumber} from "./util/color";


export class GameDetails {

    gameNamePretty: string;
    gameName: string;
    gameVersion: string;
    assetsVersion: string;
    backgroundColor: string;

    camera: CameraSettings;
    showEntityPath: ShowEntityPathSettings | null;

    constructor(gameNamePretty: string, gameName: string, gameVersion: string, assetsVersion: string,
                backgroundColor: string, camera: CameraSettings, showEntityPath: ShowEntityPathSettings | null) {
        this.gameNamePretty = gameName;
        this.gameName = gameName;
        this.gameVersion = gameVersion;
        this.assetsVersion = assetsVersion;
        this.backgroundColor = backgroundColor;
        this.camera = camera;
        this.showEntityPath = showEntityPath;
    }

    static parse(dict: any): GameDetails {
        errorIfUndefined([dict.gameNamePretty, dict.gameName, dict.gameVersion, dict.assetsVersion,
            dict.backgroundColor, dict.camera, dict.showEntityPath]);

        let showEntityPath = (dict.showEntityPath == null)
            ? null
            : ShowEntityPathSettings.parse(dict.showEntityPath);

        return new GameDetails(
            dict.gameNamePretty, dict.gameName, dict.gameVersion, dict.assetsVersion, dict.backgroundColor,
            CameraSettings.parse(dict.camera),
            showEntityPath
        );
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