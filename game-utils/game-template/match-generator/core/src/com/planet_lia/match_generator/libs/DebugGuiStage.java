package com.planet_lia.match_generator.libs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import com.planet_lia.match_generator.libs.BotListener.MessageSender;

import java.util.ArrayList;

public class DebugGuiStage extends Stage {

    private GeneralConfig generalConfig;

    private VisTable mainTable = new VisTable();
    private VisTable tabsTable = new VisTable();

    // Bot tabs
    private ArrayList<TabButton> toBots = new ArrayList<>();
    private ArrayList<TabButton> fromBots = new ArrayList<>();

    //Api content
    private VisTable content = new VisTable();
    private TabButton activeButton;

    // Entity details
    private ScrollableTextArea entityDetails;
    private VisTextButton hideEntityDetailsButton = new VisTextButton("Hide");
    private boolean isDetailsButtonDisplayed = true;
    private ScrollPane entityDetailsScrollPane;

    public DebugGuiStage(Viewport viewport, BotDetails[] botsDetails, GeneralConfig generalConfig) {
        super(viewport);

        this.generalConfig = generalConfig;

        // Create main wrapper
        mainTable.setFillParent(true);
        mainTable.pad(10).defaults().expandX().fillX().space(4);
        addActor(mainTable);

        addApiSection(botsDetails);

        addEntityDetailsSection();

        setActiveButton(toBots.get(0));
    }

    private void addApiSection(BotDetails[] botsDetails) {
        // Add title
        mainTable.add(new VisLabel("Api Calls")).pad(0).row();

        // Create tab buttons for all bots
        tabsTable.defaults().pad(5);
        for (int i = 0; i < botsDetails.length; i++) {
            toBots.add(new TabButton(this, "to " + botsDetails[i].botName  + " (" + i + ")"));
            fromBots.add(new TabButton(this, "from " + botsDetails[i].botName + " (" + i + ")"));
            tabsTable.add(toBots.get(i)).fillX();
            tabsTable.add(fromBots.get(i)).fillX();
        }
        tabsTable.pack();

        // Add Scroll bar to tab buttons
        VisScrollPane scrollPane = new VisScrollPane(tabsTable);
        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);
        mainTable.add(scrollPane).padTop(0).growX().minHeight(tabsTable.getHeight() * 1.5f).row();

        // Add api calls content
        mainTable.add(content).grow().row();
    }

    private void addEntityDetailsSection() {
        // Add title and buttons
        VisTable entityDetailsWrapper = new VisTable();
        entityDetailsWrapper.add(new VisLabel("Entity Details")).expandX().left();
        entityDetailsWrapper.add(hideEntityDetailsButton).right();
        mainTable.add(entityDetailsWrapper).pad(0).padTop(5).growX().row();
        float entityDetailsPaneHeight = Gdx.graphics.getHeight() * generalConfig.debugWindow.entityDetailsToScreenHeight;

        // Hide entity details panel on Hide button click
        hideEntityDetailsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (isDetailsButtonDisplayed) {
                    mainTable.getCell(entityDetailsScrollPane).height(0);
                    hideEntityDetailsButton.setText("Show");
                    entityDetailsScrollPane.setVisible(false);
                } else {
                    mainTable.getCell(entityDetailsScrollPane).height(entityDetailsPaneHeight);
                    hideEntityDetailsButton.setText("Hide");
                    entityDetailsScrollPane.setVisible(true);
                }
                isDetailsButtonDisplayed = !isDetailsButtonDisplayed;
            }
        });

        entityDetails = new ScrollableTextArea("Hover entity to display its details.");
        entityDetailsScrollPane = entityDetails.createCompatibleScrollPane();
        mainTable.add(entityDetailsScrollPane).height(entityDetailsPaneHeight).row();
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

    void updateEntityDetails(String text) {
        float y = entityDetailsScrollPane.getScrollY();
        entityDetails.setText(text);
        entityDetailsScrollPane.setScrollY(y);
    }
}

/**
 * TabButton displays the name and holds the content of the tab as a ScrollPane
 */
class TabButton extends TextButton {

    ScrollPane scrollPane;

    TabButton(DebugGuiStage logStage, String text) {
        super(text, VisUI.getSkin());
        addText("{\n" +
                "  \"data\": [{\n" +
                "    \"type\": \"articles\",\n" +
                "    \"id\": \"1\",\n" +
                "    \"attributes\": {\n" +
                "      \"title\": \"JSON:API paints my bikeshedmy bikeshedmy bikeshedmy bikeshedmy bikeshed!\",\n" +
                "      \"body\": \"The shortest article. Ever.\",\n" +
                "      \"created\": \"2015-05-22T14:56:29.000Z\",\n" +
                "      \"updated\": \"2015-05-22T14:56:28.000Z\"\n" +
                "    },\n" +
                "    \"relationships\": {\n" +
                "      \"author\": {\n" +
                "        \"data\": {\"id\": \"42\", \"type\": \"people\"}\n" +
                "      }\n" +
                "    }\n" +
                "  }],\n" +
                "  \"included\": [\n" +
                "    {\n" +
                "      \"type\": \"people\",\n" +
                "      \"id\": \"42\",\n" +
                "      \"attributes\": {\n" +
                "        \"name\": \"John\",\n" +
                "        \"age\": 80,\n" +
                "        \"gender\": \"male\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}" +
                "{\n" +
                "  \"data\": [{\n" +
                "    \"type\": \"articles\",\n" +
                "    \"id\": \"1\",\n" +
                "    \"attributes\": {\n" +
                "      \"title\": \"JSON:API paints my bikeshed!\",\n" +
                "      \"body\": \"The shortest article. Ever.\",\n" +
                "      \"created\": \"2015-05-22T14:56:29.000Z\",\n" +
                "      \"updated\": \"2015-05-22T14:56:28.000Z\"\n" +
                "    },\n" +
                "    \"relationships\": {\n" +
                "      \"author\": {\n" +
                "        \"data\": {\"id\": \"42\", \"type\": \"people\"}\n" +
                "      }\n" +
                "    }\n" +
                "  }],\n" +
                "  \"included\": [\n" +
                "    {\n" +
                "      \"type\": \"people\",\n" +
                "      \"id\": \"42\",\n" +
                "      \"attributes\": {\n" +
                "        \"name\": \"John\",\n" +
                "        \"age\": 80,\n" +
                "        \"gender\": \"male\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}");
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