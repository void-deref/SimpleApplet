package simple.applet;

import javacard.framework.*;
import fun.libutils.Utils;

public class
SimpleApplet extends Applet
{
	private static final byte INS_DO_XOR      = 0x12;
	private static final byte INS_BUBBLE_SORT = 0x14;
	private static final byte INS_PARSE_BCD   = 0x16;

	Utils utils;

	public
	SimpleApplet()
	{
		utils = new Utils();
	}

	public static void
	install(byte[] bArray, short bOffset, byte bLength)
	{
		new SimpleApplet().register();
	}

	public void
	process(APDU apdu)
	{
		if (selectingApplet()) {
			return;
		}

		byte[] buff = apdu.getBuffer();
		byte cla    = buff[ISO7816.OFFSET_CLA];
		byte ins    = buff[ISO7816.OFFSET_INS];
		short lc    = (short)(buff[ISO7816.OFFSET_LC] & (short)0xFF);

		if (lc == 0) {
			ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
		}

		if (cla != (byte)0x80) {
			ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
		}

		apdu.setIncomingAndReceive();

		switch (ins) {
			case INS_DO_XOR: {
				utils.xor(buff, ISO7816.OFFSET_CDATA, lc);
			} break;
			case INS_BUBBLE_SORT: {
				utils.bubble_sort(buff, ISO7816.OFFSET_CDATA, lc);
			} break;
			case INS_PARSE_BCD: {
				utils.from_bcd(buff, ISO7816.OFFSET_CDATA, lc);
			} break;
			default: ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}

		apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, lc);
	}
}