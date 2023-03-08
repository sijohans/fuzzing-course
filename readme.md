# Fuzzing course
Fuzzing with Jazzer is very easy. You just need to implement one method:
```java
public class MyFuzzer {
   public static void fuzzerTestOneInput(byte[] input) {
      // Call your code here
   }   
}
```

## Fuzzing with Jazzer
1. Download jazzer v0.15.0 from https://github.com/CodeIntelligenceTesting/jazzer/releases/tag/v0.15.0
   1. [Linux](https://github.com/CodeIntelligenceTesting/jazzer/releases/download/v0.15.0/jazzer-linux.tar.gz)
   2. [MacOS](https://github.com/CodeIntelligenceTesting/jazzer/releases/download/v0.15.0/jazzer-macos.tar.gz)
   3. [Windows](https://github.com/CodeIntelligenceTesting/jazzer/releases/download/v0.15.0/jazzer-windows.tar.gz)
2. Extract it, e.g.:
```shell
$ mkdir workspace
$ cd workspace
$ wget https://github.com/CodeIntelligenceTesting/jazzer/releases/download/v0.15.0/jazzer-macos.tar.gz
$ tar -xvf jazzer-macos.tar.gz
$ ls -l
total 37296
-rwxr-xr-x  1 sijohans  staff   658463 Jan  1  2000 jazzer
-rw-r--r--  1 sijohans  staff  8862078 Feb  2 13:05 jazzer-macos.tar.gz
-rwxr-xr-x  1 sijohans  staff  9569676 Jan  1  2000 jazzer_standalone.jar
```
3. Build this Java project
```shell
$ mvn clean compile assembly:single
$ ls target                        
archive-tmp                           fuzzing-1.0-jar-with-dependencies.jar maven-status
classes                               generated-sources
```
4. Fuzz your target
```shell
$ mkdir output
$ ./jazzer --cp=/path/to/fuzzing-1.0-jar-with-dependencies.jar --target_class=se.omegapoint.fuzzing.HelloMessage.HelloMessageFuzzer output
```
## Examples
These examples and code assumes that you have extracted jazzer in a folder called **workarea** in the root of this project.
### Fuzzing Jackson CBOR library
CBOR is a binary format with properties similar to Json. Jackson can be used to serialize and deserialize CBOR data similar as Jackson is used to parse Json.

How many different bugs can you find in this version of Jackson Cbor?
```
# Build
mvn clean compile assembly:single
cd workarea
mkdir output_cbor
./jazzer --cp=../target/fuzzing-1.0-jar-with-dependencies.jar --target_class=se.omegapoint.fuzzing.JacksonCbor.JacksonCborFuzzer output_cbor
```

### Fuzzing JJwt
JJwt is a library for parsing and verifying Json Web Tokens (JWT). This is the latest version of the library.

How many different bugs can you find in this version of JJwt?
```
# Build
mvn clean compile assembly:single
cd workarea
mkdir output_jjwt
./jazzer --cp=../target/fuzzing-1.0-jar-with-dependencies.jar --target_class=se.omegapoint.fuzzing.JJwt.JJwtFuzzer output_jjwt
```

## Code and fuzz your own code
Your task is to implement a parser and fuzz test it. In the **HelloMessage** folder there is an interface for you to implement. The parser creates and populates a HelloMessage:
```java
package se.omegapoint.fuzzing.HelloMessage;

public class HelloMessage {

    private final String name;
    private final byte[] publicKey;

    public HelloMessage(String name, byte[] publicKey) {
        this.name = name;
        this.publicKey = publicKey;
    }

    String getName() {
        return name;
    }

    byte[] getPublicKey() {
        return publicKey;
    }
}

```

The specification of the format to parse and how the interface should behave is specified in the file:
```java
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
     *  - Name maximum size is 256
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

```
A minimal set of unit tests have been written that can be useful during implementation.

### Hints
<details>
<summary>Show more</summary>

```java
// One way of parsing an integer from byte[]
public static final int bytesToIntLE(byte[] arr, int offset) {
   int i = offset;
   int result = (arr[i++] & 0x00ff);
   result |= (arr[i++] & 0x00ff) << 8;
   result |= (arr[i++] & 0x00ff) << 16;
   result |= (arr[i] & 0x00ff) << 24;
   return result;
}
// Another way
public static void main(String[] args) {
   byte[] bytes = new byte[]{ 0x01, 0x00, 0x00, 0x00 };
   int x = java.nio.ByteBuffer.wrap(bytes, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
   System.out.println(x);
}
```
</details>