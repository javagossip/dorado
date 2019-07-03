/**
 * 
 */
package ai.houyi.dorado.rest.util;

/**
 * @author wangweiping
 *
 */
public final class NetUtils {
	private final static int INADDR4SZ = 4;

	public static boolean isInternalIp(String ip) {
		if (ip == null)
			return true;
		if (ip.equals("127.0.0.1"))
			return true;
		byte[] addr = textToNumericFormatV4(ip);

		final byte b0 = addr[0];
		final byte b1 = addr[1];
		// 10.x.x.x/8
		final byte SECTION_1 = 0x0A;
		// 172.16.x.x/12
		final byte SECTION_2 = (byte) 0xAC;
		final byte SECTION_3 = (byte) 0x10;
		final byte SECTION_4 = (byte) 0x1F;
		// 192.168.x.x/16
		final byte SECTION_5 = (byte) 0xC0;
		final byte SECTION_6 = (byte) 0xA8;
		switch (b0) {
		case SECTION_1:
			return true;
		case SECTION_2:
			if (b1 >= SECTION_3 && b1 <= SECTION_4) {
				return true;
			}
		case SECTION_5:
			switch (b1) {
			case SECTION_6:
				return true;
			}
		default:
			return false;
		}
	}

	public static byte[] textToNumericFormatV4(String src) {
		if (src.length() == 0) {
			return null;
		}

		byte[] res = new byte[INADDR4SZ];
		String[] s = src.split("\\.", -1);
		long val;

		for (int i = 0; i < 4; i++) {
			val = Integer.parseInt(s[i]);
			if (val < 0 || val > 0xff)
				return null;
			res[i] = (byte) (val & 0xff);
		}

		return res;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(NetUtils.isInternalIp("10.200.136.128"));
	}
}
