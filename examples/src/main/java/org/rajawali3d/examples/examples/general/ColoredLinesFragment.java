package org.rajawali3d.examples.examples.general;

import android.content.Context;
import android.graphics.Color;
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

import org.json.JSONArray;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.json.JSONException;
import org.json.JSONObject;
import org.rajawali3d.Object3D;
import org.rajawali3d.animation.Animation;
import org.rajawali3d.animation.RotateOnAxisAnimation;
import org.rajawali3d.animation.TranslateAnimation3D;
import org.rajawali3d.examples.examples.AExampleFragment;
import org.rajawali3d.materials.Material;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Cube;
import org.rajawali3d.primitives.Line3D;

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

class Points {
	private Point[] arr;
	public Points(Point[] arr) {
		this.arr = arr;
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
				  // WebSocket 连接已打开
				  Log.d("WebSocket", "Connection opened");
			  }

			  @Override
			  public void onMessage(String message) {
				  // 收到消息
				  Log.d("WebSocket....", "Received message: " + message);
				  try {
					  ObjectMapper objectMapper = new ObjectMapper();
					  JsonNode node = objectMapper.readTree(message);
					  // Log.i("143", String.valueOf(node));
					  String jsonString = "[{\"x\":1.0,\"y\":2.0},{\"x\":3.0,\"y\":4.0}]";
					  // ObjectMapper objectMapper = new ObjectMapper();
					  List<Point> points = objectMapper.readValue(String.valueOf(node), new TypeReference<List<Point>>() {});

//					  for (Point point : points) {
//						  Log.i("point is: ", String.valueOf(point.getX()));
//						  Log.i("point is: ", String.valueOf(point.getY()));
//					  }
					  ((ColoredLinesRenderer)mRenderer).setRouteData(points);
					  // TypeReference<Point[]> typeReference = new TypeReference<Point[]>() {};
					  // objectMapper.readValue(message, typeReference);
					  // Log.i("result is: ", String.valueOf(result));
//					  for(Point point: result) {
//						  Log.i(String.valueOf(point.getX()), String.valueOf(point.getY()));
//					  }
					  // JSONArray renderRes = new JSONArray(message);
					  // Log.i("ele is: ", String.valueOf(renderRes));
//					  for(int i = 0; i < renderRes.length(); ++i) {
//						  // JSONObject obj = renderRes.getJSONObject(i);
//						  Log.i("ele is: ", String.valueOf(renderRes.get(i)));
//					  }
				  } catch (Exception e) {
					  throw new RuntimeException(e);
				  }

				  // 在这里处理收到的消息，并将数据传递给 renderer 函数进行渲染
				  // renderData(message);
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

//			pointcloudClient = new WebSocketClient(new URI("ws://172.17.0.1:3000?pointcloud=true")) {
//				//@Override
//				public void onOpen(ServerHandshake handshakedata) {
//					Log.i("12", "34");
//				}
//
//				//@Override
//				public void onMessage(String message) {
//					Log.i("point cloud is: ", message);
//
//					ObjectMapper objectMapper = new ObjectMapper();
//					try {
//						JsonNode node = objectMapper.readTree(message);
//						Log.i("point cloud node is: ", String.valueOf(node));
//					} catch (JsonProcessingException e) {
//						throw new RuntimeException(e);
//					}
//				}
//
//				//@Override
//				public void onClose(int code, String reason, boolean remote) {
//
//				}
//
//				// @Override
//				public void onError(Exception ex) {
//
//				}
//			};

			webSocketClient.connect();
            // pointcloudClient.connect();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
//		}catch (Exception e) {
//
//		}
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
//			Log.i("camera: ", String.valueOf(getCurrentCamera()));
//			Log.i("x is: ", String.valueOf(x));
//			Log.i("y is: ", String.valueOf(y));
//			Log.i("z is: ", String.valueOf(z));
			getCurrentCamera().setPosition(x, y, z);
		}

