package app;

import app.mesh.Axis;
import app.mesh.Grid;
import app.mesh.Mesh;
import app.mesh.Triangle;
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
    private Mesh axis, triangle, grid;

    private int shaderAxis, shaderTriangle, shaderGrid;

//  private int alternativeColor;

    private Camera cam;
    private Mat4PerspRH projection;

    private float theta = 0;

    private OGLTexture2D bricks;

    public void init()
    {
        glEnable(GL_DEPTH_TEST);

        glClearColor(0.1F, 0.1F, 0.1F, 1.0F);

        axis = new Axis();
        triangle = new Triangle();
        grid = new Grid(20, 20);

        shaderAxis = ShaderUtils.loadProgram("/axis");
        shaderTriangle = ShaderUtils.loadProgram("/triangle");
        shaderGrid = ShaderUtils.loadProgram("/grid");

        cam = new Camera().withPosition(new Vec3D(0, -1.5, 1.5))
                .withAzimuth(Math.toRadians(90))
                .withZenith(Math.toRadians(-45))
                .withFirstPerson(true);

        projection = new Mat4PerspRH(Math.toRadians(75), (float) height / width, 0.1, 1000);

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

        // triangle
        glUseProgram(shaderTriangle);

        setMatrixUniforms(shaderTriangle);
        glUniform1i(glGetUniformLocation(shaderTriangle, "alt_color"), alternativeColor);

        triangle.getBuffers().draw(GL_TRIANGLES, shaderTriangle);
        */

        // grid
        glUseProgram(shaderGrid);

        setMatrixUniforms(shaderGrid);
        glUniform1f(glGetUniformLocation(shaderGrid, "theta"), theta);

        bricks.bind();

        grid.getBuffers().draw(GL_TRIANGLES, shaderGrid);

        theta += 0.01F;
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
            /*
            if (key == GLFW_KEY_SPACE && action == GLFW_PRESS)
            {
                alternativeColor = alternativeColor == 0 ? 1 : 0;
            }
            */
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
	
    private GLFWCursorPosCallback cpCallbacknew = new GLFWCursorPosCallback()
    {
        @Override
        public void invoke(long window, double x, double y)
        {

        }
    };
    
    private GLFWScrollCallback scrollCallback = new GLFWScrollCallback()
    {
        @Override public void invoke (long window, double dx, double dy)
        {

        }
    };

    public GLFWKeyCallback getKeyCallback()
    {
        return keyCallback;
    }
}