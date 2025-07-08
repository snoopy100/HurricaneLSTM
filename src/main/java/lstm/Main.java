package lstm;

import org.datavec.api.records.reader.SequenceRecordReader;
import org.datavec.api.split.CollectionInputSplit;
import org.datavec.api.split.NumberedFileInputSplit;
import org.datavec.api.records.reader.impl.csv.CSVSequenceRecordReader;
import org.deeplearning4j.datasets.datavec.SequenceRecordReaderDataSetIterator;
import org.deeplearning4j.datasets.iterator.AbstractDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        MultiLayerNetwork model = getModel();
        model.init();
        model.setListeners(new ScoreIterationListener(10));

        DataSetIterator baseTrainIter = getIterator("src/main/resources/train/features/", "src/main/resources/train/labels/");
        DataSetIterator baseTestIter = getIterator("src/main/resources/test/features/", "src/main/resources/test/labels/");

        DataSetIterator trainIter = maskedIterator(baseTrainIter);
        DataSetIterator testIter  = maskedIterator(baseTestIter);

        NormalizerMinMaxScaler scaler = new NormalizerMinMaxScaler(0, 1);
        scaler.fit(trainIter);  // collect training statistics
        trainIter.setPreProcessor(scaler);
        testIter.setPreProcessor(scaler);

        int epoch = 0;
        double testAccuracy = 0.0;

        while (testAccuracy < 0.95) {
            while(trainIter.hasNext()) {
                DataSet ds = trainIter.next();
                ds = mask(ds);
                model.fit(ds);
            }
            Evaluation eval = model.evaluate(testIter);
            testAccuracy = eval.accuracy();
            System.out.println("Epoch: " + epoch + " - Test Accuracy: " + testAccuracy);
            epoch++;
            trainIter.reset();
            testIter.reset();
        }
    }

    public static DataSetIterator maskedIterator(DataSetIterator baseIter) {
        return new DataSetIterator() {
            @Override
            public boolean hasNext() {
                return baseIter.hasNext();
            }

            @Override
            public DataSet next() {
                return mask(baseIter.next());
            }

            @Override
            public DataSet next(int num) {
                return mask(baseIter.next(num));
            }

            @Override
            public int inputColumns() {
                return baseIter.inputColumns();
            }

            @Override
            public int totalOutcomes() {
                return baseIter.totalOutcomes();
            }

            @Override
            public boolean resetSupported() {
                return baseIter.resetSupported();
            }

            @Override
            public boolean asyncSupported() {
                return baseIter.asyncSupported();
            }

            @Override
            public void reset() {
                baseIter.reset();
            }

            @Override
            public int batch() {
                return baseIter.batch();
            }

            @Override
            public void setPreProcessor(DataSetPreProcessor preProcessor) {
                baseIter.setPreProcessor(preProcessor);
            }

            @Override
            public DataSetPreProcessor getPreProcessor() {
                return baseIter.getPreProcessor();
            }

            @Override
            public List<String> getLabels() {
                return baseIter.getLabels();
            }

            @Override
            public void remove() {
                baseIter.remove();
            }
        };
    }


    public static DataSetIterator getIterator(String featurePath, String labelPath) throws Exception {
        File[] labelFiles = new File(labelPath).listFiles();
        File[] featureFiles = new File(featurePath).listFiles();
        List<URI> featureURIs = Arrays.stream(featureFiles).map(File::toURI).toList();
        List<URI> labelURIs = Arrays.stream(labelFiles).map(File::toURI).toList();

        SequenceRecordReader featureReader = new CSVSequenceRecordReader();
        featureReader.initialize(new CollectionInputSplit(featureURIs));

        SequenceRecordReader labelReader = new CSVSequenceRecordReader();
        labelReader.initialize(new CollectionInputSplit(labelURIs));

        return new SequenceRecordReaderDataSetIterator(
                featureReader, labelReader,
                1,                      // miniBatchSize
                -1,                     // regression (use -1 for regression, 0+ for classification)
                true,                   // regression = true
                SequenceRecordReaderDataSetIterator.AlignmentMode.ALIGN_END
        );
    }

    public static MultiLayerNetwork getModel() {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .updater(new org.nd4j.linalg.learning.config.Adam())
                .list()
                .layer(new LSTM.Builder()
                        .nIn(5) // Number of input features (set this to match your telemetry vector size)
                        .nOut(64)
                        .activation(Activation.TANH)
                        .build())
                .layer(new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nOut(5) // Number of output features
                        .build())
                .build();

        return new MultiLayerNetwork(conf);
    }

    private static DataSet mask(DataSet ds) {
        INDArray features = ds.getFeatures(); // Shape: [minibatch, features, timesteps]
        INDArray labels = ds.getLabels();     // Shape: [minibatch, labels, timesteps]

        long batchSize = features.size(0);
        long timeSteps = features.size(2);
        long numFeatures = features.size(1);
        long numLabels = labels.size(1);

        INDArray featureMask = Nd4j.ones(batchSize, timeSteps);
        INDArray labelMask = Nd4j.ones(batchSize, timeSteps);

        for (int i = 0; i < batchSize; i++) {
            for (int t = 0; t < timeSteps; t++) {
                boolean allFeaturePadding = true;
                for (int f = 0; f < numFeatures; f++) {
                    if (features.getDouble(i, f, t) != -999) {
                        allFeaturePadding = false;
                        break;
                    }
                }

                boolean allLabelPadding = true;
                for (int l = 0; l < numLabels; l++) {
                    if (labels.getDouble(i, l, t) != -999) {
                        allLabelPadding = false;
                        break;
                    }
                }

                // If either is full of padding, mask both
                if (allFeaturePadding || allLabelPadding) {
                    featureMask.putScalar(i, t, 0.0);
                    labelMask.putScalar(i, t, 0.0);
                }
            }
        }

        ds.setFeaturesMaskArray(featureMask);
        ds.setLabelsMaskArray(labelMask);
        return ds;
    }
}
