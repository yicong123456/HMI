package org.rajawali3d.examples.DreamView.ui;

import android.content.Context;
import androidx.annotation.Nullable;
import org.rajawali3d.examples.DreamView.AExampleFragment;
import org.rajawali3d.examples.DreamView.materials.materials.CustomMaterialPlugin;
import org.rajawali3d.materials.Material;
import org.rajawali3d.primitives.ScreenQuad;

public class TwoDimensionalFragment extends AExampleFragment {

	@Override
    public AExampleRenderer createRenderer() {
		return new TwoDimensionalRenderer(getActivity(), this);
	}

	private final class TwoDimensionalRenderer extends AExampleRenderer {

		private float mTime;
		private Material mCustomMaterial;

		public TwoDimensionalRenderer(Context context, @Nullable AExampleFragment fragment) {
			super(context, fragment);
		}

        @Override
		protected void initScene() {
			mCustomMaterial = new Material();
			mCustomMaterial.enableTime(true);
			mCustomMaterial.addPlugin(new CustomMaterialPlugin());

			ScreenQuad screenQuad = new ScreenQuad();
			screenQuad.setMaterial(mCustomMaterial);
			getCurrentScene().addChild(screenQuad);
		}

        @Override
        protected void onRender(long elapsedRealtime, double deltaTime) {
            super.onRender(elapsedRealtime, deltaTime);
			mTime += .007f;
			mCustomMaterial.setTime(mTime);
		}

	}

}
