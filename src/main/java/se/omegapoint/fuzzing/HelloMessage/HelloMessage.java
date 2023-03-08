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
