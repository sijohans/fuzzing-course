package se.omegapoint.fuzzing.JacksonCbor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;

import java.io.IOException;

public class JacksonCborFuzzer {

    public static class MyValue {
        public String name;
        public int age;
    }

    public static void parse(byte [] input) {
        ObjectMapper mapper = new ObjectMapper(new CBORFactory());

        try {
            MyValue value = mapper.readValue(input, MyValue.class);
            /*
             * Not sure about what Jackson API says about returning null?
             */
            System.out.println("name: " + value.name + ", age: " + value.age);
        } catch (IOException e) {
            /* ok */
        }

    }

    public static void main(String [] args) {
        /*
            A2               # map(2)
               63            # text(3)
                  616765     # "age"
               0A            # unsigned(10)
               64            # text(4)
                  6E616D65   # "name"
               65            # text(5)
                  416C696365 # "Alice"
            A2636167650A646E616D6565416C696365
         */
        parse(new byte[]{(byte)0xA2, 0x63, 0x61, 0x67, 0x65, 0x0A, 0x64, 0x6E, 0x61, 0x6D, 0x65, 0x65, 0x41, 0x6C, 0x69, 0x63, 0x65});
    }

    public static void fuzzerTestOneInput(byte [] input) {
        parse(input);
    }

}