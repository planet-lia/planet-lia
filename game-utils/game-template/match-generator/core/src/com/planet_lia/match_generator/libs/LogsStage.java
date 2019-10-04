package com.planet_lia.match_generator.libs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.ScrollableTextArea;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.planet_lia.match_generator.libs.BotListener.MessageSender;

import java.util.ArrayList;

public class LogsStage extends Stage {

    private VisTable mainTable;
    private VisTable tabsTable;

    private ArrayList<TabButton> toBots = new ArrayList<>();
    private ArrayList<TabButton> fromBots = new ArrayList<>();

    private TabButton activeButton;
    private VisTable content;

    public LogsStage(Viewport viewport, Timer timer, BotDetails[] botsDetails) {
        super(viewport);

        // Create main wrapper
        mainTable = new VisTable();
        mainTable.setFillParent(true);
        mainTable.pad(10).defaults().expandX().fillX().space(4);
        addActor(mainTable);

        // Add tabs for all bots
        tabsTable = new VisTable();
        tabsTable.defaults().pad(5);
        for (int i = 0; i < botsDetails.length; i++) {
            toBots.add(new TabButton(this, "to " + botsDetails[i].botName  + " (" + i + ")"));
            fromBots.add(new TabButton(this, "from " + botsDetails[i].botName + " (" + i + ")"));
            tabsTable.add(toBots.get(i)).fillX();
            tabsTable.add(fromBots.get(i)).fillX();
        }
        tabsTable.pack();

        // Add Scroll bar tabs
        VisScrollPane scrollPane = new VisScrollPane(tabsTable);
        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);
        mainTable.add(scrollPane).spaceTop(8).growX().row();

        content = new VisTable();
        mainTable.add(content).grow();

        setActiveButton(toBots.get(0));
    }

    void addLog(int botIndex, MessageSender sender, String message) {
        ArrayList<TabButton> bots = (sender == MessageSender.BOT) ? fromBots : toBots;
        bots.get(botIndex).addText(message);
    }

    void setActiveButton(TabButton button) {
        if (activeButton != null) {
            activeButton.setSelected(false);
        }
        button.setSelected(true);
        activeButton = button;

        // Switch content
        content.clear();
        content.add(button.scrollPane).grow().maxHeight(Gdx.graphics.getHeight() - tabsTable.getHeight() * 2f);
        content.pack();
        mainTable.pack();
    }
}

/**
 * TabButton displays the name and holds the content of the tab as a ScrollPane
 */
class TabButton extends TextButton {

    ScrollPane scrollPane;

    TabButton(LogsStage logStage, String text) {
        super(text, VisUI.getSkin());
        addText("");
        setSelected(false);

        // Register onClick listener
        TabButton thisButton = this;
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                logStage.setActiveButton(thisButton);
            }
        });
    }

    void addText(String text) {
        ScrollableTextArea contentPanel = new ScrollableTextArea(text);
        scrollPane = contentPanel.createCompatibleScrollPane();
    }

    void setSelected(boolean selected) {
        this.setColor(
                1f, 1f,
                (selected) ? 0f : 1f,
                (selected) ? 1f : 0.5f);
    }
}