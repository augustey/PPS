package Util;

import Objects.Particle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;

public class PPSQuadTree {
    public Set<Particle> particles;
    private int depth;
    private int maxDepth;
    private int threshold;
    private Rectangle2D bounds;
    private PPSQuadTree[] nodes;

    public PPSQuadTree(int depth, Rectangle2D bounds, int threshold, int maxDepth) {
        particles = new HashSet<>();
        nodes = new PPSQuadTree[4];

        this.depth = depth;
        this.bounds = bounds;
        this.threshold = threshold;
        this.maxDepth = maxDepth;
    }

    public PPSQuadTree(double size, int threshold, int maxDepth) {
        this(0, new Rectangle2D.Double(0, 0, size, size), threshold, maxDepth);
    }

    public void add(Particle p) {
        if(!isLeaf()) {
            int index = getIndex(p);

            if(index != -1)
                nodes[index].add(p);
        }
        else {
            particles.add(p);

            if(particles.size() > threshold && depth < maxDepth) {
                split();

                for(Particle neighbor: particles) {
                    int index = getIndex(neighbor);

                    if(index != -1)
                        nodes[index].add(neighbor);
                }

                particles.clear();
            }
        }
    }

    public int size() {
        if(!isLeaf()) {
            return nodes[0].size() + nodes[1].size() + nodes[2].size() + nodes[3].size();
        }

        return particles.size();
    }

    public Set<Particle> getParticles(Set<Particle> closest, Rectangle2D selection) {
        if(!isLeaf()) {
            for(int i = 0; i < 4; i++) {
                if(nodes[i].overlaps(selection))
                    nodes[i].getParticles(closest, selection);
            }
        }

        closest.addAll(particles);

        return closest;
    }

    public Set<Particle> getParticles(Particle p, double radius) {
        double x = p.getPosition().getX();
        double y = p.getPosition().getY();

        Set<Particle> closest = getParticles(
                new HashSet<>(),
                new Rectangle2D.Double(x - radius, y - radius, radius*2, radius*2)
        );

        closest.remove(p);
        return closest;
    }

    public boolean overlaps(Rectangle2D area) {
        double sx1 = bounds.getX();
        double sy1 = bounds.getY();
        double sx2 = sx1 + bounds.getWidth();
        double sy2 = sy1 + bounds.getHeight();

        double ax1 = area.getX();
        double ay1 = area.getY();
        double ax2 = ax1 + area.getWidth();
        double ay2 = ay1 + area.getHeight();

        return !(ax1 > sx2 || ax2 < sx1 || ay1 > sy2 || ay2 < sy1);
    }

    public void clear() {
        for(int i = 0; i < 4; i++) {
            if(nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }

        particles.clear();
    }

    public int getIndex(Particle p) {
        double x = p.getPosition().getX();
        double y = p.getPosition().getY();
        double cx = bounds.getCenterX();
        double cy = bounds.getCenterY();

        if (x <= cx && y <= cy)
            return 0;
        else if (x > cx && y <= cy)
            return 1;
        else if (x <= cx && y > cy)
            return 2;
        else if (x > cx && y > cy)
            return 3;
        else
            return -1;
    }

    public void split() {
        double x = bounds.getX();
        double y = bounds.getY();
        double s = bounds.getWidth() / 2;

        nodes[0] = new PPSQuadTree(depth+1, new Rectangle2D.Double(x, y, s, s), threshold, maxDepth);
        nodes[1] = new PPSQuadTree(depth+1, new Rectangle2D.Double(x + s, y, s, s), threshold, maxDepth);
        nodes[2] = new PPSQuadTree(depth+1, new Rectangle2D.Double(x, y + s, s, s), threshold, maxDepth);
        nodes[3] = new PPSQuadTree(depth+1, new Rectangle2D.Double(x + s, y + s, s, s), threshold, maxDepth);
    }

    public boolean isLeaf() {
        return nodes[0] == null;
    }

    public void show(GraphicsContext ctx, double su) {
        if(!isLeaf()) {
            for(int i = 0; i < 4; i++) {
                nodes[i].show(ctx, su);
            }
        }
        ctx.setStroke(Color.RED);
        ctx.strokeRect(bounds.getX()*su, bounds.getY()*su, bounds.getWidth()*su, bounds.getHeight()*su);
    }
}
