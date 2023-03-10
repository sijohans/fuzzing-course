package se.omegapoint.fuzzing.HelloMessage;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;

public class HelloMessageFuzzer {

    public static void fuzzerTestOneInput(FuzzedDataProvider data) throws Exception {
        try {
            byte[] input = data.consumeRemainingAsBytes();
            HelloMessage message = new HelloMessageParserImplementation().parse(input);
            if (message == null)
            {
                throw new Exception("Returned message must not be null");
            }
            if (message.getName().length() > 256)
            {
                throw new Exception("Message.name.length() must be in range [0, 256]");
            }
            byte[] publicKey = message.getPublicKey();
            if (publicKey.length < 32 || publicKey.length > 4096)
            {
                throw new Exception("Message.publicKey.length must be in range [32, 4096");
            }
        } catch (HelloMessageFormatException e) {
            /* ok */
        }
    }

}
