package app.mesh;

import lwjglutils.OGLBuffers;

public class Grid extends Mesh
{
    public Grid(int m, int n)
    {
        vertexBuffer = new float[2 * m * n];
        indexBuffer = new int[3 * 2 * (m - 1) * (n - 1)];

        int index = 0;

        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                vertexBuffer[index++] = j / (float) (n - 1);
                vertexBuffer[index++] = i / (float) (m - 1);
            }
        }

        index = 0;

        for (int i = 0; i < m - 1; i++)
        {
            int offset = i * n;

            for (int j = 0; j < n - 1; j++)
            {
                indexBuffer[index++] = j + offset;
                indexBuffer[index++] = j + n + offset;
                indexBuffer[index++] = j + 1 + offset;

                indexBuffer[index++] = j + 1 + offset;
                indexBuffer[index++] = j + n + offset;
                indexBuffer[index++] = j + n + 1 + offset;
            }
        }

        attributes = new OGLBuffers.Attrib[]
        {
            new OGLBuffers.Attrib("in_position", 2),
        };

        buffers = new OGLBuffers(vertexBuffer, attributes, indexBuffer);
    }
}
