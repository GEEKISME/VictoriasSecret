package view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


public class RadiationView extends View {

	private Paint mPaint = new Paint();


	private int mColor = 0x3f990e;

	private int mAlpha = 60;

	private Handler mHandler = null;

	private static final int MESSAGE_DRAW = 0;
	private static final String TAG = "RadiationView";

	private int width;
	private int height;

	private int speed = 30;

	private int maxRadius = 100;

	private int centerX;
	private int centerY;

	private int minRadius = 70;

	private int radius = minRadius;

	private boolean isStarted = false;

	public RadiationView(Context context) {
		super(context);
		init();
	}

	public RadiationView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
		init();
	}

	public void startRadiate() {
		isStarted = true;
		mHandler.sendEmptyMessage(MESSAGE_DRAW);
	}

	private void init() {
		mPaint = new Paint();
		mPaint.setStrokeWidth(1);
		mPaint.setColor(mColor);
		mPaint.setAlpha(mAlpha);

		mHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (msg.what == MESSAGE_DRAW) {
					invalidate();
					if (isStarted) {
						sendEmptyMessageDelayed(MESSAGE_DRAW, speed);
					}
				}
			}
		};
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		width = this.getWidth();
		height = this.getHeight();
		if (width <= 0 || height <= 0) {
			throw new RuntimeException("size illegal");
		}

		centerX = width / 2;
		centerY = height / 2;

		maxRadius = (width > height) ? height / 2 : width / 2;
		Log.i(TAG, "MAX" + maxRadius);
		if (maxRadius < 70) {
			throw new RuntimeException("size too small");
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		mPaint.setColor(mColor);
		mPaint.setAlpha(mAlpha);
		if (radius <= 0) {
			return;
		}
		if (radius > maxRadius) {
			radius = minRadius;
		}
		canvas.save();
		canvas.drawCircle(centerX, centerY, radius, mPaint);
		canvas.restore();
		radius += 1;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	}

	public void setColor(int color) {
		this.mColor = color;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public void setMinRadius(int radius) {
		this.minRadius = radius;
	}
}
