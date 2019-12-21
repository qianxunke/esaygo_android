package com.esaygo.app.common.blutoothkit;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.Serializable;

/**
 * ESC/POS打印指令封装
 * For 热敏打印机，标签打印机
 * 连接成功之后，调用此处的函数，即可进行打印。 该处函数对指令集上的指令做了一些封装，简化了使用流程。
 *
 * @author 彭大帅
 */
public class Pos implements Serializable {

    private static final String TAG = "Pos";

    private IO IO = new IO();

    private ESCCmd Cmd = new ESCCmd();

    public void Set(IO io) {
        if (null != io) {
            IO = io;
        }
    }

    public IO GetIO() {
        return IO;
    }

    /***
     * 打印图片
     *
     * @param mBitmap 位图图片。
     * @param nWidth 要打印的宽度
     * @param nBinaryAlgorithm    二值化算法	0 使用抖动算法，对彩色图片有较好的效果。 1 使用平均阀值算法，对文本类图片有较好的效果
     * @param nCompressMethod    压缩算法		0 不使用压缩算法	1 使用压缩算法
     */
    public void POS_PrintPicture(Bitmap mBitmap, int nWidth, int nBinaryAlgorithm, int nCompressMethod) {
        if (!IO.IsOpened())
            return;

        IO.Lock();

        try {
            int dstw = ((nWidth + 7) / 8) * 8;
            int dsth = mBitmap.getHeight() * dstw / mBitmap.getWidth();
            int[] dst = new int[dstw * dsth];

            mBitmap = ImageProcessing.resizeImage(mBitmap, dstw, dsth);
            mBitmap.getPixels(dst, 0, dstw, 0, 0, dstw, dsth);
            byte[] gray = ImageProcessing.GrayImage(dst);

            boolean[] dithered = new boolean[dstw * dsth];
            if (nBinaryAlgorithm == 0)
                ImageProcessing.format_K_dither16x16(dstw, dsth, gray, dithered);
            else
                ImageProcessing.format_K_threshold(dstw, dsth, gray, dithered);

            byte[] data = null;
            if (nCompressMethod == 0)
                data = ImageProcessing.eachLinePixToCmd(dithered, dstw, 0);
            else
                data = ImageProcessing.eachLinePixToCompressCmd(dithered, dstw);
			
			/*
			if(BTPrinting.class.getName().equals(IO.getClass().getName()))
			{
				BTPrinting bt = (BTPrinting)IO;
				bt.WriteUseXonXoff(data, 0, data.length);
			}
			else
			{
				IO.Write(data, 0, data.length);
			}
			*/
            IO.Write(data, 0, data.length);
        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
        } finally {
            IO.Unlock();
        }
    }

