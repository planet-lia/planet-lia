package com.planet_lia.match_generator.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
    public static String tile = GameConfig.values.pathToImages + "tile.png";
    @Asset(value = Texture.class, param = "defaultTextureParam")
    public static String unitYellow = GameConfig.values.pathToImages + "unit-yellow.png";
    @Asset(value = Texture.class, param = "defaultTextureParam")
    public static String unitGreen = GameConfig.values.pathToImages + "unit-green.png";
    @Asset(value = Texture.class, param = "defaultTextureParam")
    public static String coin = GameConfig.values.pathToImages + "coin.png";
    @Asset(value = Texture.class, param = "defaultTextureParam")
    public static String saw = GameConfig.values.pathToImages + "saw.png";
    @Asset(value = Texture.class, param = "defaultTextureParam")
    public static String life = GameConfig.values.pathToImages + "life.png";
    @Asset(value = Texture.class, param = "defaultTextureParam")
    public static String hudBg = GameConfig.values.pathToImages + "hud-bg.png";
    @Asset(value = Texture.class, param = "defaultTextureParam")
    public static String endGameOverlay = GameConfig.values.pathToImages + "end-game-overlay.png";
    @Asset(value = BitmapFont.class, param = "defaultFontParameter")
    public static String hudFont = GameConfig.values.pathToFonts + "medium.ttf";


    // // This is how you can add fonts::
    // @Asset(value = BitmapFont.class, param = "defaultFontParameter")
    // public static String defaultFont = defaultFontParameter.fontFileName;

    // // Add your assets here, for example:
    // @Asset(value = Texture.class, param = "textureParam")
    // public static String ninja = "images/ninja.png";
    // // and then get it after you call load(...) method with:
    // Texture ninjaTex = Assets.get(Assets.ninja, Texture.class);
    // // More examples at:
    // // https://bitbucket.org/dermetfan/libgdx-utils/wiki/net.dermetfan.gdx.assets.AnnotationAssetManager

    protected static AnnotationAssetManager assetManager;

    public static void load() {
        // Prepare default texture parameter
        defaultTextureParam.minFilter = Texture.TextureFilter.Linear;
        defaultTextureParam.magFilter = Texture.TextureFilter.Linear;

        // Prepare default font parameter
        defaultFontParameter.fontFileName = GameConfig.values.pathToFonts + "medium.ttf";
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

    public static Sprite setTextureToSprite(Sprite sprite, String asset) {
        Texture tex = Assets.get(asset, Texture.class);
        if (tex != null) {
            if (sprite == null) {
                sprite = new Sprite(tex);
            }
            else sprite.setTexture(Assets.get(asset, Texture.class));
        }
        return sprite;
    }

    public static void dispose() {
        assetManager.dispose();
    }
}
