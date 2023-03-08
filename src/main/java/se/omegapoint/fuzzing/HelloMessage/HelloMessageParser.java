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
     * - Name can be the empty string (e.g. name length is 0)
     * - Name maximum size is 256
     * - The name is encoded in UTF-8
     * - Public key length must be in range [32, 4096]
     *
     */
    HelloMessage parse(byte[] data) throws HelloMessageFormatException;
}
