package com.planet_lia.match_generator.libs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

public class EntityDetailsSystem implements InputProcessor {

    private Vector2 position = new Vector2();

    private ArrayList<Clickable> entities = new ArrayList<>();
    private Viewport viewport;
    private DebugGuiStage debugGuiStage;

    private Clickable selectedEntity;

    public EntityDetailsSystem(Viewport viewport, DebugGuiStage debugGuiStage) {
        super();
        this.viewport = viewport;
        this.debugGuiStage = debugGuiStage;
    }

    public void registerEntity(Clickable entity) {
        entities.add(entity);
    }

    public void unregisterEntity(Clickable entity) {
        entities.remove(entity);
    }

    public void update() {
        if (selectedEntity != null) {
            debugGuiStage.updateEntityDetails(selectedEntity.getDisplayText());
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Ignore if its not left mouse button or first touch pointer
        if (button != Input.Buttons.LEFT || pointer > 0) {
            return false;
        }
        Clickable hooveredEntity = getHooveredEntity(screenX, screenY);
        if (hooveredEntity != null) {
            debugGuiStage.updateEntityDetails(hooveredEntity.getDisplayText());
            selectedEntity = hooveredEntity;
        }

        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Clickable hooveredEntity = getHooveredEntity(screenX, screenY);
        if (hooveredEntity != null) {
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
        }
        else {
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        }
        return true;
    }

    private Clickable getHooveredEntity(int screenX, int screenY) {
        if (screenX > viewport.getScreenWidth() || screenY > viewport.getScreenHeight()) {
            return null;
        }

        viewport.unproject(position.set(screenX, screenY));

        // Find the hovered entity
        Clickable hoveredEntity = null;
        int hoveredEntityLayer = -100000;

        for (Clickable entity : entities) {
            if (position.x >= entity.getX() - entity.getWidth() * 0.5f
                    && position.x <= entity.getX() + entity.getWidth() * 0.5f
                    && position.y >= entity.getY() - entity.getHeight() * 0.5f
                    && position.y <= entity.getY() + entity.getHeight() * 0.5f) {
                if (entity.getLayer() >= hoveredEntityLayer) {
                    hoveredEntity = entity;
                    hoveredEntityLayer = entity.getLayer();
                }
            }
        }

        return hoveredEntity;
    }

    // **************************** Unused ****************************

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }
}
