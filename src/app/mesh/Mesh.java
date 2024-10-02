package app.mesh;

import lwjglutils.OGLBuffers;

public abstract class Mesh
{
    protected float[] vertexBuffer;
    protected int[] indexBuffer;

    protected OGLBuffers.Attrib[] attributes;

    protected OGLBuffers buffers;

    public OGLBuffers getBuffers()
    {
        return buffers;
    }
}
