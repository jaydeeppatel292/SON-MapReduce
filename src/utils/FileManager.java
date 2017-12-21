package utils;

import global.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import model.FileManagerResponse;

public class FileManager {

	private static FileManager instance;
	
	private FileManager(){}
	
	/**
	 * Getting singleton instance of FileManager ..
	 * @return
	 */
	public static FileManager getInstance(){
		if(instance==null){
			instance = new FileManager();
		}
		return instance;
	}
	
	
	
	/**
	 * Create Chunks from big input file
	 * @param inputPath
	 * @return
	 * @throws IOException
	 */
	public FileManagerResponse createChunksFromBigFile(String inputPath)
			throws IOException {

		File inputFolder = new File(inputPath);
		File[] inputFiles = inputFolder.listFiles();
		int noOfBaskets = 0;
		int support = 0;
		int splitFileNumber = 1;
		
		File file = new File("splits");
		if(!file.exists()){
			file.mkdir();
		}
		
		
		for (File inputFile : inputFiles) {
			int linesWritten = 0;
			String line;
			int sizeInMb = (int) Math
					.ceil(((double) inputFile.length() / (1024 * 1024)));
			FileReader in = new FileReader(inputFile);
			BufferedReader inputReader = new BufferedReader(in);
			BufferedWriter outputWriter = null;
			
			int fileSize = 0;
			support = Integer.parseInt(inputReader.readLine().trim());

			while ((line = inputReader.readLine()) != null) {
				noOfBaskets++;
				fileSize = fileSize + line.length();
				if (linesWritten == 0) {
					File newSplitFile = new File("splits/input" + splitFileNumber++ + ".txt");
					outputWriter = new BufferedWriter(new FileWriter(newSplitFile));
				}

				outputWriter.write(line + "\n");
				linesWritten++;
				if ((int) (((double) fileSize) / (1024 * 1024)) >= Constants.SPLIT_INPUT_FILE_SIZE_MB) {
					linesWritten = 0;
					outputWriter.close();
					fileSize = 0;
				}
			}
			outputWriter.close();
			inputReader.close();
		}
		FileManagerResponse response = new FileManagerResponse(support,
				noOfBaskets, splitFileNumber-1);
		return response;
	}
}
