package dataProcessing;

public class Main {
    public static void main(String[] args) throws Exception{
        Hurdat2Splitter.main(args);
        Pad.main(args);
        RemoveEmptyString.main(args);
        HurdatCodeReplacer.main(args);
        LetterEncode.main(args);
        FeatureLabelMaker.main(args);
    }
}