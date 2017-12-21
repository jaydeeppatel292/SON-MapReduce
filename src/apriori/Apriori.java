package apriori;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper.Context;

import model.AprioriPass;
import model.AprioriTransactionPair;

public class Apriori extends Thread {

	private List<List<Integer>> transactionList;
	private int localSupport;
	private List<AprioriPass> aprioriPassList;
	private Context context;

	/**
	 * Constructor for Apriori Algo
	 * @param thrName
	 * @param data
	 * @param globalSupport
	 * @param noOfTotalBaskets
	 * @param context
	 */
	public Apriori(String thrName, String data, int globalSupport,
			int noOfTotalBaskets, Context context) {
		super(thrName);
		this.context = context;
		this.transactionList = new ArrayList<List<Integer>>();
		this.aprioriPassList = new ArrayList<>();
		HashMap<Integer, Integer> mapFrequentItem = writeIntoDatabase(data);
		this.localSupport = (int)(((double)globalSupport * transactionList.size())/ noOfTotalBaskets);
		if (this.localSupport == 0) {
			this.localSupport = 1;
		}
		addFrequentItemToAprioriFirstPass(mapFrequentItem);
	}

	/**
	 * Run Apriori algo thread
	 */
	@Override
	public void run() {
		goThroughAllPossiblePass();
	}

	/**
	 * @param aprioriPass
	 */
	public void addAprioriPass(AprioriPass aprioriPass) {
		this.aprioriPassList.add(aprioriPass);
	}

	/**
	 * @param index
	 * @return
	 */
	public AprioriPass getAprioriPass(int index) {
		return this.aprioriPassList.get(index);
	}

	/**
	 * @return
	 */
	public AprioriPass getAprioriCurrentPass() {
		return this.aprioriPassList.get(this.aprioriPassList.size() - 1);
	}

	/**
	 * @return
	 */
	public List<AprioriPass> getAprioriFrequentDataList() {
		return this.aprioriPassList;
	}

	/**
     *Remove unfrequent pairs for current pass apriori ...
     */
	private void removeUnFrequentPairForCurrentPass() {
		AprioriPass aprioriPass = getAprioriCurrentPass();
		for (Iterator<AprioriTransactionPair> it = aprioriPass
				.getAprioriTransactionPairList().iterator(); it.hasNext();) {
			AprioriTransactionPair aprioriTransactionPair = it.next();
			if (aprioriTransactionPair.getPairOccuredCount() < localSupport) {
				it.remove();
			}
		}
	}

	/**
	 * Add transaction in transactionList
	 *
	 * @param transaction
	 */
	public void addTransactioln(List<Integer> transaction) {
		transactionList.add(transaction);
	}

	/**
	 * Get All transaction from file
	 *
	 * @return
	 */
	public List<List<Integer>> getTransactionList() {
		return transactionList;
	}

	/**
	 * Getting transaction for basket number
	 *
	 * @param basketNumber
	 * @return
	 */
	public List<Integer> getTransaction(int basketNumber) {
		return getTransactionList().get(basketNumber);
	}

	/**
	 * Create TransactionList from file Data
	 *
	 * @param fileData
	 */
	public HashMap<Integer, Integer> writeIntoDatabase(String fileData) {
		HashMap<Integer, Integer> mapFrequentItem = new HashMap<>();
		String[] lines = fileData.split("\n");
		for (String line : lines) {
			List<Integer> transaction = new ArrayList<Integer>();
			String[] itemList = line.trim().split(",");
			for (int itemIndex = 1; itemIndex < itemList.length; itemIndex++) {
				String num = itemList[itemIndex];
				int item = Integer.parseInt(num.trim());
				transaction.add(item);
				if (mapFrequentItem.containsKey(item)) {
					mapFrequentItem.put(item, mapFrequentItem.get(item) + 1);
				} else {
					mapFrequentItem.put(item, 1);
				}
			}
			if (transaction.isEmpty())
				continue;
			Collections.sort(transaction);
			addTransactioln(transaction);
		}
		return mapFrequentItem;

	}

	/**
	 * Generating Apriori FirstPass from single items HashMap
	 * @param mapFrequentItem
	 */
	private void addFrequentItemToAprioriFirstPass(
			HashMap<Integer, Integer> mapFrequentItem) {
		Map<Integer, Integer> map = new TreeMap<Integer, Integer>(
				mapFrequentItem);
		Set set = map.entrySet();
		Iterator iterator = set.iterator();
		AprioriPass aprioriPass = new AprioriPass(1);
		while (iterator.hasNext()) {
			Map.Entry itemMap = (Map.Entry) iterator.next();
			int frequency = (int) itemMap.getValue();
			if (frequency >= localSupport) {
				int item = (int) itemMap.getKey();
				AprioriTransactionPair aprioriTransactionPair = new AprioriTransactionPair();
				List<Integer> itemList = new ArrayList<>();
				itemList.add(item);
				aprioriTransactionPair.setPairOccuredCount(frequency);
				aprioriTransactionPair.addAllPairList(itemList);
				aprioriPass.addAprioriTransactionPair(aprioriTransactionPair);
			}
		}
		addAprioriPass(aprioriPass);
	}

