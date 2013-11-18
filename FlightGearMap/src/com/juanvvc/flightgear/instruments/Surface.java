package com.juanvvc.flightgear.instruments;

import java.io.IOException;

import android.graphics.Canvas;

import com.juanvvc.flightgear.FGFSConnection;
import com.juanvvc.flightgear.MyBitmap;
import com.juanvvc.flightgear.PlaneData;

/** Manages a layer of an instrument.
 * A layer of a instrument is usually an image.
 * It can be static or respond to some data, such as rotation or so.
 * Surfaces are stacked on an instrument.
 * @author juanvi
 *
 */
public abstract class Surface {
	/** This is the default surface size, width and height. All positions must be expressed
	 * relative to this size!
	 */
	public static final float DEFAULT_SURFACE_SIZE = 512f;
	/** Horizontal position inside the instrument. 1.0=DEFAULT_SURFACE_FACE pixels */
	protected float relx;
	/** Vertical position inside the instrument. */
	protected float rely;
	/** The name of the image file of this surface (does not include the directory) */
	protected MyBitmap bitmap;
	/** The parent instrument for this surface. */
	protected Instrument parent;
	/** The last PlaneData object. */
	protected PlaneData planeData;
	
	/**
	 * @param file The name of the file to load (does not include the directory)
	 * @param x horizontal position inside the instrument
	 * @param y vertical position inside the instrument
	 */
	public Surface(MyBitmap bitmap, final float x, final float y) {
		this.bitmap = bitmap;
		this.relx = x / DEFAULT_SURFACE_SIZE;
		this.rely = y / DEFAULT_SURFACE_SIZE;
	}
	
	public void setParent(final Instrument ins) {
		this.parent = ins;
	}
	
	/** Returns the parent instrument of this surface */
	public Instrument getParent() {
		return parent;
	}
	
	/** Returns the bitmap of this surface */
	public MyBitmap getBitmap() {
		return this.bitmap;
	}
	
	/** The bitmap that the surface uses has changed.
	 * This usually means that the screen size has changed, or the surface has been loaded.
	 * The default behavior does nothing, but some surfaces may calculate or update their
	 * internal reference points, font sizes or transform matrices.
	 */
	public void onBitmapChanged() {
		// Does nothing
	}
	
	/** Receives a new planeData.
	 * Most surfaces don't need to change this method.
	 * @param pd The last PlaneData object */
	public void postPlaneData(final PlaneData pd) {
		this.planeData = pd;
	}
	
	/**
	 * Check if this surface controls a point to change its value.
	 * If this method returns true, the system will inform the surface abouot any movement
	 * from this one to the end of the movement. Use to calibrate surfaces on touchable screens.
	 * 
	 * @param x The x position of an event from the user inside the instrument, in 512 scale
	 * @param y The y position of an event from the user inside the instrument, in 512 scale
	 * @return True if this surface controls an event on point (x, y). Currently,
	 * this method returns always false ("we do not manage movements"). Override to do
	 * something useful. 
	 */
	public boolean youControl(final float x, final float y) {
		return false;
	}
	
	
	/** Gets a movement event from the user.
	 * Currently, this method does nothing. To do something useful, override this method
	 * and return true in youControl(x,y).
	 * @param x The x position of an event from the user inside the instrument, in 512 scale
	 * @param y The y position of an event from the user inside the instrument, in 512 scale
	 * @param end If true, the movement has ended. It is up to the class to detect starting movements.
	 */
	public void onMove(final float x, final float y, final boolean end) {
		// Does nothing
	}
	
	
	/** Draws the surface.
	 * @param c The canvas where draw the surface on.
	 * @param pd The current PlaneData object. */
	public abstract void onDraw(final Canvas c);
	
	/** The CalibratableSurfaceManager changed.
	 * A CalibratableSurfaceManager is used to control surfaces that have some data to send
	 * to the remote fgfs: selected radials, switches... A surface that needs send these data,
	 * will register in the CalibratableSurfaceManager. Then, periodically the manager asks
	 * if the surface is dirty and, it it is, calls the update method.
	 * @param cs
	 */
	public void postCalibratableSurfaceManager(final CalibratableSurfaceManager cs) {
		// Does nothing
	}
	
	/** Returns true if the surface has some value to send to the remote fgfs */
	public boolean isDirty() {
		return false;
	}
	
	/** Is it is dirty, this method will be called to send the value to the remote fgfs */
	public void update(final FGFSConnection conn) throws IOException {
		// Does nothing
	}
}
