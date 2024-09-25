package app.mesh;

import lwjglutils.OGLBuffers;

public abstract class Mesh
{
    protected OGLBuffers buffers;

    public OGLBuffers getBuffers()
    {
        return buffers;
    }
}
