package se.omegapoint.fuzzing;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;

public class StupidExample {

    private static boolean checkIfBytesAreValid(byte[] input) {

        if (input == null || input.length < 7)
        {
            return false;
        }

        if (input[0] == 0xA &&
            input[1] == 0xC &&
            input[2] == 0xA &&
            input[3] == 0xB &&
            input[4] == 0xD &&
            input[5] == 0xE &&
            input[6] == 0xA &&
            input[7] == 0xD)
        {
            return true;
        }
        return false;
    }

    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        byte[] inputs = data.consumeRemainingAsBytes();
        checkIfBytesAreValid(inputs);
    }

}
