package first.tops.com.linear

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle

/**
 * Simple Activity for curl testing.
 * 
 * @author harism
 */
class CurlActivity: Activity() {

	private lateinit var mCurlView: CurlView

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		var index: Int = 0;
		if (getLastNonConfigurationInstance() != null) {
			index = getLastNonConfigurationInstance() as Int
		}
		mCurlView = findViewById(R.id.curl) as CurlView
		mCurlView.setPageProvider(PageProvider())
		mCurlView.setSizeChangedObserver(SizeChangedObserver())
		mCurlView.setCurrentIndex(index)
		mCurlView.setBackgroundColor(0xFF202830)

		// This is something somewhat experimental. Before uncommenting next
		// line, please see method comments in CurlView.
		// mCurlView.setEnableTouchPressure(true);
	}

	override fun onPause() {
		super.onPause()
		mCurlView.onPause()
	}

	override fun onResume() {
		super.onResume()
		mCurlView.onResume()
	}

	public fun onRetainNonConfigurationInstance(): Object {
		return mCurlView.getCurrentIndex()
	}

	/**
	 * Bitmap provider.
	 */
	private class PageProvider : CurlView.PageProvider {

		// Bitmap resources.
		private var mBitmapIds = intArrayOf(
			R.drawable.obama,
			R.drawable.road_rage,
			R.drawable.taipei_101,
			R.drawable.world
		)

		override fun getPageCount(): Int = 5

		private fun loadBitmap(width: Int, height: Int, index: Int): Bitmap {
			var b: Bitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888)
			b.eraseColor(0xFFFFFFFF)
			var c: Canvas = Canvas(b)
			var d: Drawable = getResources().getDrawable(mBitmapIds[index])

			var margin = 7
			var border = 3
			var r = Rect(margin, margin, width - margin, height - margin)

			var imageWidth = r.width() - (border * 2)
			var imageHeight = imageWidth * d.getIntrinsicHeight()
					/ d.getIntrinsicWidth()
			if (imageHeight > r.height() - (border * 2)) {
				imageHeight = r.height() - (border * 2)
				imageWidth = imageHeight * d.getIntrinsicWidth()
						/ d.getIntrinsicHeight()
			}

			r.left += ((r.width() - imageWidth) / 2) - border
			r.right = r.left + imageWidth + border + border
			r.top += ((r.height() - imageHeight) / 2) - border
			r.bottom = r.top + imageHeight + border + border

			var p = Paint()
			p.setColor(0xFFC0C0C0)
			c.drawRect(r, p)
			r.left += border
			r.right -= border
			r.top += border
			r.bottom -= border

			d.setBounds(r)
			d.draw(c)

			return b
		}

		override fun updatePage(page: CurlPage, width: Int, height: Int, index: Int) {

			when (index) {
			// First case is image on front side, solid colored back.
			0 -> {
				var front = loadBitmap(width, height, 0)
				page.setTexture(front, CurlPage.SIDE_FRONT)
				page.setColor(Color.rgb(180, 180, 180), CurlPage.SIDE_BACK)
			}
			// Second case is image on back side, solid colored front.
			1 -> {
				var back = loadBitmap(width, height, 2)
				page.setTexture(back, CurlPage.SIDE_BACK)
				page.setColor(Color.rgb(127, 140, 180), CurlPage.SIDE_FRONT)
			}
			// Third case is images on both sides.
			2 -> {
				var front = loadBitmap(width, height, 1)
				var back = loadBitmap(width, height, 3)
				page.setTexture(front, CurlPage.SIDE_FRONT)
				page.setTexture(back, CurlPage.SIDE_BACK)
			}
			// Fourth case is images on both sides - plus they are blend against
			// separate colors.
			3 -> {
				var front = loadBitmap(width, height, 2)
				var back = loadBitmap(width, height, 1)
				page.setTexture(front, CurlPage.SIDE_FRONT)
				page.setTexture(back, CurlPage.SIDE_BACK)
				page.setColor(Color.argb(127, 170, 130, 255),
						CurlPage.SIDE_FRONT)
				page.setColor(Color.rgb(255, 190, 150), CurlPage.SIDE_BACK)
			}
			// Fifth case is same image is assigned to front and back. In this
			// scenario only one texture is used and shared for both sides.
			4 -> {
				var front = loadBitmap(width, height, 0)
				page.setTexture(front, CurlPage.SIDE_BOTH)
				page.setColor(Color.argb(127, 255, 255, 255),
						CurlPage.SIDE_BACK)
			}
		}

	}

	/**
	 * CurlView size changed observer.
	 */
	private class SizeChangedObserver : CurlView.SizeChangedObserver {
		override fun onSizeChanged(w: Int, h: Int) {
			if (w > h) {
				mCurlView.setViewMode(CurlView.SHOW_TWO_PAGES)
				mCurlView.setMargins(.1f, .05f, .1f, .05f)
			} else {
				mCurlView.setViewMode(CurlView.SHOW_ONE_PAGE)
				mCurlView.setMargins(.1f, .1f, .1f, .1f)
			}
		}
	}

}
