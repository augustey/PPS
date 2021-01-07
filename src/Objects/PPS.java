package Objects;

import Util.PPSQuadTree;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class PPS extends Thread {
    //System variables
    private long timeStepMili;
    private boolean active;
    private List<Particle> particles;
    private PPSQuadTree quadTree;
    private double size;

    //Simulation variables
    private double radius;
    private double alpha;
    private double beta;
    private double velocity;

    public PPS(int numParticles, double size, long timeStepMili) {
        Random rng = new Random();
        particles = new ArrayList<>();
        quadTree = new PPSQuadTree(size, 5, 5);

        for(int i = 0; i < numParticles; i++) {
            particles.add(new Particle(
                    rng.nextDouble()*size,
                    rng.nextDouble()*size,
                    rng.nextDouble()*360,
                    this
            ));
        }

        this.timeStepMili = timeStepMili;
        this.size = size;
    }

    public void initialize(double radius, double alpha, double beta, double velocity) {
        this.radius = radius;
        this.alpha = alpha;
        this.beta = beta;
        this.velocity = velocity;
        active = true;
    }

    @Override
    public void run() {
        while(active) {
            try {
                mainLoop();
                sleep(timeStepMili);
            }
            catch(InterruptedException e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }

    public synchronized void mainLoop() {
        particles.forEach(p -> quadTree.add(p));

        for(Particle p: particles) {
            int[] n = countNeighbors(p);
            int left = n[0];
            int right = n[1];
            int immediate = n[2];
            int neighbors = left + right;

            if(neighbors <= 35 && neighbors > 15)
                p.setColor(Color.rgb(251, 123, 108));
            else if(neighbors > 35)
                p.setColor(Color.rgb(255, 255, 0));
            else if(neighbors <= 15 && neighbors >= 13)
                p.setColor(Color.rgb(16, 141, 199));
            else if(immediate > 15)
                p.setColor(Color.rgb(255, 0, 0));
            else
                p.setColor(Color.rgb(45, 250, 204));

            p.rotate(alpha + beta * neighbors * Math.signum(right - left));
            p.moveForward(velocity);
        }
        quadTree.clear();
    }

    public void end() {
        active = false;
    }

    public int[] countNeighbors(Particle p) {
        Set<Particle> candidates = quadTree.getParticles(p, radius);
        int left = 0;
        int right = 0;
        int immediate = 0;

        for(Particle other: candidates) {
            double dist = getDist(p, other);
            if(dist <= radius) {
                double theta = (90 - p.getHeading()) * Math.PI/180;
                double thisx = p.getPosition().getX()*Math.cos(theta) - p.getPosition().getY()*Math.sin(theta);
                double otherx = other.getPosition().getX()*Math.cos(theta) - other.getPosition().getY()*Math.sin(theta);


                if(otherx - thisx <= 0)
                    right++;
                else
                    left++;
            }

            if(dist <= 1.3)
                immediate++;
        }

        return new int[] {left, right, immediate};
    }

    public double getDist(Particle p1, Particle p2) {
        return p1.getPosition().distance(p2.getPosition());
    }

    public double getSize() {
        return size;
    }

    public synchronized void show(GraphicsContext ctx) {
        double su = ctx.getCanvas().getWidth()/size;
//        particles.forEach(p ->quadTree.add(p));
//        quadTree.show(ctx, su);
//        quadTree.clear();

        for(Particle p: particles) {
            p.show(ctx, su);
        }
    }
}
