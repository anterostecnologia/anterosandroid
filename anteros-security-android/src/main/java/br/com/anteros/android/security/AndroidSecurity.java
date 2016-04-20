package br.com.anteros.android.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import br.com.anteros.core.log.Logger;
import br.com.anteros.core.log.LoggerProvider;

public class AndroidSecurity {

	private static Logger LOG = LoggerProvider.getInstance().getLogger(AndroidSecurity.class.getName());

	public static String getDeviceID(Context context) {
		TelephonyManager TelephonyMgr = (TelephonyManager) context.getApplicationContext().getSystemService(
				Context.TELEPHONY_SERVICE);
		String m_szImei = TelephonyMgr.getDeviceId();
		String m_szDevIDShort = "35" + Build.BOARD.length() % 10 + Build.BRAND.length() % 10 + Build.CPU_ABI.length()
				% 10 + Build.DEVICE.length()
				% 10 + Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 + Build.ID.length() % 10
				+ Build.MANUFACTURER.length() % 10
				+ Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 + Build.TAGS.length() % 10
				+ Build.TYPE.length() % 10 + Build.USER.length()
				% 10;
		String m_szAndroidID = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		String m_szLongID = m_szImei + m_szDevIDShort + m_szAndroidID;
		LOG.debug("m_szLongID " + m_szLongID);
		MessageDigest m = null;
		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
		byte p_md5Data[] = m.digest();

		String m_szUniqueID = new String();
		for (int i = 0; i < p_md5Data.length; i++) {
			int b = (0xFF & p_md5Data[i]);
			if (b <= 0xF)
				m_szUniqueID += "0";
			m_szUniqueID += Integer.toHexString(b);
		}
		m_szUniqueID = m_szUniqueID.toUpperCase();
		return m_szUniqueID;
	}
}