	/**
	 * Function will scan the given candidatePair in all transactionList
	 *
	 * @param candidatePair
	 *            : candidatepair to scan
	 * @return : Candidate Frequency
	 */
	public int scanPairsFromDatabase(List<Integer> candidatePair) {
		int candidateFrequency = 0;
		for (List<Integer> transaction : transactionList) {
			if (transaction.containsAll(candidatePair)) {
				candidateFrequency++;
			}
		}
		return candidateFrequency;
	}

	
	/** Main Algo for creating all possible frequent pairs
	 * Generate All candidate frequent pairs 
	 */
	public void goThroughAllPossiblePass() {
		boolean isNextPassRequired = true;
		while (isNextPassRequired) {
			isNextPassRequired = false;
			AprioriPass currentPass = aprioriPassList.get(aprioriPassList
					.size() - 1);
			AprioriPass aprioriPass = new AprioriPass(getAprioriCurrentPass()
					.getPassNumber() + 1);
			for (int aprioriTransactionPairIndex1 = 0; aprioriTransactionPairIndex1 < currentPass
					.getAprioriTransactionPairList().size(); aprioriTransactionPairIndex1++) {
				for (int aprioriTransactionPairIndex2 = aprioriTransactionPairIndex1 + 1; aprioriTransactionPairIndex2 < currentPass
						.getAprioriTransactionPairList().size(); aprioriTransactionPairIndex2++) {
					AprioriTransactionPair aprioriTransactionPair1 = currentPass
							.getAprioriTransactionPairList().get(
									aprioriTransactionPairIndex1);
					AprioriTransactionPair aprioriTransactionPair2 = currentPass
							.getAprioriTransactionPairList().get(
									aprioriTransactionPairIndex2);
					List<Integer> aprioriSubList1 = aprioriTransactionPair1
							.getPairList().subList(
									0,
									aprioriTransactionPair1.getPairList()
											.size() - 1);
					List<Integer> aprioriSubList2 = aprioriTransactionPair2
							.getPairList().subList(
									0,
									aprioriTransactionPair2.getPairList()
											.size() - 1);
					if (aprioriSubList1.equals(aprioriSubList2)) {
						AprioriTransactionPair newAprioriTransactionPair = new AprioriTransactionPair();
						newAprioriTransactionPair
								.addAllPairList(aprioriSubList1);
						newAprioriTransactionPair
								.addPair(
										aprioriTransactionPair1
												.getPairList()
												.subList(
														aprioriTransactionPair1
																.getPairList()
																.size() - 1,
														aprioriTransactionPair1
																.getPairList()
																.size()).get(0),
										aprioriTransactionPair2
												.getPairList()
												.subList(
														aprioriTransactionPair2
																.getPairList()
																.size() - 1,
														aprioriTransactionPair2
																.getPairList()
																.size()).get(0));
						List<Integer> aprioriSublist3 = newAprioriTransactionPair
								.getPairList().subList(
										1,
										newAprioriTransactionPair.getPairList()
												.size());
						boolean isSublist3IsInList = false;
						for (AprioriTransactionPair aprioriTransactionPair : currentPass
								.getAprioriTransactionPairList()) {
							if (aprioriTransactionPair.getPairList().equals(
									aprioriSublist3)) {
								isSublist3IsInList = true;
								break;
							}
						}
						if (isSublist3IsInList) {
							int itemSupport = scanPairsFromDatabase(newAprioriTransactionPair
									.getPairList());
							if (itemSupport >= localSupport) {
								newAprioriTransactionPair
										.setPairOccuredCount(itemSupport);
								aprioriPass
										.addAprioriTransactionPair(newAprioriTransactionPair);
								isNextPassRequired = true;
							}
						}
					}
				}
			}
			if (aprioriPassList.size() > 0) {
				writeFrequentCandidatePairsToReducer();
				aprioriPassList.remove(getAprioriCurrentPass());
			}
			aprioriPassList.add(aprioriPass);
			removeUnFrequentPairForCurrentPass();
		}
		if (aprioriPassList.size() > 0) {
			writeFrequentCandidatePairsToReducer();
			aprioriPassList.remove(getAprioriCurrentPass());
		}
	}

	/**
	 * Write AprioriPass frequent candidate pairs to reducer
	 */
	private void writeFrequentCandidatePairsToReducer() {
		for (AprioriTransactionPair aprioriTransactionPair : getAprioriCurrentPass()
				.getAprioriTransactionPairList()) {
			String frequentItemSet = Arrays.toString(aprioriTransactionPair
					.getPairList().toArray());
			int frequency = aprioriTransactionPair.getPairOccuredCount();
			try {
				context.write(new Text(frequentItemSet), new IntWritable(
						frequency));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
