package com.rand.tornado;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class TornadoSimulation {

    private long window;
    private final int width = 800;
    private final int height = 600;

    private final int numParticles = 1000;
    private final float[][] particles = new float[numParticles][3];
    private final float[] angles = new float[numParticles];
    private final Random rand = new Random();

    private void initParticles() {
        for (int i = 0; i < numParticles; i++) {
            angles[i] = rand.nextFloat() * 360f;
            particles[i][1] = rand.nextFloat() * 30f; // height
            float radius = (particles[i][1] / 30f) * 5f;
            particles[i][0] = radius * (float) Math.cos(Math.toRadians(angles[i]));
            particles[i][2] = radius * (float) Math.sin(Math.toRadians(angles[i]));
        }
    }

    public void run() {
        init();
        loop();

        glfwDestroyWindow(window);
        glfwTerminate();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        window = glfwCreateWindow(width, height, "Tornado Simulation", NULL, NULL);
        if (window == NULL) throw new RuntimeException("Failed to create the GLFW window");

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1); // Enable v-sync
        glfwShowWindow(window);

        GL.createCapabilities();
        glEnable(GL_POINT_SMOOTH);
        glPointSize(2f);
        glEnable(GL_DEPTH_TEST);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float aspect = (float) width / height;
        gluPerspective(45.0f, aspect, 0.1f, 100.0f);
        glMatrixMode(GL_MODELVIEW);

        initParticles();
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            updateParticles();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glLoadIdentity();
            glTranslatef(0.0f, -10.0f, -40.0f);
            glRotatef(20.0f, 1f, 0f, 0f);

            glBegin(GL_POINTS);
            for (int i = 0; i < numParticles; i++) {
                float y = particles[i][1];
                float r = 1.0f;
                float g = 1.0f - y / 30f;
                float b = 0.8f;
                glColor3f(r, g, b);
                glVertex3f(particles[i][0], y, particles[i][2]);
            }
            glEnd();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void updateParticles() {
        for (int i = 0; i < numParticles; i++) {
            angles[i] += 0.5f + (particles[i][1] / 30f) * 2f;
            particles[i][1] += 0.2f;
            if (particles[i][1] > 30f) {
                angles[i] = rand.nextFloat() * 360f;
                particles[i][1] = 0f;
            }
            float radius = (particles[i][1] / 30f) * 5f;
            particles[i][0] = radius * (float) Math.cos(Math.toRadians(angles[i]));
            particles[i][2] = radius * (float) Math.sin(Math.toRadians(angles[i]));
        }
    }

    // gluPerspective equivalent
    private void gluPerspective(float fovY, float aspect, float zNear, float zFar) {
        float fH = (float) Math.tan(Math.toRadians(fovY / 2)) * zNear;
        float fW = fH * aspect;
        glFrustum(-fW, fW, -fH, fH, zNear, zFar);
    }

    public static void main(String[] args) {
        new TornadoSimulation().run();
    }
}
