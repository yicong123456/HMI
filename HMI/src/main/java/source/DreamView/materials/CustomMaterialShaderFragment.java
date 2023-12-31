package org.rajawali3d.examples.DreamView.materials;

import android.content.Context;
import androidx.annotation.Nullable;
import org.rajawali3d.Object3D;
import org.rajawali3d.examples.DreamView.AExampleFragment;
import org.rajawali3d.examples.DreamView.materials.materials.CustomMaterialPlugin;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.primitives.Cube;

public class CustomMaterialShaderFragment extends AExampleFragment {
	@Override
    public AExampleRenderer createRenderer() {
		return new CustomShaderRenderer(getActivity(), this);
	}

	private final class CustomShaderRenderer extends AExampleRenderer {
		private DirectionalLight mLight;
		private Object3D mCube;
		private float mTime;
		private Material mMaterial;

		CustomShaderRenderer(Context context, @Nullable AExampleFragment fragment) {
			super(context, fragment);
		}

        @Override
		protected void initScene() {
			mLight = new DirectionalLight(0, 1, 1);

			getCurrentScene().addLight(mLight);
			mMaterial = new Material();
			mMaterial.enableTime(true);
			mMaterial.addPlugin(new CustomMaterialPlugin());

			mCube = new Cube(2);
			mCube.setMaterial(mMaterial);
			getCurrentScene().addChild(mCube);

			getCurrentCamera().setPosition(0, 0, 10);

			mTime = 0;
		}

        @Override
        protected void onRender(long elapsedRealtime, double deltaTime) {
            super.onRender(elapsedRealtime, deltaTime);
			mTime += .007f;
			mMaterial.setTime(mTime);
			mCube.setRotX(mCube.getRotX() + .5f);
			mCube.setRotY(mCube.getRotY() + .5f);
		}
	}
}
