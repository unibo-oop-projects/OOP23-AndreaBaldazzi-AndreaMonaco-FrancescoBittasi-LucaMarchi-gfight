package gfight.view;

import gfight.common.Position2D;
import gfight.common.Rotation2D;

/**
 * A component to describe how an object should be visualized.
 */
public interface GraphicsComponent {

    /**
     * Colors available in the game.
     * (we don't use JSwing colors to allow different implementations of the view)
     */
    enum EngineColor {
        /**
         * Blue.
         */
        BLUE,
        /**
         * Red.
         */
        RED, 
        /**
         * Black.
         */
        BLACK,
        /**
         * Yellow.
         */
        YELLOW
    }

    /**
     * @return the color of the object.
     */
    EngineColor getColor();

    /**
     * @return the position in which to print the object.
     */
    Position2D getPosition();

    /**
     * @return the rotation of the object.
     */
    Rotation2D getRotation();

    /**
     * A setter for the color of the object.
     * @param color
     */
    void setColor(EngineColor color);

    /**
     * A setter for the position of the object.
     * @param pos
     */
    void setPosition(Position2D pos);

    /**
     * A setter for the rotation of the object.
     * @param rot
     */
    void setRotation(Rotation2D rot);

}