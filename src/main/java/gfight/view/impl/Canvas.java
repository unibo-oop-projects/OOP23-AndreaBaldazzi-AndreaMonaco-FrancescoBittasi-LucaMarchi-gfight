package gfight.view.impl;

import javax.swing.JPanel;

import gfight.common.Pair;
import gfight.engine.graphics.api.GraphicsComponent;
import gfight.engine.graphics.api.RenderableGraphicComponent;
import gfight.engine.graphics.api.ViewableCamera;
import gfight.engine.input.api.InputEventFactory;
import gfight.engine.input.api.InputEventListener;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.util.List;
import java.util.Optional;

/**
 * Canvas class for JSwing,
 * extends a JPanel to create a panel in which we can freely paint using a GraphicsRenderer to draw GraphicsComponents.
 */
public final class Canvas extends JPanel implements KeyListener, MouseMotionListener, MouseListener {
    private static final long serialVersionUID = -4058048042685678594L;

    //private final int centerX;
    //private final int centerY;
    private final transient SwingView scene;
    private final transient ViewableCamera camera;

    private Optional<InputEventListener> inputListener;
    private Optional<InputEventFactory> inputFactory;

    Canvas(final int width, final int height, final SwingView scene, final ViewableCamera camera) {
        this.scene = scene;
        this.camera = camera;
        //this.centerX = width / 2;
        //this.centerY = height / 2;

        setSize(width, height);
        this.addKeyListener(this);
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
    }

    @Override
    public void paint(final Graphics g) {
        if (!(g instanceof Graphics2D)) {
            throw new IllegalArgumentException("Needs Graphics2D to render correctly");
        }
        final Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.clearRect(0, 0, this.getWidth(), this.getHeight());

        //final GraphicsRenderer renderer = new SwingGraphicsRenderer(g2,this.camera);
        final List<GraphicsComponent> gCompList = scene.getGraphicsComponents();
        gCompList.stream()
            .filter(comp -> comp instanceof RenderableGraphicComponent)
            .map(comp -> (RenderableGraphicComponent) comp)
            .forEach(comp -> comp.getRenderer().render(g2, this.camera));
    }

    void setInputEventListener(InputEventListener inputListener) {
        this.inputListener = Optional.ofNullable(inputListener);
    }

    void setInputEventFactory(InputEventFactory inputFactory) {
        this.inputFactory = Optional.ofNullable(inputFactory);
    }

    private boolean isInputAvailable() {
        return this.inputListener.isPresent() && this.inputFactory.isPresent();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (isInputAvailable()) {
            this.inputListener.get().notifyInputEvent(
                this.inputFactory.get().mouseDownAtPosition(
                    this.camera.getWorldPosition(new Pair(e.getX(), e.getY()))
                )
            );
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (isInputAvailable()) {
            this.inputListener.get().notifyInputEvent(
                this.inputFactory.get().pressedKey(e.getKeyCode())
            );
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (isInputAvailable()) {
            this.inputListener.get().notifyInputEvent(
                this.inputFactory.get().releasedKey(e.getKeyCode())
            );
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (isInputAvailable()) {
            this.inputListener.get().notifyInputEvent(
                this.inputFactory.get().mouseDownAtPosition(
                    this.camera.getWorldPosition(new Pair(e.getX(), e.getY()))
                )
            );
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
