package app;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;

import static org.lwjgl.opengl.GL11.*;

public class Renderer extends AbstractRenderer
{
    public void init()
    {
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
    }

    public void display()
    {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

	private GLFWKeyCallback keyCallback = new GLFWKeyCallback()
    {
		@Override
		public void invoke(long window, int key, int scancode, int action, int mods)
        {

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