    /**
     * 按照一定的格式打印字符串
     *
     * @param pszString    需要打印的字符串
     * @param nOrgx        指定 X 方向（水平）的起始点位置离左边界的点数。 2寸打印机一行384点，3寸打印机一行576点。
     * @param nWidthTimes  指定字符的宽度方向上的放大倍数。可以为 0到 1。
     * @param nHeightTimes 指定字符高度方向上的放大倍数。可以为 0 到 1。
     * @param nFontType    指定字符的字体类型。 (0x00 标准 ASCII 12x24) (0x01 压缩ASCII 9x17)
     * @param nFontStyle   指定字符的字体风格。可以为以下列表中的一个或若干个。 (0x00 正常) (0x08 加粗) (0x80 1点粗的下划线)
     *                     (0x100 2点粗的下划线) (0x200 倒置、只在行首有效) (0x400 反显、黑底白字) (0x1000
     *                     每个字符顺时针旋转 90 度)
     */
    public void POS_S_TextOut(String pszString, int nOrgx,
                              int nWidthTimes, int nHeightTimes, int nFontType, int nFontStyle) {
        if (!IO.IsOpened())
            return;

        IO.Lock();

        try {
            if (nOrgx > 65535 || nOrgx < 0 || nWidthTimes > 7 || nWidthTimes < 0
                    || nHeightTimes > 7 || nHeightTimes < 0 || nFontType < 0
                    || nFontType > 4 || (pszString.length() == 0))
                throw new Exception("invalid args");

            Cmd.ESC_dollors_nL_nH[2] = (byte) (nOrgx % 0x100);
            Cmd.ESC_dollors_nL_nH[3] = (byte) (nOrgx / 0x100);

            byte[] intToWidth = {0x00, 0x10, 0x20, 0x30, 0x40, 0x50, 0x60, 0x70};
            byte[] intToHeight = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};
            Cmd.GS_exclamationmark_n[2] = (byte) (intToWidth[nWidthTimes] + intToHeight[nHeightTimes]);

            byte[] tmp_ESC_M_n = Cmd.ESC_M_n;
            if ((nFontType == 0) || (nFontType == 1))
                tmp_ESC_M_n[2] = (byte) nFontType;
            else
                tmp_ESC_M_n = new byte[0];

            // 字体风格
            // 暂不支持平滑处理
            Cmd.GS_E_n[2] = (byte) ((nFontStyle >> 3) & 0x01);

            Cmd.ESC_line_n[2] = (byte) ((nFontStyle >> 7) & 0x03);
            Cmd.FS_line_n[2] = (byte) ((nFontStyle >> 7) & 0x03);

            Cmd.ESC_lbracket_n[2] = (byte) ((nFontStyle >> 9) & 0x01);

            Cmd.GS_B_n[2] = (byte) ((nFontStyle >> 10) & 0x01);

            Cmd.ESC_V_n[2] = (byte) ((nFontStyle >> 12) & 0x01);

            Cmd.ESC_9_n[2] = 0x01;
            byte[] pbString = pszString.getBytes();

            byte[] data = byteArraysToBytes(new byte[][]{
                    Cmd.ESC_dollors_nL_nH, Cmd.GS_exclamationmark_n, tmp_ESC_M_n,
                    Cmd.GS_E_n, Cmd.ESC_line_n, Cmd.FS_line_n, Cmd.ESC_lbracket_n,
                    Cmd.GS_B_n, Cmd.ESC_V_n, Cmd.FS_AND, Cmd.ESC_9_n, pbString});

            IO.Write(data, 0, data.length);
        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
        } finally {
            IO.Unlock();
        }
    }

    /**
     * Same as POS_S_TextOut
     *
     * @param pszString
     * @param nLan         0-GBK 1-UTF8 3-BIG5 4-SHIFT-JIS 5-EUC-KR
     * @param nOrgx
     * @param nWidthTimes
     * @param nHeightTimes
     * @param nFontType
     * @param nFontStyle
     */
    public void POS_TextOut(String pszString, int nLan, int nOrgx,
                            int nWidthTimes, int nHeightTimes, int nFontType, int nFontStyle) {
        if (!IO.IsOpened())
            return;

        IO.Lock();

        try {
            if (nOrgx > 65535 || nOrgx < 0 || nWidthTimes > 7 || nWidthTimes < 0
                    || nHeightTimes > 7 || nHeightTimes < 0 || nFontType < 0
                    || nFontType > 4 || (pszString.length() == 0))
                throw new Exception("invalid args");

            Cmd.ESC_dollors_nL_nH[2] = (byte) (nOrgx % 0x100);
            Cmd.ESC_dollors_nL_nH[3] = (byte) (nOrgx / 0x100);

            byte[] intToWidth = {0x00, 0x10, 0x20, 0x30, 0x40, 0x50, 0x60, 0x70};
            byte[] intToHeight = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};
            Cmd.GS_exclamationmark_n[2] = (byte) (intToWidth[nWidthTimes] + intToHeight[nHeightTimes]);

            byte[] tmp_ESC_M_n = Cmd.ESC_M_n;
            if ((nFontType == 0) || (nFontType == 1))
                tmp_ESC_M_n[2] = (byte) nFontType;
            else
                tmp_ESC_M_n = new byte[0];

            // 字体风格
            // 暂不支持平滑处理
            Cmd.GS_E_n[2] = (byte) ((nFontStyle >> 3) & 0x01);

            Cmd.ESC_line_n[2] = (byte) ((nFontStyle >> 7) & 0x03);
            Cmd.FS_line_n[2] = (byte) ((nFontStyle >> 7) & 0x03);

            Cmd.ESC_lbracket_n[2] = (byte) ((nFontStyle >> 9) & 0x01);

            Cmd.GS_B_n[2] = (byte) ((nFontStyle >> 10) & 0x01);

            Cmd.ESC_V_n[2] = (byte) ((nFontStyle >> 12) & 0x01);

            byte[] pbString = null;
            if (nLan == 0) {
                Cmd.ESC_9_n[2] = 0;
                pbString = pszString.getBytes("GBK");
            } else if (nLan == 3) {
                Cmd.ESC_9_n[2] = 3;
                pbString = pszString.getBytes("Big5");
            } else if (nLan == 4) {
                Cmd.ESC_9_n[2] = 4;
                pbString = pszString.getBytes("Shift_JIS");
            } else if (nLan == 5) {
                Cmd.ESC_9_n[2] = 5;
                pbString = pszString.getBytes("EUC-KR");
            } else {
                Cmd.ESC_9_n[2] = 0x01;
                pbString = pszString.getBytes();
            }


            byte[] data = byteArraysToBytes(new byte[][]{
                    Cmd.ESC_dollors_nL_nH, Cmd.GS_exclamationmark_n, tmp_ESC_M_n,
                    Cmd.GS_E_n, Cmd.ESC_line_n, Cmd.FS_line_n, Cmd.ESC_lbracket_n,
                    Cmd.GS_B_n, Cmd.ESC_V_n, Cmd.FS_AND, Cmd.ESC_9_n, pbString});

            IO.Write(data, 0, data.length);
        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
        } finally {
            IO.Unlock();
        }
    }

    /**
     * 走纸一行
     */
    public void POS_FeedLine() {
        if (!IO.IsOpened())
            return;

        IO.Lock();

        try {
            byte[] data = byteArraysToBytes(new byte[][]{Cmd.CR, Cmd.LF});

            IO.Write(data, 0, data.length);

        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
        } finally {
            IO.Unlock();
        }
    }

    /**
     * 设置对齐方式
     *
     * @param align (0 左对齐) (1 居中对齐) (2 右对齐)
     */
    public void POS_S_Align(int align) {
        if (!IO.IsOpened())
            return;

        IO.Lock();

        try {
            if (align < 0 || align > 2)
                throw new Exception("invalid args");

            byte[] data = Cmd.ESC_a_n;
            data[2] = (byte) align;

            IO.Write(data, 0, data.length);

        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
        } finally {
            IO.Unlock();
        }
    }

    /**
     * 设置行高
     *
     * @param nHeight (0,255]
     */
    public void POS_SetLineHeight(int nHeight) {
        if (!IO.IsOpened())
            return;

        IO.Lock();

        try {
            if (nHeight < 0 || nHeight > 255)
                throw new Exception("invalid args");

            byte[] data = Cmd.ESC_3_n;
            data[2] = (byte) nHeight;

            IO.Write(data, 0, data.length);

        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
        } finally {
            IO.Unlock();
        }
    }

    /**
     * 打印条码 请参看附件详细说明
     *
     * @param strCodedata
     * @param nOrgx
     * @param nType
     * @param nWidthX
     * @param nHeight
     * @param nHriFontType
     * @param nHriFontPosition
     */
    public void POS_S_SetBarcode(String strCodedata, int nOrgx, int nType,
                                 int nWidthX, int nHeight, int nHriFontType, int nHriFontPosition) {
        if (!IO.IsOpened())
            return;

        IO.Lock();

        try {
            if (nOrgx < 0 || nOrgx > 65535 || nType < 0x41 || nType > 0x49 || nHeight < 1 || nHeight > 255)
                throw new Exception("invalid args");

            byte[] bCodeData = strCodedata.getBytes();

            Cmd.ESC_dollors_nL_nH[2] = (byte) (nOrgx % 0x100);
            Cmd.ESC_dollors_nL_nH[3] = (byte) (nOrgx / 0x100);
            Cmd.GS_w_n[2] = (byte) nWidthX;
            Cmd.GS_h_n[2] = (byte) nHeight;
            Cmd.GS_f_n[2] = (byte) (nHriFontType & 0x01);
            Cmd.GS_H_n[2] = (byte) (nHriFontPosition & 0x03);
            Cmd.GS_k_m_n_[2] = (byte) nType;
            Cmd.GS_k_m_n_[3] = (byte) bCodeData.length;

            byte[] data = byteArraysToBytes(new byte[][]{
                    Cmd.ESC_dollors_nL_nH, Cmd.GS_w_n, Cmd.GS_h_n, Cmd.GS_f_n,
                    Cmd.GS_H_n, Cmd.GS_k_m_n_, bCodeData});

            IO.Write(data, 0, data.length);

        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
        } finally {
            IO.Unlock();
        }
    }

    /**
     * 打印二维码，使用的命令非（ESC/POS）。一般适用于便携式打印机。
     *
     * @param strCodedata           二维码字符串
     * @param nWidthX               二维码每个模块的单元宽度，[1,16]
     * @param nVersion              二维码版本大小，该值和二维码大小相关。 [0,16]
     *                              （设置为0自动计算二维码大小，设置为10基本占满2寸纸，如果二维码超出纸张宽度则不会打印）
     * @param nErrorCorrectionLevel 纠错等级。[1,4]
     */
    public void POS_S_SetQRcode(String strCodedata, int nWidthX, int nVersion, int nErrorCorrectionLevel) {
        if (!IO.IsOpened())
            return;

        IO.Lock();

        try {
            if (nWidthX < 1 || nWidthX > 16 || nErrorCorrectionLevel < 1 || nErrorCorrectionLevel > 4 || nVersion < 0 || nVersion > 16)
                throw new Exception("invalid args");

            byte[] bCodeData = strCodedata.getBytes();

            Cmd.GS_w_n[2] = (byte) nWidthX;
            Cmd.GS_k_m_v_r_nL_nH[3] = (byte) nVersion;
            Cmd.GS_k_m_v_r_nL_nH[4] = (byte) nErrorCorrectionLevel;
            Cmd.GS_k_m_v_r_nL_nH[5] = (byte) (bCodeData.length & 0xff);
            Cmd.GS_k_m_v_r_nL_nH[6] = (byte) ((bCodeData.length & 0xff00) >> 8);

            byte[] data = byteArraysToBytes(new byte[][]{Cmd.GS_w_n,
                    Cmd.GS_k_m_v_r_nL_nH, bCodeData});

            IO.Write(data, 0, data.length);

        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
        } finally {
            IO.Unlock();
        }
    }

    /**
     * 在一行上打印双二维码
     *
     * @param QR1Data     QR1数据
     * @param QR1Position QR1位置（2寸打印机384点宽，3寸打印机576点宽）
     * @param QR1Ecc      QR1效验 1-4
     * @param QR1Version  QR1版本（0-40，0自动选择）
     * @param QR2Data     QR2数据
     * @param QR2Position QR2位置
     * @param QR2Ecc      QR2效验 1-4
     * @param QR2Version  QR2版本（0-40,0自动选择）
     * @param ModuleSize  QR码模块宽度
     */
    public void POS_DoubleQRCode(String QR1Data, int QR1Position, int QR1Ecc, int QR1Version,
                                 String QR2Data, int QR2Position, int QR2Ecc, int QR2Version, int ModuleSize) {
        if (!IO.IsOpened())
            return;

        IO.Lock();

        try {
            byte[] head = {0x1f, 0x51, 0x02, (byte) ModuleSize};

            byte[] qr1code = QR1Data.getBytes();
            int QR1Datalen = qr1code.length;
            byte[] qr1info = {(byte) ((QR1Position & 0xff00) >> 8), (byte) (QR1Position & 0xff),
                    (byte) ((QR1Datalen & 0xff00) >> 8), (byte) (QR1Datalen & 0xff),
                    (byte) (QR1Ecc), (byte) (QR1Version)};

            byte[] qr2code = QR2Data.getBytes();
            int QR2Datalen = qr2code.length;
            byte[] qr2info = {(byte) ((QR2Position & 0xff00) >> 8), (byte) (QR2Position & 0xff),
                    (byte) ((QR2Datalen & 0xff00) >> 8), (byte) (QR2Datalen & 0xff),
                    (byte) (QR2Ecc), (byte) (QR2Version)};

            byte[] data = byteArraysToBytes(new byte[][]{head, qr1info, qr1code, qr2info, qr2code});

            IO.Write(data, 0, data.length);
        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
        } finally {
            IO.Unlock();
        }
    }

    /**
     * 使用ESC/POS命令打印二维码。一般适用于嵌入式打印机。
     *
     * @param strCodedata           二维码字符串
     * @param nWidthX               二维码每个模块的单元宽度 [1, 16]
     * @param nErrorCorrectionLevel 纠错等级。[1,4]
     */
    public void POS_EPSON_SetQRCode(String strCodedata, int nWidthX, int nErrorCorrectionLevel) {
        if (!IO.IsOpened())
            return;

        IO.Lock();

        try {
            if (nWidthX < 1 || nWidthX > 16 || nErrorCorrectionLevel < 1 || nErrorCorrectionLevel > 4)
                throw new Exception("invalid args");

            byte[] bCodeData = strCodedata.getBytes();

            Cmd.GS_leftbracket_k_pL_pH_cn_67_n[7] = (byte) nWidthX;
            Cmd.GS_leftbracket_k_pL_pH_cn_69_n[7] = (byte) (47 + nErrorCorrectionLevel);
            Cmd.GS_leftbracket_k_pL_pH_cn_80_m__d1dk[3] = (byte) ((bCodeData.length + 3) & 0xff);
            Cmd.GS_leftbracket_k_pL_pH_cn_80_m__d1dk[4] = (byte) (((bCodeData.length + 3) & 0xff00) >> 8);

            byte[] data = byteArraysToBytes(new byte[][]{
                    Cmd.GS_leftbracket_k_pL_pH_cn_67_n,
                    Cmd.GS_leftbracket_k_pL_pH_cn_69_n,
                    Cmd.GS_leftbracket_k_pL_pH_cn_80_m__d1dk, bCodeData,
                    Cmd.GS_leftbracket_k_pL_pH_cn_fn_m});

            IO.Write(data, 0, data.length);

        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
        } finally {
            IO.Unlock();
        }
    }

    /**
     * 复位打印机（软件复位）
     */
    public void POS_Reset() {
        if (!IO.IsOpened())
            return;

        IO.Lock();

        try {
            byte[] data = Cmd.ESC_ALT;

            IO.Write(data, 0, data.length);

        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
        } finally {
            IO.Unlock();
        }
    }

    /**
     * 设置打印机的移动单位。
     *
     * @param nHorizontalMU 把水平方向上的移动单位设置为 25.4 / nHorizontalMU 毫米。
     * @param nVerticalMU   把垂直方向上的移动单位设置为 25.4 / nVerticalMU 毫米。
     */
    public void POS_SetMotionUnit(int nHorizontalMU, int nVerticalMU) {
        if (!IO.IsOpened())
            return;

        IO.Lock();

        try {

            if (nHorizontalMU < 0 || nHorizontalMU > 255 || nVerticalMU < 0 || nVerticalMU > 255)
                throw new Exception("invalid args");

            byte[] data = Cmd.GS_P_x_y;
            data[2] = (byte) nHorizontalMU;
            data[3] = (byte) nVerticalMU;

            IO.Write(data, 0, data.length);

        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
        } finally {
            IO.Unlock();
        }
    }

    /**
     * 选择国际字符集和代码页。 请参考附件详细说明。
     *
     * @param nCharSet  国际字符集
     * @param nCodePage 字符代码页
     */
    protected void POS_SetCharSetAndCodePage(int nCharSet, int nCodePage) {
        if (!IO.IsOpened())
            return;

        IO.Lock();

        try {
            if (nCharSet < 0 || nCharSet > 15 || nCodePage < 0 || nCodePage > 19 || (nCodePage > 10 && nCodePage < 16))
                throw new Exception("invalid args");

            Cmd.ESC_R_n[2] = (byte) nCharSet;
            Cmd.ESC_t_n[2] = (byte) nCodePage;
            byte[] data = byteArraysToBytes(new byte[][]{Cmd.ESC_R_n,
                    Cmd.ESC_t_n});

            IO.Write(data, 0, data.length);

        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
        } finally {
            IO.Unlock();
        }
    }

    /**
     * 设置字符的右间距（相邻两个字符的间隙距离）。
     *
     * @param nDistance 指定右间距的点数。
     */
    public void POS_SetRightSpacing(int nDistance) {
        if (!IO.IsOpened())
            return;

        IO.Lock();

        try {
            if (nDistance < 0 || nDistance > 255)
                throw new Exception("invalid args");

            Cmd.ESC_SP_n[2] = (byte) nDistance;
            byte[] data = Cmd.ESC_SP_n;

            IO.Write(data, 0, data.length);

        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
        } finally {
            IO.Unlock();
        }
    }

    /**
     * 设置标准模式下的打印区域宽度。
     *
     * @param nWidth 指定打印区域的宽度。
     */
    public void POS_S_SetAreaWidth(int nWidth) {
        if (!IO.IsOpened())
            return;

        IO.Lock();

        try {
            if (nWidth < 0 || nWidth > 65535)
                throw new Exception("invalid args");

            byte nL = (byte) (nWidth % 0x100);
            byte nH = (byte) (nWidth / 0x100);
            Cmd.GS_W_nL_nH[2] = nL;
            Cmd.GS_W_nL_nH[3] = nH;
            byte[] data = Cmd.GS_W_nL_nH;

            IO.Write(data, 0, data.length);
        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
        } finally {
            IO.Unlock();
        }
    }

    /***
     * 切纸
     */
    public void POS_CutPaper() {
        if (!IO.IsOpened())
            return;

        IO.Lock();

        try {
            byte[] data = new byte[]{0x1d, 0x56, 0x42, 0x00};

            IO.Write(data, 0, data.length);

        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
        } finally {
            IO.Unlock();
        }
    }

    /***
     * 蜂鸣器鸣叫
     * @param nBeepCount 鸣叫次数
     * @param nBeepMillis 每次鸣叫的时间 = 100 * nBeemMillis ms
     */
    public void POS_Beep(int nBeepCount, int nBeepMillis) {
        if (!IO.IsOpened())
            return;

        IO.Lock();

        try {
            byte[] data = new byte[]{0x1b, 0x42, (byte) nBeepCount, (byte) nBeepMillis};

            IO.Write(data, 0, data.length);

        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
        } finally {
            IO.Unlock();
        }
    }

    /***
     * 打开钱箱
     * @param nDrawerIndex 0表示：脉冲发送到钱箱输出引脚2  1表示：脉冲发送到钱箱输出引脚5
     * @param nPulseTime 脉冲时间 高电平时间：nPulseTime*2ms 低电平时间：nPulseTime*2ms
     */
    public void POS_KickDrawer(int nDrawerIndex, int nPulseTime) {
        if (!IO.IsOpened())
            return;

        IO.Lock();

        try {
            byte[] data = new byte[]{0x1b, 0x70, (byte) nDrawerIndex, (byte) nPulseTime, (byte) nPulseTime};

            IO.Write(data, 0, data.length);
        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
        } finally {
            IO.Unlock();
        }
    }

    /***
     * 设置打印速度 注：如果打印速度大于发送速度，打印会有卡顿感。
     * @param nSpeed 打印速度（mm/s）
     */
    public void POS_SetPrintSpeed(int nSpeed) {
        if (!IO.IsOpened())
            return;

        IO.Lock();

        try {
            byte[] data = new byte[]{0x1F, 0x28, 0x73, 0x02, 0x00, (byte) (nSpeed & 0xFFL), (byte) ((nSpeed & 0xFF00L) >> 8)};

            IO.Write(data, 0, data.length);
        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
        } finally {
            IO.Unlock();
        }
    }

    /***
     * 查询状态
     * 		打印机忙时，该命令会一直阻塞
     * 		返回的状态保存在status中
     * @param status status = new byte[1] 该值目前无意义
     * @param timeout 单次查询状态的超时毫秒时间
     * @param MaxRetry 失败重试次数
     * @return true表示查询到了状态，也即打印机当前不忙。
     */
    public boolean POS_QueryStatus(byte[] status, int timeout, int MaxRetry) {
        if (!IO.IsOpened())
            return false;

        IO.Lock();

        boolean result = false;
        try {
            byte cmd[] = {0x1D, 0x72, 0x01};
            while (MaxRetry-- >= 0) {
                IO.SkipAvailable();
                if (IO.Write(cmd, 0, cmd.length) == cmd.length) {
                    if (IO.Read(status, 0, 1, timeout) == 1) {
						/*
						if(((status[0] & 0xFF) & 0x90) == 0)
						{
							result = true;
							break;
						}
						*/
                        result = true;
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
        } finally {
            IO.Unlock();
        }

        return result;
    }

    /***
     * 实时状态查询
     * 		无论打印机处于何种状态，只要打印机收到该命令就立刻回送状态
     * 		返回的状态保存在status中
     * @param status status = new byte[1]
     * @param type type可取值 [1,4] 参考指令集中针对实时状态的描述
     * @param timeout 单次查询状态的超时毫秒时间
     * @param MaxRetry 失败重试次数
     * @return true表示查询到了状态，也即打印机当前通讯正常。
     */
    public boolean POS_RTQueryStatus(byte[] status, int type, int timeout, int MaxRetry) {
        if (!IO.IsOpened())
            return false;

        IO.Lock();

        boolean result = false;
        try {
            byte cmd[] = {0x10, 0x04, (byte) type};
            while (MaxRetry-- >= 0) {
                IO.SkipAvailable();
                if (IO.Write(cmd, 0, cmd.length) == cmd.length) {
                    if (IO.Read(status, 0, 1, timeout) == 1) {
                        //if(((status[0] & 0xFF) & 0x12) == 0x12)
                        {
                            result = true;
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {

        } finally {
            IO.Unlock();
        }

        return result;
    }

    // 发送单据查询命令
    //
    // 返回0x22表示单据正常
    // 返回0x33表示单据打印不完整
    // 实时状态错误表明打印不完整
    public boolean POS_TicketSucceed(int dwSendIndex, int timeout) {
        if (!IO.IsOpened())
            return false;

        IO.Lock();

        boolean result = false;
        try {
            Log.i(TAG, String.format("Get Ticket %d Result", dwSendIndex));

            byte[] recbuf = new byte[7];

            byte[] data = new byte[11];
            data[0] = 0x1D;
            data[1] = 0x28;
            data[2] = 0x48;
            data[3] = 0x06;
            data[4] = 0x00;
            data[5] = 0x30;
            data[6] = 0x30;
            data[7] = (byte) dwSendIndex;
            data[8] = (byte) (dwSendIndex >> 8);
            data[9] = (byte) (dwSendIndex >> 16);
            data[10] = (byte) (dwSendIndex >> 24);

            byte head[] = {0x10, 0x04, 0x01, 0x10, 0x04, 0x01, 0x10, 0x04, 0x01, 0x10, 0x04, 0x01,};
            byte cmd[] = new byte[head.length + data.length];
            int offset = 0;
            System.arraycopy(head, 0, cmd, offset, head.length);
            offset += head.length;
            System.arraycopy(data, 0, cmd, offset, data.length);
            offset += data.length;

            IO.SkipAvailable();
            if (IO.Write(cmd, 0, cmd.length) == cmd.length) {
                long beginTime = System.currentTimeMillis();
                while (true) {
                    if (!IO.IsOpened())
                        break;

                    if (System.currentTimeMillis() - beginTime > timeout)
                        break;

                    int nBytesReaded = IO.Read(recbuf, 0, 1, timeout);
                    if (nBytesReaded < 0)
                        break;

                    if (nBytesReaded == 1) {
                        if (recbuf[0] == 0x37) {
                            if (IO.Read(recbuf, 1, 1, timeout) == 1) {
                                if ((recbuf[1] == 0x22) ||
                                        (recbuf[1] == 0x33)) {
                                    if (IO.Read(recbuf, 2, 5, timeout) == 5) {
                                        int dwRecvIndex = (recbuf[2] & 0xFF) | (recbuf[3] & 0xFF) << 8 | (recbuf[4] & 0xFF) << 16 | (recbuf[5] & 0xFF) << 24;
                                        if (dwSendIndex == dwRecvIndex) {
                                            if (recbuf[1] == 0x22) {
                                                result = true;
                                            }
                                            Log.i(TAG, String.format("Ticket Result: %02X %02X %02X %02X %02X %02X %02X", recbuf[0], recbuf[1], recbuf[2], recbuf[3], recbuf[4], recbuf[5], recbuf[6]));
                                            break;
                                        }
                                    }
                                }
                            }
                        } else if ((recbuf[0] & 0x12) == 0x12) {
                            // 收到了实时命令，说明数据有发送过去
                            Log.i(TAG, String.format("Printer RT Status: %02X ", recbuf[0]));
                            if ((recbuf[0] & 0x08) != 0) {
                                break; // 有出错
                            }
                        }
                    }
                }
            }

            Log.i(TAG, String.format("Ticket %d %s", dwSendIndex, result ? "Succeed" : "Failed"));
        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
        } finally {
            IO.Unlock();
        }

        return result;
    }

    private byte[] byteArraysToBytes(byte[][] data) {
        int length = 0;
        for (int i = 0; i < data.length; i++)
            length += data[i].length;
        byte[] send = new byte[length];
        int k = 0;
        for (int i = 0; i < data.length; i++)
            for (int j = 0; j < data[i].length; j++)
                send[k++] = data[i][j];
        return send;
    }

}

