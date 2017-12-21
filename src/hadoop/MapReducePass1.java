package hadoop;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import apriori.Apriori;

public class MapReducePass1 {
	static int support;
	static int numberOfChunks;

	public static class Pass1Mapper extends
			Mapper<LongWritable, Text, Text, IntWritable> {

		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {

			Configuration conf = context.getConfiguration();
			String mapperValue = value.toString();
			int globalSupport = conf.getInt("GLOBAL_SUPPORT".toString(), 0);
			int noOfTotalBaskets = conf.getInt("NUMBER_OF_TOTAL_BASKETS".toString(), 0);
			Apriori apriori = new Apriori("Name", mapperValue, globalSupport,noOfTotalBaskets,context);
			apriori.run();
			try {
				apriori.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	public static class Pass1Reducer extends
			Reducer<Text, IntWritable, Text, IntWritable> {

		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int sum = 0;
			
			for (IntWritable val : values) {
		        sum += val.get();
		      }
		
			Text text = new Text(key.toString() + ":");
			context.write(text, new IntWritable(sum));
		}

	}
}