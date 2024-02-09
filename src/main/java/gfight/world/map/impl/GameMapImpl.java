package gfight.world.map.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

import gfight.common.api.Position2D;
import gfight.common.impl.Position2DImpl;
import gfight.world.map.api.GameMap;
import gfight.world.map.api.GameTile;
import gfight.world.map.api.Spawner;
import gfight.world.map.api.GameTile.TileType;

/**
 * Standard implementation of a GameMap.
 */
public final class GameMapImpl implements GameMap {

    private final Map<Position2D, Spawner.SpawnerType> spawnersPositions;
    private final List<List<GameTile>> tiles;
    private final int width;
    private final int height;
    private Optional<Graph<GameTile, DefaultEdge>> tileGraph;

    /**
     * Utility method to convert tile grid coordinates to real game position.
     * 
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the real position
     */
    private Position2D realPosition(final int x, final int y) {
        return new Position2DImpl(x * TILE_DIM + TILE_DIM / 2, y * TILE_DIM + TILE_DIM / 2);
    }

    /**
     * Creates default obstacles inside the map, one for each diagonal direction.
     */
    private void defaultScatter() {
        this.tiles.get(this.width / 4).get(this.height / 4).setType(TileType.OBSTACLE);
        this.tiles.get(this.width / 4).get(3 * this.height / 4).setType(TileType.OBSTACLE);
        this.tiles.get(3 * this.width / 4).get(this.height / 4).setType(TileType.OBSTACLE);
        this.tiles.get(3 * this.width / 4).get(3 * this.height / 4).setType(TileType.OBSTACLE);
        this.spawnersPositions.put(
                new Position2DImpl(realPosition(this.width / 8, this.height / 2)),
                Spawner.SpawnerType.LINEAR);
        this.spawnersPositions.put(
                new Position2DImpl(realPosition(7 * this.width / 8, this.height / 2)),
                Spawner.SpawnerType.SCALAR);
        this.spawnersPositions.put(
                new Position2DImpl(realPosition(this.width / 2, 7 * this.height / 8)),
                Spawner.SpawnerType.BOSS);
    }

    /**
     * Creates a game map with the given dimensions.
     * 
     * @param width  the number of tiles of the width of the map
     * @param height the number of tiles the heigth of the map
     */
    public GameMapImpl(final int width, final int height) {
        this.spawnersPositions = new HashMap<>();
        this.width = width;
        this.height = height;
        this.tiles = new ArrayList<>(this.width);
        for (int i = 0; i < this.width; i++) {
            this.tiles.add(i, new ArrayList<>(this.height));
        }
        this.tileGraph = Optional.empty();
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                final GameTile tile = new GameTileImpl(
                        TileType.EMPTY,
                        realPosition(i, j),
                        TILE_DIM);
                if (i == 0 || i == width - 1 || j == 0 || j == height - 1) {
                    tile.setType(TileType.OBSTACLE);
                }
                this.tiles.get(i).add(j, tile);
            }
        }
        defaultScatter();
        this.tiles.get(this.width / 2).get(this.height / 2).setType(TileType.CHEST);
    }

    @Override
    public Position2D getChestPosition() {
        return this.tiles.stream()
                .flatMap(List::stream)
                .filter(t -> t.getType() == GameTile.TileType.CHEST)
                .findFirst()
                .get()
                .getPosition();
    }

    @Override
    public Position2D getPlayerSpawn() {
        return new Position2DImpl(realPosition(this.width / 2, this.height / 2 - 2));
    }

    public Set<Position2D> getObstaclesPositions() {
        return this.tiles.stream()
                .flatMap(List::stream)
                .filter(t -> t.getType() == GameTile.TileType.OBSTACLE)
                .map(GameTile::getPosition)
                .collect(Collectors.toSet());
    }

    @Override
    public Map<Position2D, Spawner.SpawnerType> getSpawnersPositions() {
        return Collections.unmodifiableMap(this.spawnersPositions);
    }

    @Override
    public GameTile searchTile(final Position2D position) {
        for (final var col : this.tiles) {
            for (final var tile : col) {
                if (tile.contains(position)) {
                    return tile;
                }
            }
        }
        throw new IllegalStateException("Entity at position " + position + " is not inside the map");
    }

    @Override
    public Graph<GameTile, DefaultEdge> getTileGraph() {
        if (!this.tileGraph.isPresent()) {
            buildGraph();
        }
        return this.tileGraph.get();
    }

    private void buildGraph() {
        final Graph<GameTile, DefaultEdge> g2 = new DefaultUndirectedGraph<>(DefaultEdge.class);
        final var freeCondition = GameTile.TileType.EMPTY;
        for (final var col : this.tiles) {
            for (final var tile : col) {
                g2.addVertex(tile);
            }
        }
        // col (x)
        for (int i = 0; i < this.width; i++) {
            // row (y)
            for (int j = 0; j < this.height; j++) {
                final var tile = this.tiles.get(i).get(j);
                Objects.requireNonNull(tile);
                if (tile.getType().equals(freeCondition)) {
                    // NORTH
                    if (j > 0 && this.tiles.get(i).get(j - 1).getType().equals(freeCondition)) {
                        g2.addEdge(tile, this.tiles.get(i).get(j - 1));
                    }
                    // SOUTH
                    if (j < (width - 1) && this.tiles.get(i).get(j + 1).getType().equals(freeCondition)) {
                        g2.addEdge(tile, this.tiles.get(i).get(j + 1));
                    }
                    // WEST
                    if (i > 0 && this.tiles.get(i - 1).get(j).getType().equals(freeCondition)) {
                        g2.addEdge(tile, this.tiles.get(i - 1).get(j));
                    }
                    // EAST
                    if (i < (width - 1) && this.tiles.get(i + 1).get(j).getType().equals(freeCondition)) {
                        g2.addEdge(tile, this.tiles.get(i + 1).get(j));
                    }
                }
            }
        }
        this.tileGraph = Optional.of(g2);
    }
}
