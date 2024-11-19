package app;

import app.mesh.Axis;
import app.mesh.Grid;
import app.mesh.Mesh;
import lwjglutils.OGLRenderTarget;
import lwjglutils.OGLTexture2D;
import lwjglutils.ShaderUtils;
import org.lwjgl.glfw.*;
import transforms.*;

import java.io.IOException;
import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Renderer extends AbstractRenderer
{
    private Mesh axis, plane, sphere, torus, wave;

    private int shaderAxis, shaderUniversal;

    private Camera cam;

    private Mat4 projection;

    private Mat4PerspRH perspective;
    private Mat4OrthoRH orthogonal;

    private OGLTexture2D checker, bricks, wood, sand;

    private boolean initial = true;

    private final double[] previousX = new double[1];
    private final double[] previousY = new double[1];

    private final double observerSpeed = 4;

    private double deltaTick;

    private int polygonMode;

    private final int[] display = new int[3];

    private Mat4 lightView, lightProjection;

    private OGLRenderTarget renderTarget;
    private OGLTexture2D.Viewer viewer;

    private float theta;

    public void init()
    {
        glEnable(GL_DEPTH_TEST);

        glClearColor(0.1F, 0.1F, 0.1F, 1.0F);

        axis = new Axis();
        plane = new Grid(2, 2, true);
        sphere = new Grid(20, 20, true);
        torus = new Grid(20, 20, true);
        wave = new Grid(20, 20, true);

        plane.scale(16, 16, 1);
        wave.scale(2, 2, 2);

        plane.translate(0, 0, -1.5);
        sphere.translate(4, 2, 0);
        torus.translate(-4, 2, 0);
        wave.translate(0, 6, 1);

        shaderAxis = ShaderUtils.loadProgram("/axis");
        shaderUniversal = ShaderUtils.loadProgram("/universal");

        cam = new Camera().withPosition(new Vec3D(0, -2, 2))
                .withAzimuth(Math.toRadians(90))
                .withZenith(Math.toRadians(-30))
                .withFirstPerson(true);

        perspective = new Mat4PerspRH(Math.toRadians(75), (float) height / width, 0.1, 1000);
        orthogonal = new Mat4OrthoRH(10, 10, 0.1, 1000);

        projection = perspective;

        try
        {
            checker = new OGLTexture2D("checker.png");
            bricks = new OGLTexture2D("bricks.png");
            wood = new OGLTexture2D("oak.png");
            sand = new OGLTexture2D("sand.png");
        }
        catch (IOException exception)
        {
            throw new RuntimeException(exception);
        }

        renderTarget = new OGLRenderTarget(width, height);

        lightView = new Mat4ViewRH(new Vec3D(2, -2, 4), new Vec3D(-1, 1, -1), new Vec3D(0, 0, 1));
        lightProjection = new Mat4OrthoRH(20, 20, 0.1, 100);

        viewer = new OGLTexture2D.Viewer();
    }

    public void display()
    {
        deltaTick = LwjglWindow.deltaTick();

        theta += 4 * deltaTick;

        switch (polygonMode)
        {
            case 0: glPolygonMode(GL_FRONT_AND_BACK, GL_FILL); break;
            case 1: glPolygonMode(GL_FRONT_AND_BACK, GL_LINE); break;
            case 2: glPolygonMode(GL_FRONT_AND_BACK, GL_POINT); break;
        }

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        renderTarget.bind();

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        render(true);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        render(false);

        viewer.view(renderTarget.getDepthTexture(), 0.6, -1, 0.4);
    }

    private void render(boolean lightDrawn)
    {
        if (!lightDrawn)
        {
            // axis
            glUseProgram(shaderAxis);

            setUniversalUniforms(shaderAxis, axis, false);

            axis.getBuffers().draw(GL_LINES, shaderAxis);
        }

        glUseProgram(shaderUniversal);

        // plane
        setUniversalUniforms(shaderUniversal, plane, lightDrawn);
        glUniform1i(glGetUniformLocation(shaderUniversal, "receive_shadows"), 1);
        glUniform1i(glGetUniformLocation(shaderUniversal, "plane"), 1);

        checker.bind(shaderUniversal, "bitmap", 0);

        plane.getBuffers().draw(GL_TRIANGLE_STRIP, shaderUniversal);

        // sphere
        setUniversalUniforms(shaderUniversal, sphere, lightDrawn);
        glUniform1i(glGetUniformLocation(shaderUniversal, "receive_shadows"), 0);
        glUniform1i(glGetUniformLocation(shaderUniversal, "plane"), 0);
        glUniform1i(glGetUniformLocation(shaderUniversal, "sphere"), 1);

        if (!lightDrawn)
        {
            renderTarget.getDepthTexture().bind(shaderUniversal, "shadowmap", 1);
            glUniformMatrix4fv(glGetUniformLocation(shaderUniversal, "light_view_projection"), false, (lightView.mul(lightProjection)).floatArray());
        }

        bricks.bind(shaderUniversal, "bitmap", 0);

        sphere.getBuffers().draw(GL_TRIANGLE_STRIP, shaderUniversal);

        // torus
        setUniversalUniforms(shaderUniversal, torus, lightDrawn);
        glUniform1i(glGetUniformLocation(shaderUniversal, "receive_shadows"), 0);
        glUniform1i(glGetUniformLocation(shaderUniversal, "sphere"), 0);
        glUniform1i(glGetUniformLocation(shaderUniversal, "torus"), 1);

        if (!lightDrawn)
        {
            renderTarget.getDepthTexture().bind(shaderUniversal, "shadowmap", 1);
            glUniformMatrix4fv(glGetUniformLocation(shaderUniversal, "light_view_projection"), false, (lightView.mul(lightProjection)).floatArray());
        }

        wood.bind(shaderUniversal, "bitmap", 0);

        torus.getBuffers().draw(GL_TRIANGLE_STRIP, shaderUniversal);

        // wave
        setUniversalUniforms(shaderUniversal, wave, lightDrawn);
        glUniform1i(glGetUniformLocation(shaderUniversal, "receive_shadows"), 0);
        glUniform1i(glGetUniformLocation(shaderUniversal, "torus"), 0);
        glUniform1i(glGetUniformLocation(shaderUniversal, "wave"), 1);

        glUniform1f(glGetUniformLocation(shaderUniversal, "theta"), theta);

        if (!lightDrawn)
        {
            renderTarget.getDepthTexture().bind(shaderUniversal, "shadowmap", 1);
            glUniformMatrix4fv(glGetUniformLocation(shaderUniversal, "light_view_projection"), false, (lightView.mul(lightProjection)).floatArray());
        }

        sand.bind(shaderUniversal, "bitmap", 0);

        wave.getBuffers().draw(GL_TRIANGLE_STRIP, shaderUniversal);
    }

    private void setUniversalUniforms(int shader, Mesh mesh, boolean lightDrawn)
    {
        glUniformMatrix4fv(glGetUniformLocation(shader, "model"), false, mesh.getModel().floatArray());
        glUniformMatrix4fv(glGetUniformLocation(shader, "view"), false, lightDrawn ? lightView.floatArray() : cam.getViewMatrix().floatArray());
        glUniformMatrix4fv(glGetUniformLocation(shader, "projection"), false, lightDrawn ? lightProjection.floatArray() : projection.floatArray());

        glUniform1iv(glGetUniformLocation(shader, "display"), display);
    }

	private GLFWKeyCallback keyCallback = new GLFWKeyCallback()
    {
		@Override
		public void invoke(long window, int key, int scancode, int action, int mods)
        {
            if (key == GLFW_KEY_P && action == GLFW_PRESS)
            {
                projection = (projection == perspective) ? orthogonal : perspective;
            }

            if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)
            {
                cam = cam.forward(observerSpeed * deltaTick);
            }

            if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)
            {
                cam = cam.backward(observerSpeed * deltaTick);
            }

            if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS)
            {
                cam = cam.left(observerSpeed * deltaTick);
            }

            if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS)
            {
                cam = cam.right(observerSpeed * deltaTick);
            }

            if (glfwGetKey(window, GLFW_KEY_Q) == GLFW_PRESS)
            {
                cam = cam.down(observerSpeed * deltaTick);
            }

            if (glfwGetKey(window, GLFW_KEY_E) == GLFW_PRESS)
            {
                cam = cam.up(observerSpeed * deltaTick);
            }
            
            if (key == GLFW_KEY_R && action == GLFW_PRESS)
            {
                polygonMode = polygonMode < 2 ? polygonMode + 1 : 0;
            }

            // default display
            if (glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS)
            {
                Arrays.fill(display, 0);
            }

            // no-texture lighting display
            if (glfwGetKey(window, GLFW_KEY_T) == GLFW_PRESS)
            {
                Arrays.fill(display, 0);

                display[0] = 1;
            }

            // depth buffer display
            if (glfwGetKey(window, GLFW_KEY_B) == GLFW_PRESS)
            {
                Arrays.fill(display, 0);

                display[1] = 1;
            }

            // normal display
            if (glfwGetKey(window, GLFW_KEY_N) == GLFW_PRESS)
            {
                Arrays.fill(display, 0);

                display[2] = 1;
            }

            if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
            {
                glfwSetWindowShouldClose(window, true);
            }
        }
    };
    
    private GLFWWindowSizeCallback wsCallback = new GLFWWindowSizeCallback()
    {
        @Override
        public void invoke(long window, int w, int h)
        {

        }
    };
    
    private GLFWMouseButtonCallback mbCallback = new GLFWMouseButtonCallback ()
    {
        @Override
        public void invoke(long window, int button, int action, int mods)
        {

        }
    };

    private final GLFWCursorPosCallback cursorPosCallback = new GLFWCursorPosCallback()
    {
        @Override
        public void invoke(long window, double x, double y)
        {
            if (initial)
            {
                glfwGetCursorPos(window, previousX, previousY);

                initial = false;
            }

            int dx = (int) (x - previousX[0]);
            int dy = (int) (y - previousY[0]);

            double moveD = Math.toRadians(-0.05);

            cam = cam.addAzimuth(moveD * dx);
            cam = cam.addZenith(moveD * dy);

            previousX[0] = (int) x;
            previousY[0] = (int) y;
        }
    };
    
    private GLFWScrollCallback scrollCallback = new GLFWScrollCallback()
    {
        @Override
        public void invoke (long window, double dx, double dy)
        {

        }
    };

    public GLFWKeyCallback getKeyCallback()
    {
        return keyCallback;
    }

    public GLFWCursorPosCallback getCursorCallback()
    {
        return new GLFWCursorPosCallback()
        {
            @Override
            public void invoke(long window, double xpos, double ypos)
            {
                cursorPosCallback.invoke(window, xpos, ypos);
            }
        };
    }
}