package hadoop;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

public class CustomTextInputFormat extends TextInputFormat {
	@Override  
	protected boolean isSplitable(JobContext context, Path file){    
	  return false;  
	} 
    @Override
    public RecordReader<LongWritable, Text> createRecordReader(InputSplit split, TaskAttemptContext context) {
       return new CustomRecordReader();
    }
}