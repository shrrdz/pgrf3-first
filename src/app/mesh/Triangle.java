package app.mesh;

import lwjglutils.OGLBuffers;

public class Triangle extends Mesh
{
    public Triangle()
    {
        float[] vertexBuffer =
        {
            0, 1, 0,     1, 0, 0,
            -1, 0, 0,    0, 1, 0,
            1, -1, 0,    0, 0, 1,
        };

        int[] indexBuffer = { 0, 1, 2, };

        OGLBuffers.Attrib[] attributes =
        {
            new OGLBuffers.Attrib("in_position", 3),
            new OGLBuffers.Attrib("in_color", 3),
        };

        buffers = new OGLBuffers(vertexBuffer, attributes, indexBuffer);
    }
}
