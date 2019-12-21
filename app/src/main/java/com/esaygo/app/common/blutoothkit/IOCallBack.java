package com.esaygo.app.common.blutoothkit;

/***
 * 底层连接打开或关闭的回调接口 
 * 		FOR：BLEPrinting，BTPrinting，NETPrinting，USBPrinting。
 * 		Open成功，会调用OnOpen。
 * 		主动断开或者异常断开，会调用OnClose。
 * @author 彭大帅
 *
 */
public interface IOCallBack {
	/***
	 * Open成功后的回调函数
	 */
	void OnOpen();
	
	/***
	 * Open失敗后的回调函数
	 */
	void OnOpenFailed();
	
	/***
	 * Close或异常断开后的回调函数
	 */
	void OnClose();
	
}
