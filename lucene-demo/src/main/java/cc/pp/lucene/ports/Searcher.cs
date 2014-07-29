
class Searcher {
   String indexDir = args[0];
   String q = args[1];

   public static void search(String indexDir, String q) {
       Directory dir = FSDriecotry.Open(new System.IO.FileInfo(indexDir));
       IndexSearcher searcher = new IndexSearcher(dir, true);
       QueryParser parser = new QueryParser("contents", new StandardAnalyzer(Version.LUCENE_CURRENT));

       Query query = parser.Parser(q);
       Lucene.Net.Saerch.TopDocs hits = searher.Search(query, 10);
       System.Console.WriteLine("Found " + hits.totalHits + " document(s) that matched query '" + q + "':");
       for (int i = 0; i < hits.scoreDocs.Length; i++) {
           ScoreDoc scoreDoc = hits.ScoreDoc[i];
           Document doc = searcher.Doc(scoreDoc.doc);
           System.Console.WriteLine(doc.Get("filename"));
       } 
      searcher.Close();
   } 
} 
