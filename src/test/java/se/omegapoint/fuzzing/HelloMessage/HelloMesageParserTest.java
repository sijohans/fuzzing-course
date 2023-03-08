package se.omegapoint.fuzzing.HelloMessage;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HelloMesageParserTest {

    HelloMessageParser parser = new HelloMessageParserImpl1();

    @Test
    public void testNullByteArray() {
        assertThrows(HelloMessageFormatException.class, () -> {
            parser.parse(null);
        });
    }

    @Test
    public void bufferToSmallForAnyThing() {
        assertThrows(HelloMessageFormatException.class, () -> {
            parser.parse(new byte[7]);
        });
    }

    @Test
    public void testEmptyNameAndKey() throws DecoderException, HelloMessageFormatException {
        byte[] data = Hex.decodeHex("0000000000000000");
        /* Public key must be in range [32, 4096] */
        assertThrows(HelloMessageFormatException.class, () -> {
            HelloMessage message = parser.parse(data);
        });

    }

    @Test
    public void testSanity() throws DecoderException, HelloMessageFormatException {
        byte[] data = Hex.decodeHex(
                "05000000" +
                        "416c696365" +
                        "20000000" +
                        "000102030405060708090A0B0C0D0E0F" +
                        "101112131415161718191a1b1c1d1e1f");
        HelloMessage message = parser.parse(data);
        assertNotNull(message);
        assertEquals(5, message.getName().length());
        assertEquals("Alice", message.getName());
        byte[] publicKey = message.getPublicKey();
        assertEquals(32, publicKey.length);
        for (int i = 0; i < 32; i++) {
            assertEquals(i, publicKey[i]);
        }
    }

}
