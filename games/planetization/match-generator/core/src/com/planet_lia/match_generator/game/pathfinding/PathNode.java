package com.planet_lia.match_generator.game.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedNode;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

public class PathNode implements IndexedNode<PathNode> {
    public float x;
    public float y;
    int index;

    Array<Connection<PathNode>> connections = new Array<>();

    public ArrayList<DefaultGraphPath<PathNode>> allPaths = new ArrayList<>();

    public PathNode(float x, float y, int index) {
        this.x = x;
        this.y = y;
        this.index = index;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public Array<Connection<PathNode>> getConnections() {
        return connections;
    }

    public void addNeighbours(ArrayList<PathNode> nodes, int... indexes) {
        for (int index : indexes) {
            PathNode node = nodes.get(index);
            if (node != null) {
                connections.add(new PathNodeConnection(this, node));
            }
        }
    }
}
