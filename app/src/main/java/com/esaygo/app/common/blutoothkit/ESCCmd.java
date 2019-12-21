package com.esaygo.app.common.blutoothkit;

class ESCCmd {

    // / <summary>
    // / 复位打印机
    // / </summary>
    public byte[] ESC_ALT = { 0x1b, 0x40 };

    // / <summary>
    // / 选择页模式
    // / </summary>
    public byte[] ESC_L = { 0x1b, 0x4c };

    // / <summary>
    // / 页模式下取消打印数据
    // / </summary>
    public byte[] ESC_CAN = { 0x18 };

    // / <summary>
    // / 打印并回到标准模式（在页模式下）
    // / </summary>
    public byte[] FF = { 0x0c };

    // / <summary>
    // / 页模式下打印缓冲区所有内容
    // / 只在页模式下有效，不清除缓冲区内容
    // / </summary>
    public byte[] ESC_FF = { 0x1b, 0x0c };

    // / <summary>
    // / 选择标准模式
    // / </summary>
    public byte[] ESC_S = { 0x1b, 0x53 };

    // / <summary>
    // / 设置横向和纵向移动单位
    // / 分别将横向移动单位近似设置成1/x英寸，纵向移动单位设置成1/y英寸。
    // / 当x和y为0时，x和y被设置成默认值200。
    // / </summary>
    public byte[] GS_P_x_y = { 0x1d, 0x50, 0x00, 0x00 };

    // / <summary>
    // / 选择国际字符集，值可以为0-15。默认值为0（美国）。
    // / </summary>
    public byte[] ESC_R_n = { 0x1b, 0x52, 0x00 };

    // / <summary>
    // / 选择字符代码表，值可以为0-10,16-19。默认值为0。
    // / </summary>
    public byte[] ESC_t_n = { 0x1b, 0x74, 0x00 };

    // / <summary>
    // / 打印并换行
    // / </summary>
    public byte[] LF = { 0x0a };

    public byte[] CR = { 0x0d };

    // / <summary>
    // / 设置行间距为[n*纵向或横向移动单位]英寸
    // / </summary>
    public byte[] ESC_3_n = { 0x1b, 0x33, 0x00 };

    // / <summary>
    // / 设置字符右间距，当字符放大时，右间距也随之放大相同倍数
    // / </summary>
    public byte[] ESC_SP_n = { 0x1b, 0x20, 0x00 };

    // / <summary>
    // / 在指定的钱箱插座引脚产生设定的开启脉冲。
    // / </summary>
    public byte[] DLE_DC4_n_m_t = { 0x10, 0x14, 0x01, 0x00, 0x01 };

    // / <summary>
    // / 选择切纸模式并直接切纸，0为全切，1为半切
    // / </summary>
    public byte[] GS_V_m = { 0x1d, 0x56, 0x00 };

    // / <summary>
    // / 进纸并且半切。
    // / </summary>
    public byte[] GS_V_m_n = { 0x1d, 0x56, 0x42, 0x00 };

    // / <summary>
    // / 设置打印区域宽度，该命令仅在标准模式行首有效。
    // / 如果【左边距+打印区域宽度】超出可打印区域，则打印区域宽度为可打印区域-左边距。
    // / </summary>
    public byte[] GS_W_nL_nH = { 0x1d, 0x57, 0x76, 0x02 };

    // / <summary>
    // / 设置绝对打印位置
    // / 将当前位置设置到距离行首（nL + nH x 256）处。
    // / 如果设置位置在指定打印区域外，该命令被忽略
    // / </summary>
    public byte[] ESC_dollors_nL_nH = { 0x1b, 0x24, 0x00, 0x00 };

    /**
     * 选择对齐方式 0 左对齐 1 中间对齐 2 右对齐
     */
    public byte[] ESC_a_n = { 0x1b, 0x61, 0x00 };

    // / <summary>
    // / 选择字符大小
    // / 0-3位选择字符高度，4-7位选择字符宽度
    // / 范围为从0-7
    // / </summary>
    public byte[] GS_exclamationmark_n = { 0x1d, 0x21, 0x00 };

    // / <summary>
    // / 选择字体
    // / 0 标准ASCII字体
    // / 1 压缩ASCII字体
    // / </summary>
    public byte[] ESC_M_n = { 0x1b, 0x4d, 0x00 };

    // / <summary>
    // / 选择/取消加粗模式
    // / n的最低位为0，取消加粗模式
    // / n最低位为1，选择加粗模式
    // / 与0x01即可
    // / </summary>
    public byte[] GS_E_n = { 0x1b, 0x45, 0x00 };

    // / <summary>
    // / 选择/取消下划线模式
    // / 0 取消下划线模式
    // / 1 选择下划线模式（1点宽）
    // / 2 选择下划线模式（2点宽）
    // / </summary>
    public byte[] ESC_line_n = { 0x1b, 0x2d, 0x00 };

    // / <summary>
    // / 选择/取消倒置打印模式
    // / 0 为取消倒置打印
    // / 1 选择倒置打印
    // / </summary>
    public byte[] ESC_lbracket_n = { 0x1b, 0x7b, 0x00 };

