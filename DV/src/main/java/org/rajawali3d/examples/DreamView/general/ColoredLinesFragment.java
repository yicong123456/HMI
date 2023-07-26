package org.rajawali3d.examples.DreamView.general;

import android.content.Context;
import android.graphics.Bitmap;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.rajawali3d.examples.DreamView.AExampleFragment;
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
				  // WebSocket 连接已打开
				  Log.d("WebSocket", "Connection opened");
			  }

			  @Override
			  public void onMessage(String message) {
				  Log.d("WebSocket....", "Received message: " + message);
				  try {
					  ObjectMapper objectMapper = new ObjectMapper();
					  JsonNode node = objectMapper.readTree(message);
					  // Log.i("143", String.valueOf(node));
					  // String jsonString = "[{\"x\":1.0,\"y\":2.0},{\"x\":3.0,\"y\":4.0}]";
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

//			Cube cube = new Cube(3.0f);
//			Log.i("cube is: ", String.valueOf(cube));
//			Material material1 = new Material();
//			material1.useVertexColors(true);
//			cube.setMaterial(material1);
//			Log.i("cube before add:", String.valueOf(cube));
//			getCurrentScene().addChild(cube);

			// Box mBox = new Box();

//			Cube mCubeBox = new Cube(0.8f);
//			mCubeBox.setMaterial(material);
//			// mCubeBox.setColor(0x00000000);
//			mCubeBox.setColor(Color.WHITE);
//			mCubeBox.setPosition(0.75f, 0.0f, -2.5f);
//			mCubeBox.setShowBoundingVolume(true);
//			getCurrentScene().addChild(mCubeBox);



            // TextMaterial cub1Text = new TextMaterial();
			Cube mCubeBox2 = new Cube(0.8f);
			Material cubeBox2Material = new Material();
             String txt = "7.8m, 9.8m/s";
             int size = 60;
			int color = Color.WHITE;
			Bitmap bitmap = TextRenderer.renderTextToBitmap(txt, size, color);
			Texture texture = new Texture("textTexture", bitmap);
			try{
				cubeBox2Material.addTexture(texture);
			}catch (Exception e) {
				e.printStackTrace();
			}
			Log.i("material is: ", String.valueOf(cubeBox2Material));



			// WireframeMaterial wireframeMaterial = new WireframeMaterial();
			mCubeBox2.setMaterial(cubeBox2Material);
			// mCubeBox2.setColor(0x00000000);
			mCubeBox2.setColor(Color.GREEN);
			mCubeBox2.setPosition(3.4f, 0.4f, -6.0);
			mCubeBox2.setShowBoundingVolume(true);
			getCurrentScene().addChild(mCubeBox2);

			Cube mCubeBox3 = new Cube(0.8f);
			Material cubeBox3Material = new Material();
			mCubeBox3.setMaterial(cubeBox3Material);
			mCubeBox3.setColor(Color.GREEN);
			mCubeBox3.setPosition(1.3f, 0.4f, -18.0);
			mCubeBox3.setShowBoundingVolume(true);
			getCurrentScene().addChild(mCubeBox3);

			Cube mCubeBox4 = new Cube(0.8f);
			Material cubeBox4Material = new Material();
			mCubeBox4.setMaterial(cubeBox4Material);
			mCubeBox4.setColor(Color.GREEN);
			mCubeBox4.setPosition(-2.85f, 0.4f, -34.5);
			mCubeBox4.setShowBoundingVolume(true);
			getCurrentScene().addChild(mCubeBox4);
			
//			Object3D temp = new RectangularPrism(1.4f, 0.9f, 1.9f);
//			Material obj3DMaterial = new Material();
//			temp.setMaterial(obj3DMaterial);
//			// temp.setColor(0xFF00FF00);
//			temp.setColor(Color.RED);
//			// temp.setPosition(0.75f, 0.0f, -2.5f);
//			temp.setPosition(0.70f, 0.95f, -2.5f);
//			temp.setShowBoundingVolume(true);
//			getCurrentScene().addChild(temp);

			Cube mCubeBox5 = new Cube(0.9f);
			Material cubeBox5Material = new Material();
			mCubeBox5.setMaterial(cubeBox5Material);
			mCubeBox5.setColor(Color.WHITE);
			mCubeBox5.setPosition(0.70f, 0.45f, -2.5f);
			mCubeBox5.setShowBoundingVolume(true);
			getCurrentScene().addChild(mCubeBox5);

		}

	}

}
