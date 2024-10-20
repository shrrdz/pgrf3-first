package app;

import app.mesh.Axis;
import app.mesh.Grid;
import app.mesh.Mesh;
import lwjglutils.OGLTexture2D;
import lwjglutils.ShaderUtils;
import org.lwjgl.glfw.*;
import transforms.*;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class Renderer extends AbstractRenderer
{
    private Mesh axis, grid;

    private int shaderAxis, shaderGrid;

    private Camera cam;

    private Mat4 projection;

    private Mat4PerspRH perspective;
    private Mat4OrthoRH orthogonal;

    private OGLTexture2D bricks;

    private boolean initial = true;

    private final double[] previous_x = new double[1];
    private final double[] previous_y = new double[1];

    public void init()
    {
        glEnable(GL_DEPTH_TEST);

        glClearColor(0.1F, 0.1F, 0.1F, 1.0F);

        axis = new Axis();
        grid = new Grid(20, 20);

        shaderAxis = ShaderUtils.loadProgram("/axis");
        shaderGrid = ShaderUtils.loadProgram("/grid");

        cam = new Camera().withPosition(new Vec3D(0, -4, 4))
                .withAzimuth(Math.toRadians(90))
                .withZenith(Math.toRadians(-45))
                .withFirstPerson(true);

        perspective = new Mat4PerspRH(Math.toRadians(75), (float) height / width, 0.1, 1000);
        orthogonal = new Mat4OrthoRH(10, 10, 0.1, 1000);

        projection = perspective;

        try
        {
            bricks = new OGLTexture2D("bricks.png");
        }
        catch (IOException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    public void display()
    {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        /*
        // axis
        glUseProgram(shaderAxis);

        setMatrixUniforms(shaderAxis);

        axis.getBuffers().draw(GL_LINES, shaderAxis);
        */

        // grid
        glUseProgram(shaderGrid);

        setMatrixUniforms(shaderGrid);

        bricks.bind();

        grid.getBuffers().draw(GL_TRIANGLES, shaderGrid);
    }

    private void setMatrixUniforms(int shader)
    {
        glUniformMatrix4fv(glGetUniformLocation(shader, "view"), false, cam.getViewMatrix().floatArray());
        glUniformMatrix4fv(glGetUniformLocation(shader, "projection"), false, projection.floatArray());
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
                cam = cam.forward(0.1);
            }

            if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)
            {
                cam = cam.backward(0.1);
            }

            if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS)
            {
                cam = cam.left(0.1);
            }

            if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS)
            {
                cam = cam.right(0.1);
            }

            if (glfwGetKey(window, GLFW_KEY_Q) == GLFW_PRESS)
            {
                cam = cam.down(0.1);
            }

            if (glfwGetKey(window, GLFW_KEY_E) == GLFW_PRESS)
            {
                cam = cam.up(0.1);
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

    private GLFWCursorPosCallback cursorPosCallback = new GLFWCursorPosCallback()
    {
        @Override
        public void invoke(long window, double x, double y)
        {
            if (initial)
            {
                glfwGetCursorPos(window, previous_x, previous_y);

                initial = false;
            }

            int dx = (int) (x - previous_x[0]);
            int dy = (int) (y - previous_y[0]);

            double moveD = Math.toRadians(-0.05);

            cam = cam.addAzimuth(moveD * dx);
            cam = cam.addZenith(moveD * dy);

            previous_x[0] = (int) x;
            previous_y[0] = (int) y;
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