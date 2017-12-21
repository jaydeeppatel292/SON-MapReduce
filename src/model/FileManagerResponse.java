package model;

public class FileManagerResponse{
	private int support;
	private int noOfBaskets;
	private int noOfSplitedFiles;
	
	
	public FileManagerResponse(int support, int noOfBaskets,
			int noOfSplitedFiles) {
		super();
		this.support = support;
		this.noOfBaskets = noOfBaskets;
		this.noOfSplitedFiles = noOfSplitedFiles;
	}
	public int getSupport() {
		return support;
	}
	public void setSupport(int support) {
		this.support = support;
	}
	public int getNoOfBaskets() {
		return noOfBaskets;
	}
	public void setNoOfBaskets(int noOfBaskets) {
		this.noOfBaskets = noOfBaskets;
	}
	public int getNoOfSplitedFiles() {
		return noOfSplitedFiles;
	}
	public void setNoOfSplitedFiles(int noOfSplitedFiles) {
		this.noOfSplitedFiles = noOfSplitedFiles;
	}

}