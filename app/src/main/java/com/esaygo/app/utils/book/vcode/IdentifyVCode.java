package com.esaygo.app.utils.book.vcode;

import android.util.Log;

import com.esaygo.app.utils.book.bean.CheckVCodeResult;
import com.esaygo.app.utils.book.bean.VCode;
import com.esaygo.app.utils.book.utils.ConnectionUtils;
import com.esaygo.app.utils.book.utils.VCodeUtils;

import org.apache.http.impl.client.CloseableHttpClient;

import java.util.logging.Logger;

import okhttp3.OkHttpClient;


public class IdentifyVCode {
    private static Logger logger = Logger.getLogger(VCodeUtils.class.getName());


    public static CheckVCodeResult autoIdentifyVCode(OkHttpClient okClient) {
        VCode vcode = VCodeUtils.getVCode(okClient);
        CheckVCodeResult checkResult = new CheckVCodeResult();
        if (vcode.getVcode() == null || vcode.getVcode().isEmpty()) {
            logger.info("验证码获取失败，正在重新获取验证码...");
            checkResult.setGetCode(false);
            return checkResult;
        }
        checkResult.setGetCode(true);
        String VCodeStrBase64 = vcode.getVcode();
        String result = VCodeUtils.markVCode(VCodeStrBase64);
        if (result == null || result.isEmpty()) {
            logger.info("验证码识别失败，正在重新获取验证码...");
            checkResult.setLocalCode(false);
            return checkResult;

        }
        checkResult.setLocalCode(true);
        okClient = VCodeUtils.checkVCode(vcode, result);
        if (okClient == null) {
            checkResult.setCheckCode(false);
            return checkResult;
        }
        checkResult.setCheckCode(true);
        checkResult.setCheckVCode(result);
        checkResult.setOkHttpClient(okClient);
        return checkResult;
    }
}
