package gfight.world.impl;

import java.util.List;
import java.util.Optional;

import org.locationtech.jts.geom.Coordinate;
import java.util.Set;
import gfight.engine.graphics.api.GraphicsComponent;
import gfight.world.api.GameEntity;
import gfight.world.api.MovingEntity;
import gfight.world.collision.api.CollisionCommand;
import gfight.world.collision.impl.SlideCommand;
import gfight.world.movement.api.Movement;

/**
 * Class that represents the Player of the game.
 */
public final class Player extends AbstractCharacter {

    /**
     * Constructor for player.
     * 
     * @param vertexes
     * @param position
     * @param graphicsComponent
     * @param movement
     * @param health
     */
    public Player(final List<Coordinate> vertexes, final Coordinate position, final GraphicsComponent graphicsComponent,
            final Optional<Movement> movement, final int health) {
        super(vertexes, position, graphicsComponent, health);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void applyCollisions(final Set<GameEntity> gameobjects) {
        getAllCollided(gameobjects).stream().forEach(el -> {
            if (el instanceof GameEntity) {
                CollisionCommand coll = new SlideCommand<MovingEntity, GameEntity>(this, el);
                coll.execute();
            }
        });
    }

}
