package se.omegapoint.fuzzing.HelloMessage;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.*;

import static org.junit.jupiter.api.Assertions.*;

public class HelloMesageParserTest {

    HelloMessageParser parser = new HelloMessageParserImplementation();

    @Test
    public void testNullByteArray() {
        assertThrows(HelloMessageFormatException.class, () -> parser.parse(null));
    }

    @Test
    public void bufferToSmallForAnyThing() {
        assertThrows(HelloMessageFormatException.class, () -> parser.parse(new byte[7]));
    }

    @Test
    public void testEmptyNameAndKey() throws DecoderException {
        byte[] data = Hex.decodeHex("0000000000000000");
        /* Public key must be in range [32, 4096] */
        assertThrows(HelloMessageFormatException.class, () -> parser.parse(data));

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

    @Test
    public void testStringLength256() throws DecoderException, HelloMessageFormatException {

        ByteArrayOutputStream messageByteBuffer = new ByteArrayOutputStream();

        // Write name length = 1024
        messageByteBuffer.writeBytes(new byte[] {0x00, 0x02, 0x00, 0x00});

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // 🚀
        String rocketCharacter = "\uD83D\uDE80";
        byte[] rocket = rocketCharacter.getBytes(StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 128; i++) {
            stream.writeBytes(rocket);
            sb.append(rocketCharacter);
        }

        String expectedName = sb.toString();

        byte[] rocketStringBytes = stream.toByteArray();
        assertEquals(512, rocketStringBytes.length);
        String rocketString = new String(rocketStringBytes, StandardCharsets.UTF_8);
        assertEquals(256, rocketString.length());

        // Write name full of rockets
        messageByteBuffer.writeBytes(rocketStringBytes);

        // Write public key length = 32
        messageByteBuffer.writeBytes(Hex.decodeHex("20000000"));
        messageByteBuffer.writeBytes(Hex.decodeHex("000102030405060708090A0B0C0D0E0F"));
        messageByteBuffer.writeBytes(Hex.decodeHex("101112131415161718191a1b1c1d1e1f"));

        byte[] messageBytes = messageByteBuffer.toByteArray();
        HelloMessage message = parser.parse(messageBytes);
        assertNotNull(message);

        String name = message.getName();
        assertNotNull(name);
        assertEquals(name, expectedName);

        byte[] publicKey = message.getPublicKey();
        assertNotNull(publicKey);

        assertEquals(32, publicKey.length);
        for (int i = 0; i < 32; i++) {
            assertEquals(i, publicKey[i]);
        }

    }

    @Test
    public void testInvalidUtf8() throws DecoderException {
        byte[] invalidUtf8Bytes = new byte[] {(byte) 0xE2, (byte) 0x28, (byte) 0xA1, (byte) 0xEF, (byte) 0xB8, (byte) 0x8F};
        byte[] data = Hex.decodeHex(
                "06000000" +
                        "e228a1efb88f" +
                        "20000000" +
                        "000102030405060708090A0B0C0D0E0F" +
                        "101112131415161718191a1b1c1d1e1f");
        assertThrows(HelloMessageFormatException.class, () -> parser.parse(data));
    }

    @FuzzTest
    void fuzzTest(FuzzedDataProvider data) {
        try {
            parser.parse(data.consumeRemainingAsBytes());
        } catch (HelloMessageFormatException e) {
            /* ok */
        }
    }

}
