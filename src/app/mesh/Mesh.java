package app.mesh;

import lwjglutils.OGLBuffers;
import transforms.*;

public abstract class Mesh
{
    protected float[] vertexBuffer;
    protected int[] indexBuffer;

    protected OGLBuffers.Attrib[] attributes;

    protected OGLBuffers buffers;

    protected Mat4 model = new Mat4Identity();

    public OGLBuffers getBuffers()
    {
        return buffers;
    }

    public Mat4 getModel()
    {
        return model;
    }

    public void translate(double x, double y, double z)
    {
        this.model = model.mul(new Mat4Transl(x, y, z));
    }

    public void rotate(double x, double y, double z)
    {
        this.model = model.mul(new Mat4RotXYZ(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z)));
    }

    public void scale(double x, double y, double z)
    {
        this.model = model.mul(new Mat4Scale(x, y, z));
    }
}
