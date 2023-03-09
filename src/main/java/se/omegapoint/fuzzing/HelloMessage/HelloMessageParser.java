package se.omegapoint.fuzzing.HelloMessage;

public interface HelloMessageParser {
    /*
     * Parse a binary message with the format:
     *  { nameSize[4] || name[nameSize] || publicKeySize[4] || publicKey[publicKeySize] }
     *
     * Size is serialized as an 32-bit integer in little endian. E.g.:
     *  size[4] = { 0x01, 0x00, 0x00, 0x00 } => size = 1
     *  size[4] = { 0x00, 0x01, 0x00, 0x00 } => size = 256
     *  size[4] = { 0x01, 0x01, 0x00, 0x00 } => size = 257
     *  size[4] = { 0xff, 0xff, 0x00, 0x00 } => size = 65535
     *  size[4] = { 0xf4, 0xf6, 0x01, 0x00 } => size = 128756
     *
     *
     * Requirements:
     *  - The interface must handle null input
     *  - The interface must never return null
     *  - The field name and publicKey of the returned HelloMessage must not be null
     *  - Name can be the empty string (e.g. name length is 0)
     *  - Name maximum size is 256 (when invoking message.getName().length())
     *  - The name is encoded in UTF-8 (It is ok to not verify this)
     *  - Public key length must be in range [32, 4096]
     *
     *  Example data in hex format with comments:
     *
     *      05000000    -> name length = 5
     *      416c696365  -> Alice
     *      20000000    -> Public key length = 32
     *      000102030405060708090A0B0C0D0E0F {0, 1,   ..., 15}
     *      101112131415161718191a1b1c1d1e1f {16, 17, ..., 31}
     *
     */
    HelloMessage parse(byte[] data) throws HelloMessageFormatException;
}