    // / <summary>
    // / 选择/取消黑白反显打印模式
    // / n的最低位为0是，取消反显打印
    // / n的最低位为1时，选择反显打印
    // / </summary>
    public byte[] GS_B_n = { 0x1d, 0x42, 0x00 };

    // / <summary>
    // / 选择/取消顺时针旋转90度
    // / </summary>
    public byte[] ESC_V_n = { 0x1b, 0x56, 0x00 };

    // / <summary>
    // / 打印下载位图
    // / 0 正常
    // / 1 倍宽
    // / 2 倍高
    // / 3 倍宽、倍高
    // / </summary>
    public byte[] GS_backslash_m = { 0x1d, 0x2f, 0x00 };

    // / <summary>
    // / 打印NV位图
    // / 以m指定的模式打印flash中图号为n的位图
    // / 1≤n≤255
    // / </summary>
    public byte[] FS_p_n_m = { 0x1c, 0x70, 0x01, 0x00 };

    // / <summary>
    // / 选择HRI字符的打印位置
    // / 0 不打印
    // / 1 条码上方
    // / 2 条码下方
    // / 3 条码上、下方都打印
    // / </summary>
    public byte[] GS_H_n = { 0x1d, 0x48, 0x00 };

    // / <summary>
    // / 选择HRI使用字体
    // / 0 标准ASCII字体
    // / 1 压缩ASCII字体
    // / </summary>
    public byte[] GS_f_n = { 0x1d, 0x66, 0x00 };

    // / <summary>
    // / 选择条码高度
    // / 1≤n≤255
    // / 默认值 n=162
    // / </summary>
    public byte[] GS_h_n = { 0x1d, 0x68, (byte) 0xa2 };

    // / <summary>
    // / 设置条码宽度
    // / 2≤n≤6
    // / 默认值 n=3
    // / </summary>
    public byte[] GS_w_n = { 0x1d, 0x77, 0x03 };

    // / <summary>
    // / 打印条码
    // / 0x41≤m≤0x49
    // / n的取值有条码类型m决定
    // / </summary>
    public byte[] GS_k_m_n_ = { 0x1d, 0x6b, 0x41, 0x0c };

    /**
     * version: 1 <= v <= 17 error correction level: 1 <= r <= 4
     */
    public byte[] GS_k_m_v_r_nL_nH = { 0x1d, 0x6b, 0x61, 0x00, 0x02, 0x00, 0x00 };

    // / <summary>
    // / 页模式下设置打印区域
    // / 该命令在标准模式下只设置内部标志位，不影响打印
    // / </summary>
    public byte[] ESC_W_xL_xH_yL_yH_dxL_dxH_dyL_dyH = { 0x1b, 0x57, 0x00, 0x00,
            0x00, 0x00, 0x48, 0x02, (byte) 0xb0, 0x04 };

    // / <summary>
    // / 在页模式下选择打印区域方向
    // / 0≤n≤3
    // / </summary>
    public byte[] ESC_T_n = { 0x1b, 0x54, 0x00 };

    // / <summary>
    // / 页模式下设置纵向绝对位置
    // / 这条命令只有在页模式下有效
    // / </summary>
    public byte[] GS_dollors_nL_nH = { 0x1d, 0x24, 0x00, 0x00 };

    // / <summary>
    // / 页模式下设置纵向相对位置
    // / 页模式下，以当前点位参考点设置纵向移动距离
    // / 这条命令只在页模式下有效
    // / </summary>
    public byte[] GS_backslash_nL_nH = { 0x1d, 0x5c, 0x00, 0x00 };

    // / <summary>
    // / 选择/取消汉字下划线模式
    // / </summary>
    public byte[] FS_line_n = { 0x1c, 0x2d, 0x00 };

    public byte[] FS_AND = { 0x1c, 0x26 };

    public byte[] ESC_9_n = { 0x1b, 0x39, 0x01 };

    /** 设置模块类型，缺省[7]=3, 0<=n<=16 */
    public byte[] GS_leftbracket_k_pL_pH_cn_67_n = { 0x1d, 0x28, 0x6b, 0x03,
            0x00, 0x31, 0x43, 0x3 };

    /**
     * 设置QR码的水平效验误差 缺省[7]=48, 48 - 7%, 49 - 15%, 50 - 25%, 51 - %30
     */
    public byte[] GS_leftbracket_k_pL_pH_cn_69_n = { 0x1d, 0x28, 0x6b, 0x03,
            0x00, 0x31, 0x45, 0x30 };

    /**
     * 4<=pl+ph*256<=7092(0<=pl<=255,0<=ph<=28) （(pL + pH×256
     * )-3）的字节在m(d1...dk)后作为图形的数据被处理。
     */
    public byte[] GS_leftbracket_k_pL_pH_cn_80_m__d1dk = { 0x1d, 0x28, 0x6b,
            0x03, 0x00, 0x31, 0x50, 0x30 };

    /**
     * 打印二维码
     */
    public byte[] GS_leftbracket_k_pL_pH_cn_fn_m = { 0x1d, 0x28, 0x6b, 0x03,
            0x00, 0x31, 0x51, 0x30 };

}
