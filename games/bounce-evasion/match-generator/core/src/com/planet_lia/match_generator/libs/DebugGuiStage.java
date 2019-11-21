package com.planet_lia.match_generator.libs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import com.planet_lia.match_generator.libs.BotListener.MessageSender;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DebugGuiStage extends Stage {

    private final Lock lock = new ReentrantLock(true);

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
    private VisTextButton refreshButton = new VisTextButton("Refresh");

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
        mainTable.add(refreshButton).row();
        mainTable.add(content).grow().row();

        refreshButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                setActiveButton(activeButton);
                refreshButton.setDisabled(true);
            }
        });
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

        entityDetails = new ScrollableTextArea("Click on an entity to display its details.");
        entityDetailsScrollPane = entityDetails.createCompatibleScrollPane();
        mainTable.add(entityDetailsScrollPane).height(entityDetailsPaneHeight).row();
    }

    @Override
    public void act() {
        lock.lock();
        try {
            super.act();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void draw() {
        lock.lock();
        try {
            super.draw();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    void setActiveButton(TabButton button) {
        if (activeButton != null) {
            activeButton.setSelected(false);
        }
        button.setSelected(true);
        activeButton = button;

        // Switch content
        content.clear();
        button.showText();
        content.add(button.scrollPane).grow().maxHeight(Gdx.graphics.getHeight() - tabsTable.getHeight() * 2f);
        content.pack();
        mainTable.pack();
    }

    void addLog(int botIndex, MessageSender sender, String message) {
        ArrayList<TabButton> bots = (sender == MessageSender.BOT) ? fromBots : toBots;
        bots.get(botIndex).saveText(message);
        refreshButton.setDisabled(false);
    }

    void showLog(int botIndex, MessageSender sender, String message) {
        lock.lock();
        try {

            ArrayList<TabButton> bots = (sender == MessageSender.BOT) ? fromBots : toBots;
            bots.get(botIndex).showText();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    void updateEntityDetails(String text) {
        lock.lock();
        try {
            float y = entityDetailsScrollPane.getScrollY();
            entityDetails.setText(text);
            entityDetailsScrollPane.setScrollY(y);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}

/**
 * TabButton displays the name and holds the content of the tab as a ScrollPane
 */
class TabButton extends TextButton {

    ScrollableTextArea contentPanel;
    ScrollPane scrollPane;
    String text;
    boolean needRefresh = false;

    TabButton(DebugGuiStage logStage, String text) {
        super(text, VisUI.getSkin());
        setSelected(false);

        contentPanel = new ScrollableTextArea(text);
        scrollPane = contentPanel.createCompatibleScrollPane();

        saveText("");
        showText();

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

    void saveText(String text) {
        this.text = text;
        needRefresh = true;
    }

    void showText() {
        needRefresh = false;
        float y = scrollPane.getScrollY();
        contentPanel.setText(text);
        scrollPane.setScrollY(y);
        contentPanel.invalidateHierarchy();
    }

    void setSelected(boolean selected) {
        this.setColor(
                1f, 1f,
                (selected) ? 0f : 1f,
                (selected) ? 1f : 0.5f);
    }
}