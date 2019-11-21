package com.planet_lia.match_generator.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.planet_lia.match_generator.game.Assets;
import com.planet_lia.match_generator.game.GameConfig;
import com.planet_lia.match_generator.libs.replays.Replay;

import java.util.ArrayList;

public class Background {

    private Sprite sprite;

    public ArrayList<ArrayList<Tile>> tiles = new ArrayList<>();

    public Background(Replay replay) {
        Texture texture = Assets.get(Assets.tile, Texture.class);
        if (texture != null) {
            sprite = new Sprite(texture);
        }

        float tileSize = GameConfig.values.tileSize;

        // Fill in all tiles
        for (int y = 0; y < GameConfig.values.mapHeight; y += tileSize) {
            tiles.add(new ArrayList<>());
            for (int x = 0; x < GameConfig.values.mapWidth; x += tileSize) {
                Tile tile = new Tile(replay, x, y);
                tiles.get(tiles.size() - 1).add(tile);
            }
        }

        // Remove a few tiles
        removeTiles();

        writeToReplay(replay);
    }

    private void removeTiles() {
        int nTilesToRemove = GameConfig.values.random.nextInt(20) + 10;
        System.out.println("Number of map gaps: " + nTilesToRemove);

        // Remove symmetrical tiles
        for (int i = 0; i < nTilesToRemove / 2f; i++) {

            // Never take the first tile when unit is located
            int x1 = GameConfig.values.random.nextInt(GameConfig.values.mapWidth - 1) + 1;
            int y1 = GameConfig.values.random.nextInt(GameConfig.values.mapHeight - 1) + 1;
            Tile tile1 = tiles.get(y1).get(x1);

            int x2 = GameConfig.values.mapWidth - x1 - 1;
            int y2 = GameConfig.values.mapHeight - y1 - 1;
            Tile tile2 = tiles.get(y2).get(x2);

            tiles.get(y1).set(x1, null);
            tiles.get(y2).set(x2, null);

            if (!areAllTilesConnected()) {
                // Reverse state and try again
                tiles.get(y1).set(x1, tile1);
                tiles.get(y2).set(x2, tile2);
                i--;
            }
        }
    }

    private boolean areAllTilesConnected() {
        // Set all tiles to be unmarked
        for (ArrayList<Tile> tilesRow : tiles) {
            for (Tile tile : tilesRow) {
                if (tile != null) {
                    tile.marked = false;
                }
            }
        }

        // Start at first tile and go through all the
        // neighbours recursively and mark them as visited
        markConnectedTiles(0, 0);

        // Check if we all tiles are connected
        for (ArrayList<Tile> tilesRow : tiles) {
            for (Tile tile : tilesRow) {
                if (tile != null) {
                    if (!tile.marked) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void markConnectedTiles(int x, int y) {
        if (x < 0 || y < 0 || x >= GameConfig.values.mapWidth || y >= GameConfig.values.mapHeight) {
            return;
        }

        Tile tile = tiles.get(y).get(x);
        if (tile == null || tile.marked) return;

        tile.marked = true;
        markConnectedTiles(x + 1, y);
        markConnectedTiles(x - 1, y);
        markConnectedTiles(x, y + 1);
        markConnectedTiles(x, y - 1);
    }

    private void writeToReplay(Replay replay) {
        for (ArrayList<Tile> tilesRow : tiles) {
            for (Tile tile : tilesRow) {
                if (tile != null) {
                    tile.writeToReplay(replay);
                }
            }
        }
    }

    public void draw(SpriteBatch batch) {
        for (ArrayList<Tile> tilesRow : tiles) {
            for (Tile tile : tilesRow) {
                if (tile != null) {
                    tile.draw(batch);
                }
            }
        }
    }
}
