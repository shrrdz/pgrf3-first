package app;

import app.mesh.Mesh;
import app.mesh.Triangle;
import lwjglutils.ShaderUtils;
import org.lwjgl.glfw.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class Renderer extends AbstractRenderer
{
    private Mesh triangle;

    private int shaderTriangle;

    private int alternativeColor;

    public void init()
    {
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        triangle = new Triangle();

        shaderTriangle = ShaderUtils.loadProgram("/triangle");
    }

    public void display()
    {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glUseProgram(shaderTriangle);

        glUniform1i(glGetUniformLocation(shaderTriangle, "alt_color"), alternativeColor);

        triangle.getBuffers().draw(GL_TRIANGLES, shaderTriangle);
    }

	private GLFWKeyCallback keyCallback = new GLFWKeyCallback()
    {
		@Override
		public void invoke(long window, int key, int scancode, int action, int mods)
        {
            if (key == GLFW_KEY_SPACE && action == GLFW_PRESS)
            {
                alternativeColor = alternativeColor == 0 ? 1 : 0;
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