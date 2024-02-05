package gfight.world.entity.impl;

import java.util.List;
import java.util.Set;

import gfight.common.api.Position2D;
import gfight.common.api.Vect;
import gfight.common.impl.VectorImpl;
import gfight.engine.graphics.api.GraphicsComponent;
import gfight.world.weapon.api.Weapon;
import gfight.world.collision.api.CollisionCommand;
import gfight.world.collision.impl.SlideCommand;
import gfight.world.entity.api.Character;
import gfight.world.entity.api.GameEntity;
import gfight.world.entity.api.MovingEntity;
import gfight.world.hitbox.api.Hitboxes;
import gfight.world.hitbox.impl.HitboxesImpl;

/**
 * Class that represents the concept of main character (Player, Enemies).
 */
public class CharacterImpl extends AbstractActiveEntity implements Character {
    private Weapon weapon;
    private Vect pointingDirection = new VectorImpl(0, 0);
    private CharacterType role;

    /**
     * Constructor of abstract character.
     * 
     * @param vertexes of the shape
     * @param position the centre of the character
     * @param graphicsComponent the color of the character
     * @param health life point of the character
     */
    public CharacterImpl(final List<Position2D> vertexes, final Position2D position,
            final GraphicsComponent graphicsComponent, final int health, CharacterType role) {
        super(vertexes, position, graphicsComponent, health);
        this.role = role;
    }

    @Override
    public final void rotate(final double theta) {
        Hitboxes rotation = new HitboxesImpl();
        rotation.rotate(getPosition2Ds(), theta);
    }

    @Override
    public final void setWeapon(final Weapon weapon) {
        this.weapon = weapon;
    }

    @Override
    public final void makeDamage() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'shoot'");
    }

    @Override
    public Vect getPointedDirection() {
        return this.pointingDirection;
    }

    @Override
    public void setPointingDirection(Vect pointingDirection) {
        this.pointingDirection = pointingDirection;
    }

    @Override
    public CharacterType getType() {
        return this.role;
    }

    @Override
    protected void applyCollisions(Set<GameEntity> gameobjects) {
        getAllCollided(gameobjects).stream().forEach(el -> {
            if (el instanceof GameEntity) {
                CollisionCommand coll = new SlideCommand<MovingEntity, GameEntity>(this, el);
                coll.execute();
            }
        });
    }

}
