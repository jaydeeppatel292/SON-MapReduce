package main;

import hadoop.CustomPartitioner;
import hadoop.CustomTextInputFormat;
import hadoop.HadoopConf;
import hadoop.MapReducePass1;
import hadoop.MapReducePass2;

import java.io.File;
import java.io.IOException;

import model.FileManagerResponse;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Job;

import utils.FileManager;

public class Executer {

	public static void main(String[] args) throws IOException,
			ClassNotFoundException, InterruptedException {

		long startTime = System.currentTimeMillis();
		
		// delete output folder
		FileUtils.deleteDirectory(new File(args[1]));

		// delete mapper 1 output folder
		FileUtils.deleteDirectory(new File("mapper1Output"));

		// delete intermediate split files if exists
		FileUtils.deleteDirectory(new File("splits"));

		// Create chunks from big input file
		FileManagerResponse response = FileManager.getInstance().createChunksFromBigFile(args[0]);
		
		boolean pass1Complete = runFirstPassMapReduce("splits/", "mapper1Output/",
				response.getSupport(), response.getNoOfBaskets(),
				response.getNoOfSplitedFiles());
		if (pass1Complete) {
			runSecondPassMapReduce("mapper1Output/", args[1], response.getSupport());
		}
		
		long algoRunTime = System.currentTimeMillis() - startTime;
		System.out.println("Algo Run Time:"+algoRunTime/1000);

	}

	private static boolean runFirstPassMapReduce(String inputPath,
			String outputPath, int globalSupport, int noOfTotalBaskets,
			int noOfReducer) throws IOException, ClassNotFoundException,
			InterruptedException {
		System.out.println("Starting Pass 1");

		Job jobPass1 = HadoopConf.generateConf(MapReducePass1.class,
				MapReducePass1.Pass1Mapper.class, MapReducePass1.Pass1Reducer.class, "jobPass1",
				Text.class, IntWritable.class, Text.class, IntWritable.class,
				CustomTextInputFormat.class, CustomPartitioner.class,
				noOfReducer);

		jobPass1.getConfiguration().setInt("GLOBAL_SUPPORT", globalSupport);
		jobPass1.getConfiguration().setInt("NUMBER_OF_TOTAL_BASKETS",
				noOfTotalBaskets);
		jobPass1.getConfiguration().setLong("mapreduce.task.timeout", 1000000);

		FileInputFormat.setInputPaths(jobPass1, new Path(inputPath));
		FileOutputFormat.setOutputPath(jobPass1, new Path(outputPath));

		return jobPass1.waitForCompletion(true);
	}

	private static boolean runSecondPassMapReduce(String inputPath,
			String outputPath, int globalSupport) throws IOException,
			ClassNotFoundException, InterruptedException {
		System.out.println("Starting Pass 1");

		Job jobPass2 = HadoopConf.generateConf(MapReducePass1.class,
				MapReducePass2.Pass2Mapper.class, MapReducePass2.Pass2Reducer.class, "jobPass2",
				Text.class, IntWritable.class, Text.class, IntWritable.class,
				TextInputFormat.class, null, 1);

		jobPass2.getConfiguration().setInt("GLOBAL_SUPPORT", globalSupport);
		jobPass2.getConfiguration().setLong("mapreduce.task.timeout", 1000000);

		FileInputFormat.setInputPaths(jobPass2, new Path(inputPath));
		FileOutputFormat.setOutputPath(jobPass2, new Path(outputPath));

		return jobPass2.waitForCompletion(true);
	}
}