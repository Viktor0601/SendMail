package de.vkeimes.sendmail.util;

import java.util.Vector;

public class PasswordService {

//	public static void main(String[] args) {
//		System.out.println(	"Verschlüsselung von KLARTEXT -> " + encryptPasswort("KLARTEXT") );
//		System.out.println(	"Entschlüsselung von 4V4W4L52544P5854 -> " + decryptPasswort("4V4W4L52544P5854") );
//	}

	public static String decryptPasswort(String encryptPassword) {
		String strkompChar = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuffer sbPasswort = new StringBuffer();
		Vector<String> vectorTupel = new Vector<String>();
		for (int i = 0; i < encryptPassword.length(); i += 2) {
			String strTupel = String.valueOf(encryptPassword.charAt(i))
							+ String.valueOf(encryptPassword.charAt(i + 1));

			vectorTupel.add(strTupel);
		}
		for (int i = 0; i < vectorTupel.size(); i++) {
			String aString = (String) vectorTupel.get(i);
			int iValue = strkompChar.indexOf(aString.charAt(0)) * 36;
			iValue += strkompChar.indexOf(aString.charAt(1));
			iValue -= 100;
			sbPasswort.append((char) iValue);
		}
		return sbPasswort.toString();
	}

	public static String encryptPasswort(String strDecyrptPasswort) {
		String strkompChar = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuffer sbPasswort = new StringBuffer();
		char[] charDecryptPwd = strDecyrptPasswort.toCharArray();

		for (int iIdx = 0; iIdx < strDecyrptPasswort.length(); iIdx++) {
			int iValue = charDecryptPwd[iIdx];
			iValue += 100;

			int iValue1 = iValue / 36;
			int iValue2 = iValue - iValue1 * 36;
			sbPasswort.append(strkompChar.substring(iValue1, iValue1 + 1));
			sbPasswort.append(strkompChar.substring(iValue2, iValue2 + 1));
		}
		return sbPasswort.toString();
	}
}
