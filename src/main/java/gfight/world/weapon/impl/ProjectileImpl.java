package gfight.world.weapon.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import gfight.common.api.Position2D;
import gfight.engine.graphics.api.GraphicsComponent;
import gfight.world.entity.api.GameEntity;
import gfight.world.entity.api.Character.CharacterType;
import gfight.world.entity.api.Character;
import gfight.world.entity.impl.AbstractActiveEntity;
import gfight.world.map.impl.Chest;
import gfight.world.map.impl.Obstacle;
import gfight.world.movement.api.Movement;
import gfight.world.weapon.api.Projectile;

/**
 * Implementation of a simple projectile.
 * It damages the ActiveEntities that come in touch with it and are of the opponent team.
 */
public class ProjectileImpl extends AbstractActiveEntity implements Projectile {

    private static final int ALIVE_HEALTH = 1;

    private int damage;
    private boolean collided;
    private final Character.CharacterType team;

    /**
     * Constructor of a simple projectile.
     * @param vertexes  vertexes of the projectile
     * @param position  central position
     * @param gComp     GraphicsComponent of the projectile
     * @param team      Team who shoot the projectile
     * @param movement  Movement of the projectile
     * @param damage    Damage of the projectile
     */
    public ProjectileImpl(
        final List<Position2D> vertexes,
        final Position2D position,
        final GraphicsComponent gComp,
        final Character.CharacterType team,
        final Movement movement,
        final int damage
        ) {
        super(vertexes, position, gComp, ALIVE_HEALTH);
        this.setMovement(Optional.ofNullable(movement));
        this.team = team;
        this.damage = damage;
    }

    @Override
    public final void setDamage(final int damage) {
        this.damage = damage;
    }

    @Override
    protected final void applyCollisions(final Set<? extends GameEntity> gameobjects) {
        final var collidedObjects = getAllCollided(gameobjects);
        this.collided = collidedObjects.stream()
            .anyMatch(obj -> {
                if (obj instanceof Obstacle || obj instanceof Chest) {
                    if (obj instanceof Chest && this.team != CharacterType.PLAYER) {
                        ((Chest) obj).takeDamage(this.damage);
                    }
                    return true;
                }
                if (obj instanceof Character) {
                    final Character character = (Character) obj;
                    if (character.getType() != this.team) {
                        character.takeDamage(damage);
                        return true;
                    }
                }
                return false;
            });
    }

    @Override
    public final boolean isAlive() {
        return !this.collided;
    }
}
