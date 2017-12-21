package hadoop;

import java.io.IOException;  
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.mapreduce.lib.input.*;

public class CustomRecordReader extends RecordReader<LongWritable, Text>{

    private FileSplit fileSplit;
    private Configuration conf;
    private boolean processed = false;

    private LongWritable key = new LongWritable(0);
    private Text value = new Text();

    @Override
    public void initialize(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
        this.fileSplit = (FileSplit) split;
        this.conf = context.getConfiguration();
    }

    @Override
    public boolean nextKeyValue() throws IOException, InterruptedException {
        if (!processed) {
            byte[] contents = new byte[(int) fileSplit.getLength()];

            Path filePath = fileSplit.getPath();
            FileSystem fileSystem = filePath.getFileSystem(conf);

            FSDataInputStream inputStream = null;
            try {
                inputStream = fileSystem.open(filePath);
                IOUtils.readFully(inputStream, contents, 0, contents.length);
                value.set(contents, 0, contents.length);
            } finally {
                IOUtils.closeStream(inputStream);
            }
            processed = true;
            return true;
        }
        return false;
    }

    @Override
    public LongWritable getCurrentKey() throws IOException, InterruptedException {
        return key;
    }

    @Override
    public Text getCurrentValue() throws IOException, InterruptedException {
        return value;
    }

    @Override
    public float getProgress() throws IOException, InterruptedException {
        return processed ? 1 : 0;
    }

    @Override
    public void close() throws IOException {

    }
}
