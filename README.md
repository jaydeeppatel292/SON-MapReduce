# SON-MapReduce

The task is to implement an algorithm for mining frequent itemsets from market basket data. The input to the algorithm will be n baskets, each containing m items, where 3 ≤ m ≤ 100. Each item is a natural number from the set {0, 1, 2, . . . , 99}. The support threshold t, where t ≥ 3, is also part of the input. Algorithm will output all frequent itemsets (not just all pairs) {i1, i2, . . . , ik} {0, 1, . . . , 99}k
for 2 ≤ k ≤ 100 of items whose support is at least t. 

Algorithm will read the input as Enter delimited text files, where each basket is given as basket#, item1, item2, . . . , itemm and baskets are per line (Enter delimited). Basket numbers are positive integers.

The application will then output the frequent itemsets and their count in the form
({i1, i2, . . . , ik} : count) per line. 

Since we have no real parallelism, the timing could be measured as the maximum time to do one
of the subtasks, plus the combination time.