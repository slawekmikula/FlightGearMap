/**
 * 
 */
package com.juanvvc.comicviewer.readers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import de.innosystec.unrar.Archive;
import de.innosystec.unrar.rarfile.FileHeader;

/**
 * @author juanvi
 *
 */
public class CBRReader extends Reader {
	private Archive archive=null;
	List<? extends FileHeader> entries = null;
	
	public CBRReader(Context context){
		super(context);
		Log.v(TAG, "Usig CBRReader");
		this.archive = null;
	}
	public CBRReader(Context context, String uri) throws ReaderException{
		super(context);
		Log.v(TAG, "Usig CBRReader");
		this.archive = null;
		this.load(uri);
	}


	public void load(String uri) throws ReaderException {
		Log.i(TAG, "Loading URI"+uri);
		// tries to open the RAR file
		try{
			this.archive = new Archive(new File(uri));			
		}catch(Exception e){
			this.uri = null;
			throw new ReaderException(e.getMessage());
		}
		// throws an exception if the file is encrypted
		if(this.archive.isEncrypted()){
			this.archive = null;
			throw new ReaderException(this.context.getString(com.juanvvc.comicviewer.R.string.encrypted_file));
		}
		this.entries=this.archive.getFileHeaders();
		// removes files that are not .jpg or .png
		Iterator<? extends FileHeader> itr=this.entries.iterator();
		while(itr.hasNext()){
			FileHeader e = itr.next();
			String name = e.getFileNameString().toLowerCase();
			if(e.isDirectory() || !(name.endsWith(".jpg") || name.endsWith(".png")))
				itr.remove();
		}
		// sort the names alphabetically
		Collections.sort(this.entries, new Comparator<FileHeader>(){
			public int compare(FileHeader lhs, FileHeader rhs) {
				String n1=lhs.getFileNameString();
				String n2=rhs.getFileNameString();
				return n1.compareTo(n2);
			}
			
		});
		super.load(uri);
	}


	public void close() {
		try{
			this.archive.close();
		}catch(IOException e){}
		this.archive = null;
		this.currentPage = -1;

	}

	private Drawable getDrawableFromRarEntry(FileHeader entry) throws ReaderException{
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// sometimes, a outofmemory is triggered here. Try to save as much memory as possible
			System.gc();
			this.archive.extractFile(entry, baos);
			baos.close();
			return new BitmapDrawable(this.byteArrayToBitmap(baos.toByteArray()));
		}catch(Exception e){
			throw new ReaderException("Cannot read page: "+e.getMessage());
		}catch(OutOfMemoryError err){
			throw new ReaderException(this.context.getString(com.juanvvc.comicviewer.R.string.outofmemory));
		}
	}

	public Drawable next() throws ReaderException{
		if(this.archive==null)
			return null;
		if(this.currentPage<-1 || this.currentPage>=this.entries.size())
			return null;
		this.currentPage += 1;
		return this.current();
	}

	public Drawable prev() throws ReaderException{
		if(this.archive==null)
			return null;
		if(this.currentPage<=0 || this.currentPage>=this.entries.size())
			return null;
		this.currentPage -= 1;
		return this.current();
	}

	public Drawable current()  throws ReaderException {
		if(this.currentPage<0 || this.currentPage>=this.countPages()) return null;
		return this.getDrawableFromRarEntry(this.entries.get(this.currentPage));
	}

	public int countPages() {
		if(this.archive!=null)
			return this.entries.size();
		else
			return -1;
	}

	public int currentPage() {
		return this.currentPage;
	}
	
	public void moveTo(int page) {
		this.currentPage = page;
	}
	
	public String getURI(){
		return this.uri;
	}

}