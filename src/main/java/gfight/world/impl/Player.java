package gfight.world.impl;

import java.util.List;
import java.util.Optional;

import org.locationtech.jts.geom.Coordinate;
import java.util.Set;
import gfight.engine.graphics.api.GraphicsComponent;
import gfight.world.api.CachedGameEntity;
import gfight.world.movement.api.Movement;

/**
 * Class that represents the Player of the game.
 */
public final class Player extends AbstractCharacter {

    /**
     * Constructor for player.
     * @param vertexes
     * @param position
     * @param graphicsComponent
     * @param movement
     * @param health
     */
    public Player(final List<Coordinate> vertexes, final Coordinate position, final GraphicsComponent graphicsComponent,
            final Optional<Movement> movement, final int health) {
        super(vertexes, position, graphicsComponent, movement, health);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void applyCollisions(final Set<CachedGameEntity> gameobjects) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'applyCollisions'");
    }

}