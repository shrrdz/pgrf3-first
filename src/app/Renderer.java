package app;

import app.mesh.Axis;
import app.mesh.Mesh;
import app.mesh.Triangle;
import lwjglutils.ShaderUtils;
import org.lwjgl.glfw.*;
import transforms.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class Renderer extends AbstractRenderer
{
    private Mesh axis, triangle;

    private int shaderAxis, shaderTriangle;

    private int alternativeColor;

    private Camera cam;
    private Mat4PerspRH projection;

    public void init()
    {
        glClearColor(0.1F, 0.1F, 0.1F, 1.0F);

        axis = new Axis();
        triangle = new Triangle();

        shaderAxis = ShaderUtils.loadProgram("/axis");
        shaderTriangle = ShaderUtils.loadProgram("/triangle");

        cam = new Camera().withPosition(new Vec3D(0.5, -1.5, 1))
                .withAzimuth(Math.toRadians(90))
                .withZenith(Math.toRadians(-15))
                .withFirstPerson(true);

        projection = new Mat4PerspRH(Math.toRadians(75), (float) height / width, 0.1, 1000);
    }

    public void display()
    {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glUseProgram(shaderAxis);

        setMatrixUniforms(shaderAxis);

        axis.getBuffers().draw(GL_LINES, shaderAxis);

        /*
        glUseProgram(shaderTriangle);

        setMatrixUniforms(shaderTriangle);
        glUniform1i(glGetUniformLocation(shaderTriangle, "alt_color"), alternativeColor);

        triangle.getBuffers().draw(GL_TRIANGLES, shaderTriangle);
        */
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