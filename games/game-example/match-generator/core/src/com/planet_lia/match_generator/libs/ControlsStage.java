package com.planet_lia.match_generator.libs;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class ControlsStage extends Stage {

    private VisTable mainTable = new VisTable();
    private VisTable centerContainer = new VisTable();

    private float speed = 0f;
    private float backupSpeed = 1f;
    private boolean step = false;

    private VisLabel timeLabel = new VisLabel(0.00 + "");
    private VisTextButton pauseResumeButton = new VisTextButton("Start");
    private VisTextButton stepButton = new VisTextButton(">");
    private VisSlider speedSlider = new VisSlider(0.1f, 20, 0.1f, false);
    private VisLabel speedLabel = new VisLabel(backupSpeed + "x");
    private VisTextButton maxSpeedButton = new VisTextButton("Fast forward");

    private boolean paused = true;


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
        addStepButton();
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
                    stepButton.setDisabled(true);
                    pauseResumeButton.setText("Pause");
                } else {
                    paused = true;
                    backupSpeed = speed;
                    speed = 0;
                    stepButton.setDisabled(false);
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

    public boolean isStep() {
        return step;
    }

    public void disableStep() {
        this.step = false;
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
                speed = 100;
                paused = false;
                stepButton.setDisabled(true);
                pauseResumeButton.setText("Pause");
                speedLabel.setText(("max"));
            }
        });

        centerContainer.add(maxSpeedButton).expandX();
    }

    private void addStepButton() {
        stepButton.setDisabled(false);

        stepButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                step = true;
            }
        });

        centerContainer.add(stepButton).expandX();
    }

}