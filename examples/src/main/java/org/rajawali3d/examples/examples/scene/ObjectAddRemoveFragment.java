package org.rajawali3d.examples.examples.scene;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import org.rajawali3d.Object3D;
import org.rajawali3d.animation.Animation;
import org.rajawali3d.animation.RotateOnAxisAnimation;
import org.rajawali3d.examples.R;
import org.rajawali3d.examples.examples.AExampleFragment;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Cube;
import org.rajawali3d.renderer.ISurfaceRenderer;

import java.util.Random;

/**
 * @author Jared Woolston (jwoolston@keywcorp.com)
 */
public class ObjectAddRemoveFragment extends AExampleFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            ((Button) view.findViewById(R.id.add_cube)).setOnClickListener(new OnClickListener() {
                @Override public void onClick(View v) {
                    ((ObjectAddRemoveRenderer) mRenderer).addNewCube();
                }
            });
            ((Button) view.findViewById(R.id.remove_cube)).setOnClickListener(new OnClickListener() {
                @Override public void onClick(View v) {
                    ((ObjectAddRemoveRenderer) mRenderer).removeRandomCube();
                }
            });
        }
        return view;
    }

    @Override
    public ISurfaceRenderer createRenderer() {
        return new ObjectAddRemoveRenderer(getActivity(), this);
    }

    @Override
    public int getLayoutID() {
        return R.layout.rajawali_object_add_remove_fragment;
    }

    public static final class ObjectAddRemoveRenderer extends AExampleRenderer {

        private final Random random = new Random();
        private Material mCubeMaterial;

        public ObjectAddRemoveRenderer(Context context, @Nullable AExampleFragment fragment) {
            super(context, fragment);
        }

        @Override
        protected void initScene() {
            DirectionalLight light = new DirectionalLight();
            light.setPower(1);
            light.setLookAt(0, 0, -1);
            light.enableLookAt();
            getCurrentScene().setBackgroundColor(Color.BLACK);
            getCurrentScene().addLight(light);

            //
            // -- Create a material for all cubes
            //

            mCubeMaterial = new Material();
            mCubeMaterial.enableLighting(true);
            mCubeMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());

            getCurrentCamera().setZ(10);

            addNewCube();
        }

        public void addNewCube() {
            Cube cube = new Cube(1);
            cube.setPosition(-5 + random.nextFloat() * 10, -5 + random.nextFloat() * 10, random.nextFloat() * -10);
            cube.setMaterial(mCubeMaterial);
            cube.setColor(0x666666 + random.nextInt(0x999999));
            getCurrentScene().addChild(cube);

            Vector3 randomAxis = new Vector3(random.nextFloat(), random.nextFloat(), random.nextFloat());
            randomAxis.normalize();

            RotateOnAxisAnimation anim = new RotateOnAxisAnimation(randomAxis, 360);
            anim.setTransformable3D(cube);
            anim.setDurationMilliseconds(3000 + (int) (random.nextDouble() * 5000));
            anim.setRepeatMode(Animation.RepeatMode.INFINITE);
            getCurrentScene().registerAnimation(anim);
            anim.play();
        }

        public void removeRandomCube() {
            final int count = getCurrentScene().getNumChildren();
            if (count > 0) {
                final int index = random.nextInt(count);
                final Object3D child = getCurrentScene().getChildrenCopy().get(index);
                getCurrentScene().removeChild(child);
                child.destroy();
            }
        }
    }
}
