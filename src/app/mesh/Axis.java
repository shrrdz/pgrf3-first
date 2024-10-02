package app.mesh;

import lwjglutils.OGLBuffers;

public class Axis extends Mesh
{
    public Axis()
    {
        vertexBuffer = new float[]
        {
            0, 0, 0,     1, 0, 0,
            1, 0, 0,     1, 0, 0,

            0, 0, 0,     0, 1, 0,
            0, 1, 0,     0, 1, 0,

            0, 0, 0,     0, 0, 1,
            0, 0, 1,     0, 0, 1,
        };

        indexBuffer = new int[] { 0, 1, 2, 3, 4, 5 };

        attributes = new OGLBuffers.Attrib[]
        {
            new OGLBuffers.Attrib("in_position", 3),
            new OGLBuffers.Attrib("in_color", 3),
        };

        buffers = new OGLBuffers(vertexBuffer, attributes, indexBuffer);
    }
}
