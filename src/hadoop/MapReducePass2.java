package hadoop;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class MapReducePass2 {
	public static class Pass2Mapper extends
			Mapper<LongWritable, Text, Text, IntWritable> {

		@Override
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {

			String frequentPairsData = value.toString();
			String[] frequentPair = frequentPairsData.split(":");

			context.write(new Text(frequentPair[0]),
					new IntWritable(Integer.parseInt(frequentPair[1].trim())));

		}

	}

	public static class Pass2Reducer extends
			Reducer<Text, IntWritable, Text, IntWritable> {

		@Override
		public void reduce(Text key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {

			Configuration conf = context.getConfiguration();
			int globalSupport = conf.getInt("GLOBAL_SUPPORT".toString(), 0);

			int sum = 0;
			for(IntWritable freq: values){
				sum+=freq.get();
			}
			
			if (sum >= globalSupport) {
				context.write(new Text(key.toString().replace("[","{").replace("]","}") +" :"), new IntWritable(sum));
			}

		}

	}
}
