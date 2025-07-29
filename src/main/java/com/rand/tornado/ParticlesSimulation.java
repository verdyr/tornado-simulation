package com.rand.tornado;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticlesSimulation {

    private long window;
    private int width = 800;
    private int height = 600;
    private List<Particle> particles = new ArrayList<>();
    private Random random = new Random();

    public void run() {
        init();
        loop();

        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    private void init() {
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        window = GLFW.glfwCreateWindow(width, height, "Tornado Simulation", MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities(); // Important: required to use OpenGL functions

        GLFW.glfwSwapInterval(1); // Enable v-sync

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, width, 0, height, -1, 1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        // Initialize particles
        for (int i = 0; i < 1000; i++) {
            particles.add(new Particle(width / 2f, height / 2f));
        }
    }

    private void loop() {
        while (!GLFW.glfwWindowShouldClose(window)) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            GL11.glLoadIdentity();

            // Update and draw particles
            for (Particle p : particles) {
                p.update();
                p.draw();
            }

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new TornadoSimulation().run();
    }

    class Particle {
        float x, y;
        float angle, radius, speed, lift;

        public Particle(float x, float y) {
            this.x = x;
            this.y = y;
            this.radius = random.nextFloat() * 50 + 10;
            this.angle = random.nextFloat() * 360;
            this.speed = random.nextFloat() * 0.5f + 0.5f;
            this.lift = random.nextFloat() * 1.5f;
        }

        void update() {
            angle += speed;
            radius *= 0.995;
            y += lift;
            x = width / 2f + (float) Math.cos(Math.toRadians(angle)) * radius;
        }

        void draw() {
            GL11.glColor3f(0.8f, 0.8f, 0.8f);
            GL11.glBegin(GL11.GL_POINTS);
            GL11.glVertex2f(x, y);
            GL11.glEnd();
        }
    }
}
