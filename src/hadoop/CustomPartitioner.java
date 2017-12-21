package hadoop;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class CustomPartitioner extends Partitioner<Text, IntWritable>{

	@Override
	public int getPartition(Text key, IntWritable valaue, int numReduceTasks) {
		String strKey = key.toString().replace("[", "").replace("]", "").trim();
		String[] itemSets = strKey.split(",");
		int sum=0;
		for(int i=0;i<itemSets.length;i++){
			sum+= Integer.parseInt(itemSets[i].trim());
		}
		return sum % numReduceTasks;
	}

}
