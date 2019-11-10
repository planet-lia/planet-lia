package com.planet_lia.match_generator.game.pathfinding;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.planet_lia.match_generator.game.Assets;
import com.planet_lia.match_generator.game.GameConfig;
import com.planet_lia.match_generator.game.entities.Planet;

import java.util.ArrayList;

public class Grid {

    public ArrayList<PathNode> nodes = new ArrayList<>();
    public NodeGraph nodeGraph;
    public IndexedAStarPathFinder<PathNode> pathFinder;
    public ManhattanDistance heuristic = new ManhattanDistance();
    Sprite sprite;

    ArrayList<Planet> planets;

    public Grid(ArrayList<Planet> planets) {
        sprite = Assets.setTextureToSprite(sprite, Assets.whiteIndicator);
        this.planets = planets;

        // TODO this is hardcoded due to lack of development time, to improve

        int index = 0;

        // Create nodes
        // Row 1 (bottom)
        nodes.add(new PathNode(planets.get(4).x, planets.get(0).y, index++));
        nodes.add(new PathNode(planets.get(5).x, planets.get(0).y, index++));
        // Row 2
        nodes.add(new PathNode(halfX(planets,0, 4), planets.get(4).y, index++));
        nodes.add(new PathNode(halfX(planets,4, 7), planets.get(4).y, index++));
        nodes.add(new PathNode(halfX(planets,7, 8), planets.get(4).y, index++));
        nodes.add(new PathNode(halfX(planets,8, 5), planets.get(4).y, index++));
        nodes.add(new PathNode(halfX(planets,5, 9), planets.get(4).y, index++));
        // Row 3
        nodes.add(new PathNode(planets.get(4).x, planets.get(6).y, index++));
        nodes.add(new PathNode(planets.get(5).x, planets.get(6).y, index++));
        // Row 4
        nodes.add(new PathNode(halfX(planets,0, 4), planets.get(10).y, index++));
        nodes.add(new PathNode(halfX(planets,4, 7), planets.get(10).y, index++));
        nodes.add(new PathNode(halfX(planets,7, 8), planets.get(10).y, index++));
        nodes.add(new PathNode(halfX(planets,8, 5), planets.get(10).y, index++));
        nodes.add(new PathNode(halfX(planets,5, 9), planets.get(10).y, index++));
        // Row 5
        nodes.add(new PathNode(planets.get(4).x, planets.get(12).y, index++));
        nodes.add(new PathNode(planets.get(5).x, planets.get(12).y, index++));
        // Row 6
        nodes.add(new PathNode(halfX(planets,0, 4), planets.get(16).y, index++));
        nodes.add(new PathNode(halfX(planets,4, 7), planets.get(16).y, index++));
        nodes.add(new PathNode(halfX(planets,7, 8), planets.get(16).y, index++));
        nodes.add(new PathNode(halfX(planets,8, 5), planets.get(16).y, index++));
        nodes.add(new PathNode(halfX(planets,5, 9), planets.get(16).y, index++));
        // Row 7
        nodes.add(new PathNode(planets.get(4).x, planets.get(18).y, index++));
        nodes.add(new PathNode(planets.get(5).x, planets.get(18).y, index++));

        // Connect nodes
        nodes.get(0).addNeighbours(nodes, 2, 3);
        nodes.get(1).addNeighbours(nodes, 5, 6);
        nodes.get(2).addNeighbours(nodes, 0, 7, 9);
        nodes.get(3).addNeighbours(nodes, 0, 7, 10, 4);
        nodes.get(4).addNeighbours(nodes, 3, 5, 11);
        nodes.get(5).addNeighbours(nodes, 4, 1, 8, 12);
        nodes.get(6).addNeighbours(nodes, 1, 8, 13);
        nodes.get(7).addNeighbours(nodes, 2, 3, 9, 10);
        nodes.get(8).addNeighbours(nodes, 5, 6, 12, 13);
        nodes.get(9).addNeighbours(nodes, 2, 7, 14, 16);
        nodes.get(10).addNeighbours(nodes, 7, 3, 11, 14, 17);
        nodes.get(11).addNeighbours(nodes, 4, 10, 12, 18);
        nodes.get(12).addNeighbours(nodes, 11, 8, 15, 19);
        nodes.get(13).addNeighbours(nodes, 6, 8, 15, 20);
        nodes.get(14).addNeighbours(nodes, 9, 10, 16, 17);
        nodes.get(15).addNeighbours(nodes, 12, 13, 19, 20);
        nodes.get(16).addNeighbours(nodes, 9, 14, 21);
        nodes.get(17).addNeighbours(nodes, 10, 14, 18, 21);
        nodes.get(18).addNeighbours(nodes, 11, 17, 19);
        nodes.get(19).addNeighbours(nodes, 12, 15, 18, 22);
        nodes.get(20).addNeighbours(nodes, 13, 15, 22);
        nodes.get(21).addNeighbours(nodes, 16, 17);
        nodes.get(22).addNeighbours(nodes, 19, 20);

        // Create NodeGraph
        nodeGraph = new NodeGraph(nodes.size());
        for (PathNode node : nodes) nodeGraph.addNode(node);

        // Create pathFinder
        pathFinder = new IndexedAStarPathFinder<>(nodeGraph, false);

        // Calculate shortest paths from all nodes to all nodes
        for (PathNode node1 : nodes) {
            for (PathNode node2 : nodes) {
                DefaultGraphPath<PathNode> path = new DefaultGraphPath<>();
                pathFinder.searchNodePath(node1, node2, heuristic, path);
                node1.allPaths.add(path);
            }
        }

        // Add close path nodes to every planet
        planets.get(0).addCloseNodePaths(nodes, 0, 2);
        planets.get(1).addCloseNodePaths(nodes, 0, 3, 4);
        planets.get(2).addCloseNodePaths(nodes, 4, 5, 1);
        planets.get(3).addCloseNodePaths(nodes, 1, 6);
        planets.get(4).addCloseNodePaths(nodes, 2, 0, 3, 7, 9, 10);
        planets.get(5).addCloseNodePaths(nodes, 1, 6, 8, 5, 12, 13);
        planets.get(6).addCloseNodePaths(nodes, 2, 7, 9);
        planets.get(7).addCloseNodePaths(nodes, 3, 4, 11, 10, 7);
        planets.get(8).addCloseNodePaths(nodes, 4, 5, 8, 12, 11);
        planets.get(9).addCloseNodePaths(nodes, 6, 8, 13);
        planets.get(10).addCloseNodePaths(nodes, 9, 7, 10, 14, 2, 3, 16, 17);
        planets.get(11).addCloseNodePaths(nodes, 12, 8, 13, 15, 5, 6, 19, 20);
        planets.get(12).addCloseNodePaths(nodes, 9, 14, 16);
        planets.get(13).addCloseNodePaths(nodes, 10, 11, 18, 17, 14);
        planets.get(14).addCloseNodePaths(nodes, 11, 12, 15, 19, 18);
        planets.get(15).addCloseNodePaths(nodes, 13, 15, 20);
        planets.get(16).addCloseNodePaths(nodes, 14, 17, 21, 16, 9, 10);
        planets.get(17).addCloseNodePaths(nodes, 15, 20, 22, 19, 12, 13);
        planets.get(18).addCloseNodePaths(nodes, 16, 21);
        planets.get(19).addCloseNodePaths(nodes, 21, 17, 18);
        planets.get(20).addCloseNodePaths(nodes, 18, 19, 22);
        planets.get(21).addCloseNodePaths(nodes, 20, 22);
    }

    private float halfX(ArrayList<Planet> planets, int i, int j) {
        Planet p1 = planets.get(i);
        Planet p2 = planets.get(j);
        return p1.x + (p2.x - p1.x) / 2f;
    }

    public void draw(SpriteBatch batch) { }
}
