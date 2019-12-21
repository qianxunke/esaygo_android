package com.esaygo.app.common.blutoothkit;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.Random;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 蓝牙2.0底层读写封装
 *
 * @author 彭大帅
 */
@TargetApi(Build.VERSION_CODES.ECLAIR)
public class BTPrinting extends IO implements Serializable {

    private static final String TAG = "BTPrinting";
    private static final UUID uuid = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothServerSocket mmServerSocket = null;
    private BluetoothSocket mmClientSocket = null;
    private DataInputStream is = null;
    private DataOutputStream os = null;
    private AtomicBoolean isOpened = new AtomicBoolean(false);
    private AtomicBoolean isReadyRW = new AtomicBoolean(false);
    private IOCallBack cb = null;
    private Vector<Byte> rxBuffer = new Vector<Byte>();

    private AtomicLong nIdleTime = new AtomicLong(0);

    private final ReentrantLock mCloseLocker = new ReentrantLock();

    private String address;
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)
                    || BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                if (device == null)
                    return;
                if (!device.getAddress().equalsIgnoreCase(address))
                    return;
                Close();
            }
        }

    };
    private IntentFilter filter = new IntentFilter();
    private Context context;

    private void RegisterReceiver() {
        if (!filter.hasAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED))
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        if (!filter.hasAction(BluetoothDevice.ACTION_ACL_DISCONNECTED))
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        context.registerReceiver(receiver, filter);

        Log.i(TAG, "RegisterReceiver");
    }

    private void UnregisterReceiver() {
        context.unregisterReceiver(receiver);

        Log.i(TAG, "UnregisterReceiver");
    }

    /***
     * 连接2.0蓝牙打印机
     *
     * @param BTAddress
     *            打印机蓝牙MAC地址
     * @param mContext
     *            Context
     * @return
     */
    public boolean Open(String BTAddress, Context mContext) {
        Lock();

        try {
            if (isOpened.get())
                throw new Exception("Already open");

            if (null == mContext)
                throw new Exception("Null Pointer mContext");
            context = mContext;

            if (null == BTAddress)
                throw new Exception("Null Pointer BTAddress");
            address = BTAddress;

            isReadyRW.set(false);

            BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                    .getDefaultAdapter();
            if (bluetoothAdapter == null)
                throw new Exception("Null BluetoothAdapter");

            bluetoothAdapter.cancelDiscovery();

            BluetoothDevice device = bluetoothAdapter
                    .getRemoteDevice(BTAddress);

            long timeout = 10000;
            long time = System.currentTimeMillis();
            while ((System.currentTimeMillis() - time) < timeout) {
                try {
                    mmClientSocket = device
                            .createRfcommSocketToServiceRecord(uuid);

                    try {
                        mmClientSocket.connect();
                        os = new DataOutputStream(
                                mmClientSocket.getOutputStream());
                        is = new DataInputStream(
                                mmClientSocket.getInputStream());
                        isReadyRW.set(true);
                    } catch (Exception connectException) {
                        Log.i(TAG, connectException.toString());

                        try {
                            mmClientSocket.close();
                        } catch (Exception closeException) {
                            Log.i(TAG, closeException.toString());
                        } finally {
                            mmClientSocket = null;
                            os = null;
                            is = null;
                        }

                        throw new Exception("Connect Failed");
                    }
                } catch (Exception ex) {
                    Log.i(TAG, ex.toString());
                }

                if (isReadyRW.get())
                    break;
            }

            if (isReadyRW.get()) {
                Log.v(TAG, "Connected to " + BTAddress);
                rxBuffer.clear();

                RegisterReceiver();
				
				/*
				boolean bCaysnPrinter = false;
				try
				{
					SharedPreferences records = context.getSharedPreferences(TAG, 0);
					bCaysnPrinter = records.getBoolean(BTAddress, false);
				}
				catch(Exception ex)
				{
					Log.v(TAG, ex.toString());
				}
				
				if(!bCaysnPrinter)
				{
					if(1 == PTR_CheckPrinter())
					{
						bCaysnPrinter = true;
					}
					
					if(bCaysnPrinter)
					{
						try
						{
							SharedPreferences records = context.getSharedPreferences(TAG, 0);
							SharedPreferences.Editor editor = records.edit();
							editor.putBoolean(BTAddress, bCaysnPrinter);
							editor.commit();
						}
						catch(Exception ex)
						{
							Log.v(TAG, ex.toString());
						}
					}
				}
				*/
            }

            isOpened.set(isReadyRW.get());

            if (null != cb) {
                if (isOpened.get()) {
                    cb.OnOpen();
                } else {
                    cb.OnOpenFailed();
                }
            }
        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
        } finally {
            Unlock();
        }

        return isOpened.get();
    }

    /***
     * 连接2.0蓝牙打印机（作为主模式，等待打印机主动上连）
     *
     * @param BTAddress
     *            暂不使用
     * @param timeout
     *            等待超时ms时间
     * @return
     */
    public boolean Listen(String BTAddress, int timeout, Context mContext) {
        Lock();

        try {
            if (isOpened.get())
                throw new Exception("Already open");

            if (null == mContext)
                throw new Exception("Null Pointer mContext");
            context = mContext;

            if (null == BTAddress)
                throw new Exception("Null Pointer BTAddress");
            address = BTAddress;

            isReadyRW.set(false);

            BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                    .getDefaultAdapter();
            if (bluetoothAdapter == null)
                throw new Exception("Null BluetoothAdapter");

            bluetoothAdapter.cancelDiscovery();

            mmServerSocket = bluetoothAdapter
                    .listenUsingRfcommWithServiceRecord("rfcomm", uuid);

            try {
                mmClientSocket = mmServerSocket.accept(timeout);
            } catch (Exception acceptException) {
                Log.i(TAG, acceptException.toString());
                try {
                    mmServerSocket.close();
                } catch (Exception closeException) {
                    Log.i(TAG, closeException.toString());
                } finally {
                    mmServerSocket = null;
                }

                throw new Exception("Accept Failed");
            }

            try {
                os = new DataOutputStream(mmClientSocket.getOutputStream());
                is = new DataInputStream(mmClientSocket.getInputStream());
                isReadyRW.set(true);
            } catch (Exception streamException) {
                Log.i(TAG, streamException.toString());
                try {
                    mmClientSocket.close();
                } catch (Exception closeException) {
                    Log.i(TAG, closeException.toString());
                } finally {
                    mmClientSocket = null;
                    os = null;
                    is = null;
                }

                throw new Exception("Get Stream Failed");
            }

            if (isReadyRW.get()) {
                Log.v(TAG, "Connected to " + BTAddress);
                rxBuffer.clear();

                RegisterReceiver();

                boolean bCaysnPrinter = false;
                try {
                    SharedPreferences records = context.getSharedPreferences(TAG, 0);
                    bCaysnPrinter = records.getBoolean(BTAddress, false);
                } catch (Exception ex) {
                    Log.v(TAG, ex.toString());
                }

                if (!bCaysnPrinter) {
                    if (1 == PTR_CheckPrinter()) {
                        bCaysnPrinter = true;
                    }

                    if (bCaysnPrinter) {
                        try {
                            SharedPreferences records = context.getSharedPreferences(TAG, 0);
                            SharedPreferences.Editor editor = records.edit();
                            editor.putBoolean(BTAddress, bCaysnPrinter);
                            editor.commit();
                        } catch (Exception ex) {
                            Log.v(TAG, ex.toString());
                        }
                    }
                }
            }

            isOpened.set(isReadyRW.get());

            if (null != cb) {
                if (isOpened.get()) {
                    cb.OnOpen();
                } else {
                    cb.OnOpenFailed();
                }
            }
        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
        } finally {
            Unlock();
        }

        return isOpened.get();
    }

    /***
     * 关闭连接
     */
    public void Close() {
        mCloseLocker.lock();

        try {
            try {
                if (null != mmServerSocket) {
                    mmServerSocket.close();
                }
            } catch (Exception ex) {

            } finally {

            }

            try {
                if (null != mmClientSocket) {
                    mmClientSocket.close();
                }
            } catch (Exception ex) {

            } finally {

            }

            if (!isReadyRW.get())
                throw new Exception();

            mmServerSocket = null;
            mmClientSocket = null;
            is = null;
            os = null;
            UnregisterReceiver();

            isReadyRW.set(false);

            if (!isOpened.get())
                throw new Exception();

            isOpened.set(false);

            if (null != cb) {
                cb.OnClose();
            }
        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
        } finally {
            mCloseLocker.unlock();
        }
    }

    /***
     * 发送数据包，底层自带流控
     *
     * @param buffer
     * @param offset
     * @param count
     * @return
     */
    public int Write(byte[] buffer, int offset, int count) {
        if (!isReadyRW.get())
            return -1;

        Lock();

        int nBytesWritten = 0;

        try {
            nIdleTime.set(0);

            os.write(buffer, offset, count);
            os.flush();

            nBytesWritten = count;

            nIdleTime.set(System.currentTimeMillis());
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
            Close();

            nBytesWritten = -1;
        } finally {
            Unlock();
        }

        return nBytesWritten;
    }

    /***
     * 读数据
     */
    @Override
    public int Read(byte[] buffer, int offset, int count, int timeout) {
        if (!isReadyRW.get())
            return -1;

        Lock();

        int nBytesReaded = 0;

        try {
            nIdleTime.set(0);

            long time = System.currentTimeMillis();

            while ((System.currentTimeMillis() - time) < timeout) {
                if (!isReadyRW.get())
                    throw new Exception("Not Ready For Read Write");

                if (nBytesReaded == count)
                    break;

                if (rxBuffer.size() > 0) {
                    buffer[offset + nBytesReaded] = rxBuffer.get(0);
                    rxBuffer.remove(0);
                    nBytesReaded += 1;
                } else {
                    int available = is.available();
                    if (available > 0) {
                        byte[] receive = new byte[available];
                        int nReceived = is.read(receive);
                        if (nReceived > 0) {
                            for (int i = 0; i < nReceived; ++i)
                                rxBuffer.add(receive[i]);
                        }
                    } else {
                        Thread.sleep(1);
                    }
                }
            }

            nIdleTime.set(System.currentTimeMillis());
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
            Close();

            nBytesReaded = -1;
        } finally {
            Unlock();
        }

        return nBytesReaded;
    }

    /***
     * 忽略缓冲区中的数据
     */
    @Override
    public void SkipAvailable() {
        Lock();

        try {
            rxBuffer.clear();
            is.skip(is.available());
        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
        } finally {
            Unlock();
        }
    }

    /***
     * 是否已连接
     */
    public boolean IsOpened() {
        return isOpened.get();
    }

    /***
     * 设置IO回调接口
     *
     * @param callBack
     */
    public void SetCallBack(IOCallBack callBack) {
        Lock();

        try {
            cb = callBack;
        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
        } finally {
            Unlock();
        }
    }

    /***
     * 检查打印机 -1 无返回 0 有返回，无加密 1 有返回，有加密
     *
     * @return
     */
    private int PTR_CheckEncrypt() {
        Lock();

        int result = -1;

        try {
            Random rmByte = new Random(System.currentTimeMillis());
            byte[] data = new byte[]{0x1F, 0x28, 0x63, 0x08, 0x00, 0x1b,
                    0x40, (byte) 0xd2, (byte) 0xd3, (byte) 0xd4, (byte) 0xd5,
                    0x1b, 0x40, 0x00, 0x00, 0x00, 0x00, 0x1d, 0x72, 0x01};
            for (int i = 0; i < 4; ++i) {
                data[7 + i] = (byte) rmByte.nextInt(0x9);
            }
            byte[] cmd = new byte[60 + data.length];
            System.arraycopy(data, 0, cmd, 60, data.length);
            SkipAvailable();
            if (Write(cmd, 0, cmd.length) == cmd.length) {
                byte[] rec = new byte[7];

                while (Read(rec, 0, 1, 3000) == 1) {
                    result = 0;

                    if (rec[0] == 0x63) {
                        if (Read(rec, 1, 5, 3000) == 5) {
                            if (rec[1] == 0x5F) {
                                long v1 = (data[5] & 0x0FFl) << 24
                                        | (data[6] & 0x0FFl) << 16
                                        | (data[7] & 0x0FFl) << 8
                                        | (data[8] & 0x0FFl);
                                long v2 = (data[9] & 0x0FFl) << 24
                                        | (data[10] & 0x0FFl) << 16
                                        | (data[11] & 0x0FFl) << 8
                                        | (data[12] & 0x0FFl);
                                long vadd = (v1 + v2) & 0x0FFFFFFFFl;
                                long vxor = (v1 ^ v2) & 0x0FFFFFFFFl;
                                long l1 = v1 & 0xFFFFl;
                                long h2 = (v2 >> 16) & 0x0FFFFl;

                                v1 = (l1 * l1 - h2 * h2) & 0x0FFFFFFFFl;
                                v1 = (vadd - vxor - v1) & 0x0FFFFFFFFl;

                                v2 = (rec[2] & 0x0FFl) << 24
                                        | (rec[3] & 0x0FFl) << 16
                                        | (rec[4] & 0x0FFl) << 8
                                        | (rec[5] & 0x0FFl);
                                if (v1 == v2) {
                                    result = 1;
                                }
                            }
                        }
                        break;
                    } else if ((rec[0] & 0x90) == 0) {
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
        } finally {
            Unlock();
        }

        return result;
    }

    private boolean PTR_CheckKey() {
        Lock();

        boolean result = false;

        try {
            byte[] key = "XSH-KCEC".getBytes();
            byte[] random = new byte[8];
            Random rmByte = new Random(System.currentTimeMillis());
            for (int i = 0; i < 8; ++i) {
                random[i] = (byte) rmByte.nextInt(0x9);
            }
            final int HeaderSize = 5;
            byte[] recHeader = new byte[HeaderSize];
            byte[] recData = null;
            int rec = 0;
            int recDataLen = 0;
            byte[] randomlen = new byte[2];
            randomlen[0] = (byte) (random.length & 0xff);
            randomlen[1] = (byte) ((random.length >> 8) & 0xff);
            byte[] data = ByteUtils.byteArraysToBytes(new byte[][]{
                    new byte[]{0x1f, 0x1f, 0x02}, randomlen, random,
                    new byte[]{0x1b, 0x40}});
            SkipAvailable();
            Write(data, 0, data.length);
            rec = Read(recHeader, 0, HeaderSize, 1000);
            if (rec == HeaderSize) {
                recDataLen = (recHeader[3] & 0xff)
                        + ((recHeader[4] << 8) & 0xff);
                recData = new byte[recDataLen];
                rec = Read(recData, 0, recDataLen, 1000);
                if (rec == recDataLen) {

                    byte[] encrypted = recData;
                    byte[] decrypted = new byte[encrypted.length + 1];
                    /**
                     * 对数据进行解密
                     */
                    DES2 des2 = new DES2();
                    // 初始化密钥
                    des2.yxyDES2_InitializeKey(key);
                    des2.yxyDES2_DecryptAnyLength(encrypted, decrypted,
                            encrypted.length);
                    result = ByteUtils.bytesEquals(random, 0, decrypted, 0,
                            random.length);
                }
            }
        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
        } finally {
            Unlock();
        }

        return result;
    }

    private static int nCheckFaildTimes = 0;
    private static int nMaxCheckFailedTimes = 30;

    /***
     * 检查打印机，如果不是本公司打印机，则打印提示内容
     */
    private int PTR_CheckPrinter() {
        Lock();

        int check = -1;

        try {

            for (int i = 0; i < 3; ++i) {
                // 桌面打印机加密命令
                check = PTR_CheckEncrypt();
                if (check == -1)
                    continue;
                else
                    break;
            }

            // 如果有返回，但是不支持桌面打印机加密，此处测试便携打印机加密命令
            if (check == 0) {
                // 便携打印机加密命令
                if (PTR_CheckKey())
                    check = 1;
            }

            if (check == 1) { // 如果检查成功，则归零
                nCheckFaildTimes = 0;
            } else if (check == 0) {
                // 如果有返回数据，但是加密失败，则将失败次数加1
                nCheckFaildTimes++;
            }

            if (nCheckFaildTimes >= nMaxCheckFailedTimes) {
                byte[] header = new byte[]{0x0d, 0x0a, 0x1b, 0x40, 0x1c,
                        0x26, 0x1b, 0x39, 0x01};
                byte[] txt = "----Unknow printer----\r\n"
                        .getBytes();
                byte[] cmd = new byte[header.length + txt.length];
                int offset = 0;
                System.arraycopy(header, 0, cmd, offset, header.length);
                offset += header.length;
                System.arraycopy(txt, 0, cmd, offset, txt.length);
                offset += txt.length;
                Write(cmd, 0, cmd.length);
            }
        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
        } finally {
            Unlock();
        }

        return check;
    }

}
