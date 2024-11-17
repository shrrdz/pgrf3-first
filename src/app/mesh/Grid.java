package app.mesh;

import lwjglutils.OGLBuffers;

public class Grid extends Mesh
{
    public Grid(int m, int n, boolean TRIANGLE_STRIP)
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

        if (TRIANGLE_STRIP)
        {
            // resize index buffer to include degenerate triangles
            indexBuffer = new int[(m - 1) * (2 * n + 2) - 2];

            for (int i = 0; i < m - 1; i++)
            {
                int offset = i * n;
                int otherOffset = (i + 1) * n;

                // generate alternating indices
                // for the current row and the next row
                for (int j = 0; j < n; j++)
                {
                    indexBuffer[index++] = j + offset;
                    indexBuffer[index++] = j + otherOffset;
                }

                // add degenerate triangles (except on the last strip)
                if (i < m - 2)
                {
                    indexBuffer[index++] = (n - 1) + otherOffset;
                    indexBuffer[index++] = (i + 1) * n;
                }
            }
        }
        else // GL_TRIANGLES
        {
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
        }

        attributes = new OGLBuffers.Attrib[]
        {
            new OGLBuffers.Attrib("in_position", 2),
        };

        buffers = new OGLBuffers(vertexBuffer, attributes, indexBuffer);
    }
}
