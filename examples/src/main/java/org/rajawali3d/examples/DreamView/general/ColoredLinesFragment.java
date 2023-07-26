package org.rajawali3d.examples.DreamView.general;

import org.rajawali3d.loader.ALoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLES20;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.rajawali3d.Object3D;
import org.rajawali3d.examples.DreamView.AExampleFragment;
import org.rajawali3d.examples.DreamView.materials.CubeBoxMaterial;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Cube;
import org.rajawali3d.primitives.Line3D;
import org.rajawali3d.materials.textures.Texture;
// import org.rajawali3d.materials.textures.;
// import org.rajawali3d.materials.textures.utils.


import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Stack;

class Point {
	private float x, y;

	public Point() {

	}

	public Point(float x, float y) {
        this.x = x;
		this.y = y;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public void setX(float x) { this.x = x; }

	public void setY(float y) { this.y = y; }

	public static Point fromString(String pointString) {
		String[] coordinates = pointString.split(",");
		float x = Float.parseFloat(coordinates[0]);
		float y = Float.parseFloat(coordinates[1]);
		return new Point(x, y);
	}
}

public class ColoredLinesFragment extends AExampleFragment implements
		SeekBar.OnSeekBarChangeListener {

	private SeekBar mSeekBarX, mSeekBarY, mSeekBarZ;
	private float processX = 50.0f, processY = 50.0f, processZ = 50.0f;

	private Vector3 mCameraOffset;
	private WebSocketClient webSocketClient;
	private WebSocketClient pointcloudClient;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mCameraOffset = new Vector3();
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		LinearLayout ll = new LinearLayout(getActivity());
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.setGravity(Gravity.BOTTOM);

		mSeekBarZ = new SeekBar(getActivity());
		mSeekBarZ.setMax(100);
		mSeekBarZ.setProgress(70);
		mSeekBarZ.setOnSeekBarChangeListener(this);
		ll.addView(mSeekBarZ);

		mSeekBarY = new SeekBar(getActivity());
		mSeekBarY.setMax(100);
		mSeekBarY.setProgress(60);
		mSeekBarY.setOnSeekBarChangeListener(this);
		ll.addView(mSeekBarY);

		mSeekBarX = new SeekBar(getActivity());
		mSeekBarX.setMax(100);
		mSeekBarX.setProgress(50);
		mSeekBarX.setOnSeekBarChangeListener(this);
		ll.addView(mSeekBarX);

		mLayout.addView(ll);


		return mLayout;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		try {
          Log.i("data", "communicate");
		  webSocketClient = new WebSocketClient(new URI("ws://172.17.0.1:3000?realtime=true")) {
			  @Override
			  public void onOpen(ServerHandshake handshakedata) {
				  Log.d("WebSocket", "Connection opened");
			  }

			  @Override
			  public void onMessage(String message) {
				  Log.d("WebSocket....", "Received message: " + message);
				  try {
					  ObjectMapper objectMapper = new ObjectMapper();
					  JsonNode node = objectMapper.readTree(message);
					  List<Point> points = objectMapper.readValue(String.valueOf(node), new TypeReference<List<Point>>() {});
					  ((ColoredLinesRenderer)mRenderer).setRouteData(points);
				  } catch (Exception e) {
					  throw new RuntimeException(e);
				  }
			  }

			  @Override
			  public void onClose(int code, String reason, boolean remote) {
				  // WebSocket 连接已关闭
				  Log.d("WebSocket", "Connection closed");
			  }

			  @Override
			  public void onError(Exception ex) {
				  // WebSocket 连接出错
				  Log.e("WebSocket", "Connection error: " + ex.getMessage());
			  }
		  };

			webSocketClient.connect();
            // pointcloudClient.connect();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

	}

	@Override
    public AExampleRenderer createRenderer() {
		return new ColoredLinesRenderer(getActivity(), this);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		Log.i("seek bar: ", String.valueOf(seekBar));
		if(seekBar == mSeekBarX) {
			processX = (float)progress;
		}else if(seekBar == mSeekBarY) {
			processY = (float)progress;
		}else {
			processZ = (float)progress;
		}

		((ColoredLinesRenderer)mRenderer).setCamera(processX,processY,processZ );
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

	private final class ColoredLinesRenderer extends AExampleRenderer {
		private List<Point> routeData;

		public ColoredLinesRenderer(Context context, @Nullable AExampleFragment fragment) {
			super(context, fragment);
		}

		public void setCamera(float x, float y, float z) {
			getCurrentCamera().setPosition(x, y, z);
		}

		public void setRouteData(List<Point> routeData) {
			Log.i("route data is: ", String.valueOf(routeData));
			this.routeData = routeData;
		}

		public Stack<Vector3> getRoadLine(float posDif) {
			Stack<Vector3> res = new Stack<>();
			for(Point p: routeData) {
				Vector3 curV = new Vector3();
				curV.z = p.getX();
				curV.y = 0;
				curV.x = p.getY() + posDif;
				res.add(curV);
			}
			return res;
		}

		public void generateLine(Stack<Vector3> pointsAnother, int[] colors) {
			Line3D line2 = new Line3D(pointsAnother, 1, colors);
			Material material2 = new Material();
			material2.useVertexColors(true);
			line2.setMaterial(material2);
			getCurrentScene().addChild(line2);
		}

		public void putVehicle(float size, int color, float x, float z, float alpha) {
			Cube mCubeBox5 = new Cube(size);
			CubeBoxMaterial cubeBox5Material = new CubeBoxMaterial();
			float[] curColor = new float[4];
			curColor[0] = (float) Color.red(color) / 255.f;
			curColor[1] = (float) Color.green(color) / 255.f;
			curColor[2] = (float) Color.blue(color) / 255.f;
			curColor[3] = alpha;
			mCubeBox5.setMaterial(cubeBox5Material);
			mCubeBox5.setTransparent(true);
			mCubeBox5.getMaterial().setColor(curColor);
			Log.i("height is: ", String.valueOf(size/2.0f));
			mCubeBox5.setPosition(x, size/2.0f , z);
			mCubeBox5.setShowBoundingVolume(true);
			getCurrentScene().addChild(mCubeBox5);
		}

        @Override
		protected void initScene() {
			getCurrentCamera().setPosition(0, 0, 5);
			getCurrentCamera().setLookAt(0, 0, 0);

			Stack<Vector3> points = new Stack<>();
			Stack<Vector3> pointsAnother = new Stack<>();

			int[] colors = new int[this.routeData.size()];
			int[] restColors = new int[this.routeData.size()];
			int[] mainLineColors = new int[this.routeData.size()];
			int index = 0;
			for(Point point:  routeData) {
				colors[index] = Color.argb(255,1.0f, 1.0f, 1.0f);
				mainLineColors[index] = Color.argb(255, 0.5f, 0.0f, 0.5f);
				// restColors[index++] = Color.argb(255,0.0f, 1.0f, 0.0f);
				restColors[index++] = Color.argb(255,1.0f, 1.0f, 1.0f);
				Vector3 v = new Vector3();
				Vector3 v2 = new Vector3();
				v.x = point.getY();
				v2.x = point.getY()+1.5f;
				v.y = 0;
				v2.y = 0;
				v.z = point.getX();
				v2.z = point.getX();
				points.add(v);
				pointsAnother.add(v2);
			}

			Line3D line = new Line3D(points, 1, colors);
			Material material = new Material();
			material.useVertexColors(true);
			line.setMaterial(material);
			getCurrentScene().addChild(line);

			Line3D line2 = new Line3D(pointsAnother, 1, colors);
			Material material2 = new Material();
			material2.useVertexColors(true);
			line2.setMaterial(material2);
			getCurrentScene().addChild(line2);

			Stack<Vector3> linePos3 = this.getRoadLine(-1.5f);
			this.generateLine(linePos3, restColors);

			Stack<Vector3> linePos4 = this.getRoadLine(-3.0f);
			this.generateLine(linePos4, restColors);

			Stack<Vector3> linePos5 = this.getRoadLine(-4.5f);
			this.generateLine(linePos5, restColors);

			Stack<Vector3> linePos = this.getRoadLine(3.0f);
			this.generateLine(linePos, restColors);

			Stack<Vector3> linePos1 = this.getRoadLine(4.5f);
			this.generateLine(linePos1, restColors);

			Stack<Vector3> linePos2 = this.getRoadLine(6.0f);
			this.generateLine(linePos2, restColors);

			Stack<Vector3> mainLinePos = this.getRoadLine(0.80f);
			this.generateLine(mainLinePos, mainLineColors);

            // TextMaterial cub1Text = new TextMaterial();
			this.putVehicle(0.8f, Color.GREEN, 3.4f, -6.0f, 0.5f);
			this.putVehicle(0.8f, Color.GREEN, 1.3f, -18.0f, 0.5f);
			this.putVehicle(0.8f, Color.GREEN, -2.85f, -34.5f, 0.5f);
			this.putVehicle(1.3f, Color.WHITE, 0.70f, -2.5f, 0.5f);

		}

	}

}
