package com.planet_lia.match_generator.game.pathfinding;

import com.badlogic.gdx.ai.pfa.DefaultConnection;

public class PathNodeConnection extends DefaultConnection<PathNode> {

    public PathNodeConnection(PathNode fromNode, PathNode toNode) {
        super(fromNode, toNode);
    }

    @Override
    public float getCost() {
        return Math.abs(toNode.x - fromNode.x) + Math.abs(toNode.y - fromNode.y);
    }
}
