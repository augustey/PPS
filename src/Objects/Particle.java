package Objects;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.awt.geom.Point2D;

public class Particle {
    private static final double RADIUS = 5;
    private Point2D position;
    private double heading;
    private PPS pps;
    private Color color;

    public Particle(double x, double y, double heading, PPS pps) {
        position = new Point2D.Double(x, y);
        color = Color.WHITE;
        this.heading = heading;
        this.pps = pps;
    }

    public void moveForward(double v) {
        double theta = heading * Math.PI/180;
        double newX = position.getX() + Math.cos(theta) * v;
        double newY = position.getY() + Math.sin(theta) * v;
        double size = pps.getSize();

        if(newX > size)
            newX %= size;
        else if(newX < 0)
            newX += size;

        if(newY > size)
            newY %= size;
        else if(newY < 0)
            newY += size;

        position = new Point2D.Double(newX, newY);
    }

    public void rotate(double angle) {
        heading += angle;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Point2D getPosition() {
        return position;
    }

    public double getHeading() {
        return heading;
    }

    public void show(GraphicsContext ctx, double su) {
        ctx.setFill(color);
        ctx.fillOval(position.getX()*su - RADIUS/2, position.getY()*su - RADIUS/2, RADIUS, RADIUS);
    }
}
