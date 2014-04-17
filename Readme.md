*													*
*													*
*						README						*
*													*
*													*
*													*

Codebase:
--SourceCode/RankingComparisonWeb: UI control and search functions
QueryRest.java: This class declares a REST API interface. Its acts like a middle layer for UI and core Ranking Algorithms.


--SourceCode/RankingAlgoComparison: Tf-idf generation & Query module, Pagerank Query module, Lucene
CalculateScore.java: Computes the score over the sorted TF-IDF index
CorpusSplitter.java: Util class to split the wikipedia corpus into documents for consumption by Lucene
DocumentAttributes.java: This is a POJO class to hold document attributes
DocumentMapperSecondaryIndexer.java: This class creates secondary index for document title map
DocumentScore.java: POJO class to hold document title, score pairs
DocumentTitleMapper.java: Util classes for setting getting titles based on document ID
ExternalSort.java: Util class to join & sort the document posting list externally from seperate chunked files
Index.java: Main file to run indexing
MutableInt.java: Integer class for self-incrementing functions
PageRankDocumentMapper.java: Document title mapper for Pagerank index
PageRankPosting.java: POJO to hold pagerank postings
PageRankQuery.java: Main class to handle pagerank queries
Parser.java: Util class for running SAX parser over wikipedia corpus
Posting.java: POJO to hold posting list for TF-IDF module
QueryCache.java: Cache class to hold about 500 posting results
Query.java: Main class to run queries over TF-IDF index
Search.java: Util class to run binary search over seconday indexes
Stemmer.java: Ported stemmer class
StringSizeEstimator.java: Util class for external sort
Test.java: Util class for test
Util.java: Util class for text processing, and vocabulary reading, dumping index to chunked files, etc.
WordFrequencyCounter.java: Util to count frequency of words in a text and returning a structured format
Word.java: POJO to hold a word along with its frequency

--SourceCode/LuceneTest: Module for Lucene Query testing
LuceneSearch.java: Query class for Lucene index
LuceneSearchTest.java: Test class for LuceneSearch.java


--SourceCode/PageRank: PageRank generation
ComputeRank.java: Computes pagerank value from the outlinks and inlinks file
ExtMergeSort.java: Generic module to perform external merge sort
Links.java: Calculates the number of outlinks and there by generates number of inlinks of a particular doc
LoadinMemory: for loading an intermediate file into memory
SAXHandler: SAX handlers to parse xml
SecondaryIndex.java: generates secondary indices
SortPageRank.java: Sorting final posting list based on pagerank
TitleIndex.java: Main runnable class
TreeSort: sort pagerank values
