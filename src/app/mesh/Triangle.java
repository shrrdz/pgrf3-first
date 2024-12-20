package app.mesh;

import lwjglutils.OGLBuffers;

public class Triangle extends Mesh
{
    public Triangle()
    {
        vertexBuffer = new float[]
        {
            0, 1, 0,     1, 0, 0,
            -1, 0, 0,    0, 1, 0,
            1, -1, 0,    0, 0, 1,
        };

        indexBuffer = new int[] { 0, 1, 2, };

        attributes = new OGLBuffers.Attrib[]
        {
            new OGLBuffers.Attrib("in_position", 3),
            new OGLBuffers.Attrib("in_color", 3),
        };

        buffers = new OGLBuffers(vertexBuffer, attributes, indexBuffer);
    }
}
