package com.esaygo.app.common.blutoothkit;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

class ImageProcessing {
		
	// 转成灰度图
	public static Bitmap toGrayscale(Bitmap bmpOriginal) {
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();
		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}

	public static Bitmap resizeImage(Bitmap bitmap, int w, int h)
	{
		int bitmapWidth = bitmap.getWidth();
		int bitmapHeight = bitmap.getHeight();
		// 缩放图片的尺寸
		float scaleWidth = (float) w / bitmapWidth;
		float scaleHeight = (float) h / bitmapHeight;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);
	}

	// 16*16
	private static int[][] Floyd16x16 = /* Traditional Floyd ordered dither */
	{
			{ 0, 128, 32, 160, 8, 136, 40, 168, 2, 130, 34, 162, 10, 138, 42,
					170 },
			{ 192, 64, 224, 96, 200, 72, 232, 104, 194, 66, 226, 98, 202, 74,
					234, 106 },
			{ 48, 176, 16, 144, 56, 184, 24, 152, 50, 178, 18, 146, 58, 186,
					26, 154 },
			{ 240, 112, 208, 80, 248, 120, 216, 88, 242, 114, 210, 82, 250,
					122, 218, 90 },
			{ 12, 140, 44, 172, 4, 132, 36, 164, 14, 142, 46, 174, 6, 134, 38,
					166 },
			{ 204, 76, 236, 108, 196, 68, 228, 100, 206, 78, 238, 110, 198, 70,
					230, 102 },
			{ 60, 188, 28, 156, 52, 180, 20, 148, 62, 190, 30, 158, 54, 182,
					22, 150 },
			{ 252, 124, 220, 92, 244, 116, 212, 84, 254, 126, 222, 94, 246,
					118, 214, 86 },
			{ 3, 131, 35, 163, 11, 139, 43, 171, 1, 129, 33, 161, 9, 137, 41,
					169 },
			{ 195, 67, 227, 99, 203, 75, 235, 107, 193, 65, 225, 97, 201, 73,
					233, 105 },
			{ 51, 179, 19, 147, 59, 187, 27, 155, 49, 177, 17, 145, 57, 185,
					25, 153 },
			{ 243, 115, 211, 83, 251, 123, 219, 91, 241, 113, 209, 81, 249,
					121, 217, 89 },
			{ 15, 143, 47, 175, 7, 135, 39, 167, 13, 141, 45, 173, 5, 133, 37,
					165 },
			{ 207, 79, 239, 111, 199, 71, 231, 103, 205, 77, 237, 109, 197, 69,
					229, 101 },
			{ 63, 191, 31, 159, 55, 183, 23, 151, 61, 189, 29, 157, 53, 181,
					21, 149 },
			{ 254, 127, 223, 95, 247, 119, 215, 87, 253, 125, 221, 93, 245,
					117, 213, 85 } };

	/**
	 * 将256色灰度图转换为2值图
	 * 黑点用1表示 白点用0表示
	 * 
	 * @param orgpixels
	 * @param xsize
	 * @param ysize
	 * @param despixels
	 */
	public static void format_K_dither16x16(int xsize, int ysize, byte[] orgpixels, boolean[] despixels) {
		int k = 0;
		for (int y = 0; y < ysize; y++) {

			for (int x = 0; x < xsize; x++) {

				if ((orgpixels[k] & 0xff) > Floyd16x16[x & 15][y & 15])
					despixels[k] = false;// white
				else
					despixels[k] = true;

				k++;
			}
		}

	}

	// 灰度越大，图片越白
	public static void format_K_threshold(int xsize, int ysize, byte[] orgpixels, boolean[] despixels) {

		int graytotal = 0;
		int grayave = 128;
		int i, j;
		int gray;

		int k = 0;
		for (i = 0; i < ysize; i++) {

			for (j = 0; j < xsize; j++) {

				gray = orgpixels[k] & 0xff;
				graytotal += gray;
				k++;
			}
		}
		grayave = graytotal / ysize / xsize;

		// 二值化
		k = 0;
		for (i = 0; i < ysize; i++) {

			for (j = 0; j < xsize; j++) {

				gray = orgpixels[k] & 0xff;

				if (gray > grayave)
					despixels[k] = false;// white
				else
					despixels[k] = true;// black

				k++;
			}
		}
	}

	/*
	 * 对灰度图(ARGB_8888)执行平均阀值算法(滤去0和255不考虑)
	 * 
	 * 可以先调用toGrayscale从彩色图片生成灰度图 再调用该函数，将灰度图片转成2值图片
	 */
	public static void format_K_threshold(Bitmap mBitmap) {

		int graytotal = 0;
		int grayave = 128;
		int graycnt = 1;
		int gray;

		int ysize = mBitmap.getHeight();
		int xsize = mBitmap.getWidth();

		int i, j;
		for (i = 0; i < ysize; ++i) {
			for (j = 0; j < xsize; ++j) {
				gray = mBitmap.getPixel(j, i) & 0xFF;
				if (gray != 0 && gray != 255) {
					graytotal += gray;
					++graycnt;
				}
			}
		}
		grayave = graytotal / graycnt;

		// 根据前面的计算，求得一个平均阀值
		for (i = 0; i < ysize; i++) {

			for (j = 0; j < xsize; j++) {

				gray = mBitmap.getPixel(j, i) & 0xFF;

				if (gray > grayave)
					mBitmap.setPixel(j, i, Color.WHITE);
				else
					mBitmap.setPixel(j, i, Color.BLACK);
			}
		}
	}

	public static Bitmap alignBitmap(Bitmap bitmap, int wbits, int hbits,
                                     int color) {
		// 已经是对齐的，可以直接返回。
		if ((bitmap.getWidth() % wbits == 0)
				&& (bitmap.getHeight() % hbits == 0))
			return bitmap;

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int[] pixels = new int[width * height];
		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

		int newwidth = (width + wbits - 1) / wbits * wbits;
		int newheight = (height + hbits - 1) / hbits * hbits;
		int[] newpixels = new int[newwidth * newheight];
		Bitmap newbitmap = Bitmap.createBitmap(newwidth, newheight,
				Config.ARGB_8888);
		for (int i = 0; i < newheight; ++i) {
			for (int j = 0; j < newwidth; ++j) {
				if ((i < height) && (j < width))
					newpixels[i * newwidth + j] = pixels[i * width + j];
				else
					newpixels[i * newwidth + j] = color;
			}
		}
		newbitmap.setPixels(newpixels, 0, newwidth, 0, 0, newwidth, newheight);
		return newbitmap;
	}

	public static Bitmap adjustPhotoRotation(Bitmap bm,
                                             final int orientationDegree) {
		Matrix m = new Matrix();
		m.setRotate(orientationDegree, (float) bm.getWidth() / 2,
				(float) bm.getHeight() / 2);

		try {
			Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
					bm.getHeight(), m, true);
			return bm1;
		} catch (OutOfMemoryError ex) {
			// Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		}
		return null;
	}
	
	private static int PixOffset(int w, int x, int y)
	{
		return y*w + x;
	}

	// NV命令 1C 71 n [xL xH yL yH d1...dk]1...[xL xH yL yH d1...dk]n
	// 该函数返回 [xL xH yL yH d1...dk] 这些字节
	public static byte[] Image1ToNVData(int width, int height, boolean[] src)
	{
		int x = (width + 7) / 8;
		int y = (height + 7) / 8;
		int dstlen = 4 + x * y * 8;
		
		byte[] dst = new byte[dstlen];
		dst[0] = (byte)(x);
		dst[1] = (byte)(x >> 8);
		dst[2] = (byte)(y);
		dst[3] = (byte)(y >> 8);
		
		int idx = 4;
		int d = 0;
		for (int i = 0; i < width; ++i)
		{
			for (int j = 0; j < height; ++j)
			{
				int offset = PixOffset(width, i, j);
				if (j % 8 == 0) 
				{
					d = ((src[offset] ? 0x01 : 0x00) << (7 - (j % 8)));
				}
				else
				{

					d |= ((src[offset] ? 0x01 : 0x00) << (7 - (j % 8)));
				}

				if ((j % 8 == 7) || (j == height - 1))
				{
					dst[idx++] = (byte) d;
				}

			}
		}
		
		return dst;
	}
	
	// 图像转为下传位图
	public static byte[] Image1ToGSCmd(int width, int height, boolean[] src)
	{
		int x = (width + 7) / 8;
		int y = (height + 7) / 8;
		int dstlen = 4 + x * y * 8;

		byte[] dst = new byte[dstlen];
		dst[0] = 0x1d;
		dst[1] = 0x2a;
		dst[2] = (byte)(x);
		dst[3] = (byte)(y);

		int idx = 4;
		int d = 0;
		for (int i = 0; i < width; ++i)
		{
			for (int j = 0; j < height; ++j)
			{
				int offset = PixOffset(width, i, j);
				if (j % 8 == 0)
				{
					d = ((src[offset] ? 0x01 : 0x00) << (7 - (j % 8)));
				}
				else
				{

					d |= ((src[offset] ? 0x01 : 0x00) << (7 - (j % 8)));
				}

				if ((j % 8 == 7) || (j == height - 1))
				{
					dst[idx++] = (byte) d;
				}

			}
		}
		
		return dst;
	}
	
	// 图像转为光栅位图
	public static byte[] Image1ToRasterCmd(int width, int height, boolean[] src)
	{
		int x = (width + 7) / 8;
		int y = (height + 7) / 8 * 8;
		int dstlen = 8 + x * y;

		byte[] dst = new byte[dstlen];

		dst[0] = 0x1d;
		dst[1] = 0x76;
		dst[2] = 0x30;
		dst[3] = 0x00;
		dst[4] = (byte)(x % 256);
		dst[5] = (byte)(x / 256);
		dst[6] = (byte)(y % 256);
		dst[7] = (byte)(y / 256);

		int idx = 8;
		int d = 0;

		for (int j = 0; j < height; ++j)
		{
			for (int i = 0; i < width; ++i)
			{
				int offset = PixOffset(width, i, j);
				if (i % 8 == 0)
				{
					d = ((src[offset] ? 0x01 : 0x00) << (7 - (i % 8)));
				}
				else
				{
					d |= ((src[offset] ? 0x01 : 0x00) << (7 - (i % 8)));
				}

				if ((i % 8 == 7) || (i == width - 1))
				{
					dst[idx++] = (byte) d;
				}
			}
		}
		
		return dst;
	}
	
	// 图像转为光栅位图数据
	public static byte[] Image1ToRasterData(int width, int height, boolean[] src)
	{
		int x = (width + 7) / 8;
		int y = height;
		int dstlen = x * y;

		byte[] dst = new byte[dstlen];

		int idx = 0;
		int d = 0;

		for (int j = 0; j < height; ++j)
		{
			for (int i = 0; i < width; ++i)
			{
				int offset = PixOffset(width, i, j);
				if (i % 8 == 0)
				{
					d = ((src[offset] ? 0x01 : 0x00) << (7 - (i % 8)));
				}
				else
				{
					d |= ((src[offset] ? 0x01 : 0x00) << (7 - (i % 8)));
				}

				if ((i % 8 == 7) || (i == width - 1))
				{
					dst[idx++] = (byte) d;
				}
			}
		}
		
		return dst;
	}
	
	// 图像转为TM88IV光栅位图
	public static byte[] Image1ToTM88IVRasterCmd(int width, int height, boolean[] src)
	{
		int x = (width + 7) / 8 * 8;
		int y = (height + 7) / 8 * 8;
		int dstlen = 7 + 10 + 7 + 2 + x * y / 8;

		byte[] dst = new byte[dstlen];
		
		dst[0] = 0x1d;
		dst[1] = 0x38;
		dst[2] = 0x4C;
		dst[3] = (byte)((x*y / 8 + 10) & 0xFF);
		dst[4] = (byte)(((x*y / 8 + 10) >> 8) & 0xFF);
		dst[5] = (byte)(((x*y / 8 + 10) >> 16) & 0xFF);
		dst[6] = (byte)(((x*y / 8 + 10) >> 24) & 0xFF);

		dst[7] = 0x30; dst[8] = 0x70; dst[9] = 0x30; dst[10] = 0x01; dst[11] = 0x01; dst[12] = 0x31;
		dst[13] = (byte)(x % 256);
		dst[14] = (byte)(x / 256);
		dst[15] = (byte)(y % 256);
		dst[16] = (byte)(y / 256);

		byte cmdPrint[] = { 0x1d, 0x38, 0x4c, 0x02, 0x00, 0x00, 0x00, 0x30, 0x02 };
		System.arraycopy(cmdPrint, 0, dst, dstlen - cmdPrint.length, cmdPrint.length);

		int idx = 17;
		int d = 0;

		for (int j = 0; j < height; ++j)
		{
			for (int i = 0; i < width; ++i)
			{
				int offset = PixOffset(width, i, j);
				if (i % 8 == 0)
				{
					d = (byte)((src[offset] ? 0x01 : 0x00) << (7 - (i % 8)));
				}
				else
				{
					d |= (byte)((src[offset] ? 0x01 : 0x00) << (7 - (i % 8)));
				}

				if ((i % 8 == 7) || (i == width - 1))
				{
					dst[idx++] = (byte) d;
				}
			}
		}
		
		return dst;
	}
	
	// 一行图片数据转成光栅位图命令
 	public static byte[] eachLinePixToCmd(boolean[] src, int nWidth, int nMode) {

		int nHeight = src.length / nWidth;
		int nBytesPerLine = nWidth / 8;
		byte[] data = new byte[nHeight * (8 + nBytesPerLine)];
		int offset = 0;
		int k = 0;
		for (int i = 0; i < nHeight; i++) {
			offset = i * (8 + nBytesPerLine);
			data[offset + 0] = 0x1d;
			data[offset + 1] = 0x76;
			data[offset + 2] = 0x30;
			data[offset + 3] = (byte) (nMode & 0x01);
			data[offset + 4] = (byte) (nBytesPerLine % 0x100);
			data[offset + 5] = (byte) (nBytesPerLine / 0x100);
			data[offset + 6] = 0x01;
			data[offset + 7] = 0x00;
			for (int j = 0; j < nBytesPerLine; j++) {
				data[offset + 8 + j] = (byte) (
						(src[k]   ? 0x80 : 0) |
						(src[k+1] ? 0x40 : 0) |
						(src[k+2] ? 0x20 : 0) |
						(src[k+3] ? 0x10 : 0) |
						(src[k+4] ? 0x08 : 0) |
						(src[k+5] ? 0x04 : 0) |
						(src[k+6] ? 0x02 : 0) |
						(src[k+7] ? 0x01 : 0) );
				k = k + 8;
			}
		}

		return data;
	}

	public static byte[] eachLinePixToCompressCmd(boolean[] src, int nWidth) {
		
		int nHeight = src.length / nWidth;
		int nBytesPerLine = nWidth / 8;
		byte[] data = new byte[nHeight * nBytesPerLine];
		int k = 0;
		for (int i = 0; i < nHeight; i++) {
			for (int j = 0; j < nBytesPerLine; j++) {
				data[i * nBytesPerLine + j] = (byte) (
								(src[k]   ? 0x80 : 0) |
								(src[k+1] ? 0x40 : 0) |
								(src[k+2] ? 0x20 : 0) |
								(src[k+3] ? 0x10 : 0) |
								(src[k+4] ? 0x08 : 0) |
								(src[k+5] ? 0x04 : 0) |
								(src[k+6] ? 0x02 : 0) |
								(src[k+7] ? 0x01 : 0) );
				k += 8;
			}
		}

		// data是原始数据了
		int compresseddatalen = 0;
		for(int y = 0; y < nHeight; ++y)
		{
			byte[] line = new byte[nBytesPerLine];
			System.arraycopy(data, y * nBytesPerLine, line, 0, nBytesPerLine);
			byte[] buf = CompressDataBuf(line);
			byte[] cmd = new byte[] {0x1f, 0x28, 0x50, (byte)(buf.length & 0xFFl), (byte)((buf.length & 0xFFFFl) >> 8)};
			compresseddatalen += cmd.length;
			compresseddatalen += buf.length;
		}
		
		byte[] compresseddatabytes = new byte[compresseddatalen];
		int offset = 0;
		for(int y = 0; y < nHeight; ++y)
		{
			byte[] line = new byte[nBytesPerLine];
			System.arraycopy(data, y * nBytesPerLine, line, 0, nBytesPerLine);
			byte[] buf = CompressDataBuf(line);
			byte[] cmd = new byte[] {0x1f, 0x28, 0x50, (byte)(buf.length & 0xFFl), (byte)((buf.length & 0xFFFFl) >> 8)};
			
			System.arraycopy(cmd, 0, compresseddatabytes, offset, cmd.length); offset += cmd.length;
			System.arraycopy(buf, 0, compresseddatabytes, offset, buf.length); offset += buf.length;
		}
		
		return compresseddatabytes;
	}
	
	public static byte[] CompressDataBuf(byte[] src)
	{
		int srclen = src.length;
		byte[] buf = new byte[srclen * 2];
		byte ch;
		int i, cnt, idx;

		ch = src[0];
		buf[0] = ch;
		cnt = 1;
		idx = 1;
		for (i = 1; i< srclen; i++)
		{
			while (src[i] == ch)
			{
				i++;
				cnt++;
				if (i >= srclen)
					break;
			}
			if (i >= srclen)
			{
				buf[idx] = (byte) cnt;
				idx++;
				break;
			}
			buf[idx] = (byte) cnt;
			buf[idx + 1] = ch = src[i];
			cnt = 1;
			idx += 2;
		}
		if ((idx & 0x01) != 0)
		{   // 最后一个字节是单独字节
			buf[idx] = (byte) cnt;
			idx++;
		}

		if (idx >= (srclen))
		{
			byte[] dst = new byte[srclen + 1];
			dst[0] = 0;
			System.arraycopy(src, 0, dst, 1, srclen);
			return dst;
		}
		else
		{
			byte[] dst = new byte[idx + 1];
			dst[0] = (byte) idx;
			System.arraycopy(buf, 0, dst, 1, idx);
			return dst;
		}
	}
	
	
	static class TARGB32
	{
		public int b;
		public int g;
		public int r;
		public int a;
		
		public TARGB32(int argb)
		{
			a = (int) ((argb & 0xFFFFFFFFL) >> 24);
			r = (int) ((argb & 0xFFFFFFFFL) >> 16);
			g = (int) ((argb & 0xFFFFFFFFL) >> 8);
			b = (int) ((argb & 0xFFFFFFFFL) >> 0);
		}
		
		public int IntValue()
		{
			return (int)((a & 0xFFL) << 24 | (r & 0xFFL) << 16 |(g & 0xFFL) << 8 |(b & 0xFFL) << 0);
		}
	};
	static class TPicRegion
	{
		public int[] pdata;
		public int width;
		public int height;
	}
	
	public static void PicZoom_ThreeOrder0(int srcw, int srch, int [] src, int dstw, int dsth, int [] dst)
	{		
		if ((0 == dstw) || (0 == dsth) || (0 == srcw) || (0 == srch))
			return;

		if(srcw == dstw && srch == dsth)
		{
			System.arraycopy(src, 0, dst, 0, src.length);
			return;
		}
		
		double srcy,srcx;
		int y,x;
		int k = 0;
		for (y = 0; y < dsth; ++y)
		{
			srcy = (y+0.4999999)*srch/dsth - 0.5;
			for (x = 0; x<dstw; ++x)
			{
				srcx = (x+0.4999999)*srcw/dstw - 0.5;
				dst[k++] = ThreeOrder0(srcw, srch, src , srcx, srcy);
			}
		}
	}
	static int ThreeOrder0(int srcw, int srch, int [] src, double fx, double fy)
	{
		int x0 = (int)fx; if (x0>fx) --x0;    
		int y0 = (int)fy; if (y0>fy) --y0;
		double fu = fx - x0;
		double fv = fy - y0;

		TARGB32[] pixel = new TARGB32[16];

		for (int i = 0; i<4; ++i)
		{
			for (int j = 0; j<4; ++j)
			{
				long x = x0 - 1 + j;
				long y = y0 - 1 + i;
				pixel[i * 4 + j] = Pixels_Bound(srcw, srch, src, x, y);
			}
		}

		double [] afu = new double[4];
		double [] afv = new double[4];
		//
		afu[0] = SinXDivX(1 + fu);
		afu[1] = SinXDivX(fu);
		afu[2] = SinXDivX(1 - fu);
		afu[3] = SinXDivX(2 - fu);
		afv[0] = SinXDivX(1 + fv);
		afv[1] = SinXDivX(fv);
		afv[2] = SinXDivX(1 - fv);
		afv[3] = SinXDivX(2 - fv);

		float sR = 0, sG = 0, sB = 0, sA = 0;
		for (int i = 0; i<4; ++i)
		{
			float aR = 0, aG = 0, aB = 0, aA = 0;
			for (int j = 0; j<4; ++j)
			{
				aA += afu[j] * pixel[i * 4 + j].a;
				aR += afu[j] * pixel[i * 4 + j].r;
				aG += afu[j] * pixel[i * 4 + j].g;
				aB += afu[j] * pixel[i * 4 + j].b;
			}
			sA += aA*afv[i];
			sR += aR*afv[i];
			sG += aG*afv[i];
			sB += aB*afv[i];
		}

		byte a = (byte) border_color((long)(sA + 0.5));
		byte r = (byte) border_color((long)(sR + 0.5));
		byte g = (byte) border_color((long)(sG + 0.5));
		byte b = (byte) border_color((long)(sB + 0.5));
		
		return (int) ((a & 0xFFL) << 24 | (r & 0xFFL) << 16 | (g & 0xFFL) << 8 | (b & 0xFFL) << 0);
	}
	static TARGB32 Pixels_Bound(int srcw, int srch, int [] src, long x, long y)
	{
		boolean IsInPic = true;
		if (x < 0) { x = 0; IsInPic = false; }
		else if (x >= (long)srcw) { x = srcw - 1; IsInPic = false; }
		if (y < 0) { y = 0; IsInPic = false; }
		else if (y >= (long)srch) { y = srch - 1; IsInPic = false; }
		TARGB32 result = Pixels(srcw, srch, src, x, y);
		if (!IsInPic) result.a = 0;
		return result;
	}
	static TARGB32 Pixels(int srcw, int srch, int [] src, long x, long y)
	{
		int pixel = src[(int) (y * srcw + x)];
		return new TARGB32(pixel);
	}
	//三次卷积
	static double SinXDivX(double x)
	{
		//该函数计算插值曲线sin(x*PI)/(x*PI)的值 //PI=3.1415926535897932385; 
		//下面是它的近似拟合表达式
		final float a = -1; //a还可以取 a=-2,-1,-0.75,-0.5等等，起到调节锐化或模糊程度的作用

		if (x<0) x = -x; //x=abs(x);
		double x2 = x*x;
		double x3 = x2*x;
		if (x <= 1)
			return (a + 2)*x3 - (a + 3)*x2 + 1;
		else if (x <= 2)
			return a*x3 - (5 * a)*x2 + (8 * a)*x - (4 * a);
		else
			return 0;
	}
	static long border_color(long Color)
	{
		if (Color <= 0)
			return 0;
		else if (Color >= 255)
			return 255;
		else
			return Color;
	}

	// 将rgb数据转为gray数据
	public static byte[] GrayImage(int [] src)
	{
		int srclen = src.length;
		byte[] dst = new byte[srclen];
		
		int k;
		for(k = 0; k < srclen; ++k)
		{
			dst[k] = (byte) ( (((src[k] & 0xFF0000L) >> 16)*19595 + ((src[k] & 0xFF00L) >> 8)*38469 + ((src[k] & 0xFFL) >> 0)*7472) >> 16 );
		}
		
		return dst;
	}
	
	public static void ReverseBitmap(int srcw, int srch, int [] src)
	{
		int srclen = src.length;
		for(int i = 0; i < srclen; ++i)
		{
			src[i] = (int) ((0xFFL << 24) | ~(src[i] & 0xFFFFFFL));
		}
	}
	
}



