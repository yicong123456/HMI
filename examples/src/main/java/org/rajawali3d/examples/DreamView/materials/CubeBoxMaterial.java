package org.rajawali3d.examples.DreamView.materials;

import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.shaders.FragmentShader;
import org.rajawali3d.materials.shaders.VertexShader;

public class CubeBoxMaterial extends Material {
    public CubeBoxMaterial() {
        super(new VertexShader(), new FragmentShader());
    }

    @Override
    public void useVertexColors(boolean value) {
        super.useVertexColors(false); // 禁用顶点颜色
    }
}
