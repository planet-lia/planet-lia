package com.planet_lia.match_generator.libs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class ControlsStage extends Stage {

    private VisTable mainTable = new VisTable();
    private VisTable centerContainer = new VisTable();

    float speed = 0f;
    float backupSpeed = 1f;

    VisLabel timeLabel = new VisLabel(0.00 + "");
    VisTextButton pauseResumeButton = new VisTextButton("Start");
    VisSlider speedSlider = new VisSlider(0.1f, 20, 0.1f, false);
    VisLabel speedLabel = new VisLabel(backupSpeed + "x");
    VisTextButton maxSpeedButton = new VisTextButton("Fast forward");

    boolean paused = true;


    public ControlsStage(Viewport viewport, Timer timer) {
        super(viewport);

        // Create main wrapper
        mainTable.setFillParent(true);
        mainTable.pad(10);
        addActor(mainTable);

        // Add centered container
        centerContainer.defaults().pad(10);
        mainTable.add(centerContainer);

        // Add elements
        centerContainer.add(timeLabel).expandX();
        addPauseResumeButton();
        addSpeedControls();
        addMaxSpeedButton();
    }

    private void addPauseResumeButton() {
        pauseResumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (paused) {
                    paused = false;
                    speed = backupSpeed;
                    pauseResumeButton.setText("Pause");
                } else {
                    paused = true;
                    backupSpeed = speed;
                    speed = 0;
                    pauseResumeButton.setText("Resume");
                }
            }
        });

        centerContainer.add(pauseResumeButton).expandX();
    }

    public void setTime(float time) {
        this.timeLabel.setText(String.format("%.2f", time));
    }

    public float getSpeed() {
        return speed;
    }

    private void addSpeedControls() {
        // Speed slider
        speedSlider.setValue(backupSpeed);
        speedSlider.addListener(event -> {
            if (event instanceof InputEvent
                    && ((InputEvent) event).getType() == InputEvent.Type.touchDragged) {
                if (paused) {
                    backupSpeed = speedSlider.getValue();
                } else {
                    speed = speedSlider.getValue();
                }
                speedLabel.setText(String.format("%.1fx", speedSlider.getValue()));
            }
            return true;
        });

        centerContainer.add(speedSlider).width(getViewport().getWorldWidth() * 0.2f).padRight(0);

        // Speed label
        centerContainer.add(speedLabel);
    }

    private void addMaxSpeedButton() {
        maxSpeedButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (paused) {
                    backupSpeed = speedSlider.getValue();
                } else {
                    speed = 100;
                }
                speedLabel.setText(("max"));
            }
        });

        centerContainer.add(maxSpeedButton).expandX();
    }

}