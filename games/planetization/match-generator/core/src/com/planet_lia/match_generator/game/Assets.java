package com.planet_lia.match_generator.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;
import net.dermetfan.gdx.assets.AnnotationAssetManager;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class Assets {

    private static TextureLoader.TextureParameter defaultTextureParam = new TextureLoader.TextureParameter();
    private static FreeTypeFontLoaderParameter defaultFontParameter = new FreeTypeFontLoaderParameter();

    @Asset(value = Texture.class, param = "defaultTextureParam")
    public static String background = GameConfig.values.pathToImages + "bg.png";
    @Asset(value = Texture.class, param = "defaultTextureParam")
    public static String planetGrey = GameConfig.values.pathToImages + "planet-grey.png";
    @Asset(value = Texture.class, param = "defaultTextureParam")
    public static String planetGreen = GameConfig.values.pathToImages + "planet-green.png";
    @Asset(value = Texture.class, param = "defaultTextureParam")
    public static String planetRed = GameConfig.values.pathToImages + "planet-red.png";
    @Asset(value = Texture.class, param = "defaultTextureParam")
    public static String redWorker = GameConfig.values.pathToImages + "red-worker.png";
    @Asset(value = Texture.class, param = "defaultTextureParam")
    public static String redWarrior = GameConfig.values.pathToImages + "red-warrior.png";
    @Asset(value = Texture.class, param = "defaultTextureParam")
    public static String greenWorker = GameConfig.values.pathToImages + "green-worker.png";
    @Asset(value = Texture.class, param = "defaultTextureParam")
    public static String greenWarrior = GameConfig.values.pathToImages + "green-warrior.png";
    @Asset(value = Texture.class, param = "defaultTextureParam")
    public static String greenIndicator = GameConfig.values.pathToImages + "green-indicator.png";
    @Asset(value = Texture.class, param = "defaultTextureParam")
    public static String redIndicator = GameConfig.values.pathToImages + "red-indicator.png";
    @Asset(value = Texture.class, param = "defaultTextureParam")
    public static String whiteIndicator = GameConfig.values.pathToImages + "white-indicator.png";

    protected static AnnotationAssetManager assetManager;

    public static void load() {
        // Prepare default texture parameter
        defaultTextureParam.minFilter = Texture.TextureFilter.Linear;
        defaultTextureParam.magFilter = Texture.TextureFilter.Linear;

        // Prepare default font parameter
        defaultFontParameter.fontFileName = "fonts/medium.ttf";
        defaultFontParameter.fontParameters.size = getFontHeight();

        // Prepare asset manager and load assets
        InternalFileHandleResolver resolver = new InternalFileHandleResolver();
        assetManager = new AnnotationAssetManager(resolver);
        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
        assetManager.load(Assets.class);
        assetManager.finishLoading();
    }

    private static int getFontHeight() {
        // Only calculate if debug mode
        System.out.println(Gdx.app.getType());
        if (Gdx.app.getType() == Application.ApplicationType.HeadlessDesktop) return 0;
        return (int) (GameConfig.values.defaultFontSize *
                (Gdx.graphics.getHeight() / GameConfig.values.cameraViewHeight));
    }

    public static <T> T get(String fileName, Class<T> type) {
        // In headless mode the asset manager will be null
        if (assetManager == null) return null;
        // In debug mode, assets will be able to load
        return assetManager.get(fileName, type);
    }

    public static void dispose() {
        assetManager.dispose();
    }
}