		public void setRouteData(List<Point> routeData) {
			Log.i("route data is: ", String.valueOf(routeData));
//			for (Point point : routeData) {
//				Log.i("point x in render is: ", String.valueOf(point.getX()));
//				Log.i("point y in render is: ", String.valueOf(point.getY()));
//			}
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

        @Override
		protected void initScene() {
			getCurrentCamera().setPosition(0, 0, 5);
			getCurrentCamera().setLookAt(0, 0, 0);

			Stack<Vector3> points = new Stack<>();
			Stack<Vector3> pointsAnother = new Stack<>();
//			int[] colors = new int[2000];
//			int colorCount = 0;
//			for (int i = -1000; i < 1000; i++) {
//				double j = i * .5;
//				Vector3 v = new Vector3();
//				//v.x = Math.cos(j * .4);
//				v.x = j*.4;
//				//v.y = Math.sin(j * .3);
//				v.y = j*.3;
//				v.z = j * .01;
//				points.add(v);
//				colors[colorCount++] = Color.argb(255,1.0f, 1.0f, 1.0f);
//						// (int) (190.f * Math.sin(j)),
//						// (int) (190.f * Math.cos(j * .3f)),
//						// (int) (190.f * Math.sin(j * 2) * Math.cos(j)));
//			}

			int[] colors = new int[this.routeData.size()];
			int[] restColors = new int[this.routeData.size()];
			int[] mainLineColors = new int[this.routeData.size()];
			int index = 0;
			for(Point point:  routeData) {
				colors[index] = Color.argb(255,1.0f, 1.0f, 1.0f);
				mainLineColors[index] = Color.argb(255, 0.5f, 0.0f, 0.5f);
				restColors[index++] = Color.argb(255,0.0f, 1.0f, 0.0f);
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
			for(Vector3 point: points) {
//				Log.i("x is: ", String.valueOf(point.x));
//				Log.i("y is: ", String.valueOf(point.y));
//				Log.i("z is: ", String.valueOf(point.z));
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

//			Cube cube = new Cube(3.0f);
//			Log.i("cube is: ", String.valueOf(cube));
//			Material material1 = new Material();
//			material1.useVertexColors(true);
//			cube.setMaterial(material1);
//			Log.i("cube before add:", String.valueOf(cube));
//			getCurrentScene().addChild(cube);

			Cube mCubeBox = new Cube(0.8f);
			mCubeBox.setMaterial(material);
			// mCubeBox.setColor(0x00000000);
			mCubeBox.setColor(Color.WHITE);
			mCubeBox.setPosition(0.75f, 0.0f, -2.5f);
			mCubeBox.setShowBoundingVolume(true);
			getCurrentScene().addChild(mCubeBox);

			Cube mCubeBox2 = new Cube(0.8f);
			mCubeBox2.setMaterial(material);
			mCubeBox2.setColor(0x00000000);
			mCubeBox2.setPosition(3.5f, 0.0f, -6.0);
			mCubeBox2.setShowBoundingVolume(true);
			getCurrentScene().addChild(mCubeBox2);

			Cube mCubeBox3 = new Cube(0.8f);
			mCubeBox3.setMaterial(material);
			mCubeBox3.setColor(0x00000000);
			mCubeBox3.setPosition(1.3f, 0.0f, -18.0);
			mCubeBox3.setShowBoundingVolume(true);
			getCurrentScene().addChild(mCubeBox3);

			Cube mCubeBox4 = new Cube(0.8f);
			mCubeBox4.setMaterial(material);
			mCubeBox4.setColor(0x00000000);
			mCubeBox4.setPosition(-1.15f, 0.0f, -4.5);
			mCubeBox4.setShowBoundingVolume(true);
			getCurrentScene().addChild(mCubeBox4);

//			Object3D mCubeSphere = new Cube(1);
//			mCubeSphere.setMaterial(material);
//			mCubeSphere.setColor(0xff00bfff);
//			mCubeSphere.setPosition(1, -2, 0);
//			mCubeSphere.setShowBoundingVolume(true);
//			getCurrentScene().addChild(mCubeSphere);



//			RotateOnAxisAnimation lineAnim = new RotateOnAxisAnimation(Vector3.Axis.Y, 359);
//			lineAnim.setDurationMilliseconds(10000);
//			lineAnim.setRepeatMode(Animation.RepeatMode.INFINITE);
//			lineAnim.setTransformable3D(line);
//			getCurrentScene().registerAnimation(lineAnim);
//			lineAnim.play();
//
//			TranslateAnimation3D camAnim = new TranslateAnimation3D(
//					new Vector3(0, 0, 10), new Vector3(0, 0, -10));
//			camAnim.setDurationMilliseconds(12000);
//			camAnim.setRepeatMode(Animation.RepeatMode.REVERSE_INFINITE);
//			camAnim.setTransformable3D(getCurrentCamera());
//			getCurrentScene().registerAnimation(camAnim);
//			 camAnim.play();
		}

	}

}
