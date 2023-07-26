package org.rajawali3d.examples.shader;

import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.plugins.AlphaMaterialPlugin;
import org.rajawali3d.materials.shaders.VertexShader;

public class CubeMaterialPlugin extends Material {
    private static final String VERTEX_SHADER =
        "precision mediump float;\n" +
            "attribute vec4 a_Position;\n" +
            "uniform mat4 u_MVPMatrix;\n" +
            "void main() {\n" +
            "    gl_Position = u_MVPMatrix * a_Position;\n" +
            "}";

    private static final String FRAGMENT_SHADER =
            "precision mediump float;\n" +
                    "uniform vec4 u_Color;\n" +
                    "void main() {\n" +
                    "    gl_FragColor = u_Color;\n" +
                    "}";

    private float[] mColor = {1.0f, 1.0f, 1.0f, 1.0f}; // 默认颜色为紫色

    public CubeMaterialPlugin() {
        // super(VERTEX_SHADER, FRAGMENT_SHADER, null);
        super();
    }

    public void setColor(float r, float g, float b, float a) {
        mColor[0] = r;
        mColor[1] = g;
        mColor[2] = b;
        mColor[3] = a;
    }

    @Override
    public void useProgram() {
        super.useProgram();
        // int colorHandle = this.mProgramHandle.getUniformLocation("u_Color");
        // this.mProgramHandle.setUniform4fv(colorHandle, mColor, 1);
    }

}
