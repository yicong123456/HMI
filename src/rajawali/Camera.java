package rajawali;

import rajawali.math.MathUtil;
import android.opengl.Matrix;

public class Camera extends ATransformable3D {
	protected float[] mVMatrix = new float[16];
	protected float[] mRotationMatrix = new float[16];
	protected float[] mProjMatrix = new float[16];
	protected float mNearPlane = 1.0f;
	protected float mFarPlane = 120.0f;
	protected float mFieldOfView = 45;
	protected boolean mUseRotationMatrix = false;
	protected float[] mRotateMatrixTmp = new float[16];
	protected float[] mTmpMatrix = new float[16];

	public Camera() {
		super();
		mIsCamera = true;
	}

	public float[] getViewMatrix() {
		if (mLookAt != null) {
			Matrix.setLookAtM(mVMatrix, 0, -mPosition.x, mPosition.y,
					mPosition.z, -mLookAt.x, mLookAt.y, mLookAt.z, 0f, 1.0f,
					0.0f);
		} else {
			if (mUseRotationMatrix == false && mRotationDirty) {
//				Matrix.setIdentityM(mRotationMatrix, 0);
//				rotateM(mRotationMatrix, 0, -mRotation.x, 1.0f, 0.0f, 0.0f);
//				rotateM(mRotationMatrix, 0, -mRotation.y + 180, 0.0f, 1.0f, 0.0f);
//				rotateM(mRotationMatrix, 0, -mRotation.z, 0.0f, 0.0f, 1.0f);
				setOrientation();
				//mOrientation.inverse();
				mOrientation.toRotationMatrix(mRotationMatrix);
				mRotationDirty = false;
			}
			Matrix.setIdentityM(mTmpMatrix, 0);
			Matrix.setIdentityM(mVMatrix, 0);
			Matrix.translateM(mTmpMatrix, 0, mPosition.x, -mPosition.y,
					-mPosition.z);
			Matrix.multiplyMM(mVMatrix, 0, mRotationMatrix, 0, mTmpMatrix, 0);
		}
		return mVMatrix;
	}

	protected void rotateM(float[] m, int mOffset, float a, float x, float y,
			float z) {
		Matrix.setIdentityM(mRotateMatrixTmp, 0);
		Matrix.setRotateM(mRotateMatrixTmp, 0, a, x, y, z);
		System.arraycopy(m, 0, mTmpMatrix, 0, 16);
		Matrix.multiplyMM(m, mOffset, mTmpMatrix, mOffset, mRotateMatrixTmp, 0);
	}

	public void setRotationMatrix(float[] m) {
		mRotationMatrix = m;
	}

	public void setProjectionMatrix(int width, int height) {
		float ratio = (float) width / height;
		float frustumH = MathUtil.tan(getFieldOfView() / 360.0f * MathUtil.PI)
				* getNearPlane();
		float frustumW = frustumH * ratio;

		Matrix.frustumM(mProjMatrix, 0, -frustumW, frustumW, -frustumH,
				frustumH, getNearPlane(), getFarPlane());
	}

	public float[] getProjectionMatrix() {
		return mProjMatrix;
	}

	public float getNearPlane() {
		return mNearPlane;
	}

	public void setNearPlane(float nearPlane) {
		this.mNearPlane = nearPlane;
	}

	public float getFarPlane() {
		return mFarPlane;
	}

	public void setFarPlane(float farPlane) {
		this.mFarPlane = farPlane;
	}

	public float getFieldOfView() {
		return mFieldOfView;
	}

	public void setFieldOfView(float fieldOfView) {
		this.mFieldOfView = fieldOfView;
	}

	public boolean getUseRotationMatrix() {
		return mUseRotationMatrix;
	}

	public void setUseRotationMatrix(boolean useRotationMatrix) {
		this.mUseRotationMatrix = useRotationMatrix;
	}
}
