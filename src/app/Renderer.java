package app;

import app.mesh.Axis;
import app.mesh.Grid;
import app.mesh.Mesh;
import lwjglutils.OGLTexture2D;
import lwjglutils.ShaderUtils;
import org.lwjgl.glfw.*;
import transforms.*;

import java.io.IOException;
import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class Renderer extends AbstractRenderer
{
    private Mesh axis, plane, sphere, torus;

    private int shaderAxis, shaderPlane, shaderSphere, shaderTorus;

    private Camera cam;

    private Mat4 projection;

    private Mat4PerspRH perspective;
    private Mat4OrthoRH orthogonal;

    private OGLTexture2D checker, bricks;

    private boolean initial = true;

    private final double[] previousX = new double[1];
    private final double[] previousY = new double[1];

    private final double observerSpeed = 4;

    private double deltaTick;

    private final int[] display = new int[3];

    public void init()
    {
        glEnable(GL_DEPTH_TEST);

        glClearColor(0.1F, 0.1F, 0.1F, 1.0F);

        axis = new Axis();
        plane = new Grid(2, 2);
        sphere = new Grid(20, 20);
        torus = new Grid(20, 20);

        plane.scale(8, 8, 1);

        plane.translate(0, 0, -1.5);
        sphere.translate(2, 2, 0);
        torus.translate(-2, 2, 0);

        shaderAxis = ShaderUtils.loadProgram("/axis");
        shaderPlane = ShaderUtils.loadProgram("/plane", "/universal");
        shaderSphere = ShaderUtils.loadProgram("/sphere", "/universal");
        shaderTorus = ShaderUtils.loadProgram("/torus", "/universal");

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
        }
        catch (IOException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    public void display()
    {
        deltaTick = LwjglWindow.deltaTick();

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // axis
        glUseProgram(shaderAxis);

        setUniversalUniforms(shaderAxis, axis);

        axis.getBuffers().draw(GL_LINES, shaderAxis);

        // plane
        glUseProgram(shaderPlane);

        setUniversalUniforms(shaderPlane, plane);

        checker.bind();

        plane.getBuffers().draw(GL_TRIANGLES, shaderPlane);

        // sphere
        glUseProgram(shaderSphere);

        setUniversalUniforms(shaderSphere, sphere);

        bricks.bind();

        sphere.getBuffers().draw(GL_TRIANGLES, shaderSphere);

        // torus
        glUseProgram(shaderTorus);

        setUniversalUniforms(shaderTorus, torus);

        bricks.bind();

        torus.getBuffers().draw(GL_TRIANGLES, shaderTorus);
    }

    private void setUniversalUniforms(int shader, Mesh mesh)
    {
        glUniformMatrix4fv(glGetUniformLocation(shader, "model"), false, mesh.getModel().floatArray());
        glUniformMatrix4fv(glGetUniformLocation(shader, "view"), false, cam.getViewMatrix().floatArray());
        glUniformMatrix4fv(glGetUniformLocation(shader, "projection"), false, projection.floatArray());

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

            // default display
            if (glfwGetKey(window, GLFW_KEY_R) == GLFW_PRESS)
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