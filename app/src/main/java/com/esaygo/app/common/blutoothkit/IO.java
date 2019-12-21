package com.esaygo.app.common.blutoothkit;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 连接上层函数和底层函数。 
 * 		具体表现为：BLEPrinting，BTPrinting，NETPrinting，USBPrinting各自继承IO，并重写IO的三个函数IsOpened，Write，Read。
 * 		使用IO的类Pos和Lable1持有IO的一个对象，这样形成了底层连接和上层逻辑分离的效果。
 * 注意：连接成功之后，IO会启动心跳。
 * 	
 * @author Administrator
 * 
 */
public class IO {


	private final ReentrantLock locker = new ReentrantLock();
	
	/***
	 * 写数据
	 * @param buffer
	 * @param offset
	 * @param count
	 * @return 返回写入的字节数。-1表示写出错（异常）。
	 */
	public int Write(byte[] buffer, int offset, int count) {
		return -1;
	}

	/***
	 * 读数据
	 * @param buffer
	 * @param offset
	 * @param count
	 * @param timeout
	 * @return 返回读入的字节数。-1表示读出错（异常）。
	 */
	public int Read(byte[] buffer, int offset, int count, int timeout) {
		return -1;
	}
	
	/***
	 * 忽略缓冲区中的数据
	 */
	public void SkipAvailable() {
		
	}
	
	/***
	 * 是否已连接
	 */
	public boolean IsOpened() {
		return false;
	}
	
	/***
	 * 锁定IO操作
	 */
	protected void Lock() {
		locker.lock();
	}
	
	/***
	 * 解锁IO操作
	 */
	protected void Unlock() {
		locker.unlock();
	}
	
}

