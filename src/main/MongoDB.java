package main;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.crypto.Data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import main.disambiguation.v2.GetBaikeInfo;

import org.bson.Document;
/**
 * @author HuangDC
 * @date 2016年4月3日
 * @description 
 */
public class MongoDB {
	PrintStream bb=System.out;
	@SuppressWarnings("resource")
	public static void main(String[] args) throws ParseException, UnsupportedEncodingException, IOException, InterruptedException{
		String file="e://entityOut_v2";
		MongoCredential credential = MongoCredential.createCredential("mdbadmin","admin","bjgdFristDB2016".toCharArray());
		MongoClient mongoClient = new MongoClient(new ServerAddress("idcbak.answercow.org",3006),Arrays.asList(credential));	//3006设置为Mongodb端口号
		MongoDatabase db = mongoClient.getDatabase("Plover");
		System.out.println(db);
		for (String name : db.listCollectionNames()) {
			System.out.println("collection:"+name);
	  	}
		MongoCollection<Document> collection = db.getCollection("BaikeInfo"); 
       System.out.println();
       
//       Document find=new Document();
//       find.put("word", "锁定");
////       find.put("default_alias", "中国人民解放军");
//       FindIterable<Document> xx=collection.find(find);
//       for(Document bb:xx){
//    	   System.out.println(bb);
//       }
       
       
       
       
       
       
       
       
       
       
//     //获取指定Collection中所有的索引
//       for (final Document index : collection.listIndexes()) {
//       	    System.out.println(index.toJson());
//      	}
//     //检索出所有数据
//       collection.deleteMany(new Document());
//       long i=0;
//		for (Document cur : collection.find()) {
//			System.out.println(cur.toJson());
//			i++;
////			System.out.println(i);
////			if(i%100==10){
////				break;
////			}
//      	}
//		System.out.println(i);
		
//       try (BufferedReader br = new BufferedReader(new FileReader(file))) {
//    	    String line;
//    	    ExecutorService executorService=Executors.newFixedThreadPool(3);
//    	    while ((line = br.readLine()) != null) {
//    	    	executorService.submit(new CrawlBaikeParallel(line.trim(),collection));
//    		}
//    		executorService.shutdown();
//    		try {
//                executorService.awaitTermination(1, TimeUnit.DAYS);
//            } catch (InterruptedException ex) {
//                System.out.println(ex.getMessage());
//            }
//    	}
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
    	    String line;
    		int i=1;
    		while ((line = br.readLine()) != null) {
    			i++;
    	    	if(line.equals("向远方")){
    	    		break;
    	    	}
    	    }
    	    while ((line = br.readLine()) != null) {
//    	    	System.out.println(line);
    	    	new GetBaikeInfo(line).writeMongo(collection);
    	    	System.out.println(new Date()+"\tinsert\t" + line+"\t"+(i++));
//    	    	Thread.sleep(1000);
    		}
    	}
		
		
		
		
   
	}
}
class CrawlBaikeParallel implements Runnable{
	static int i=1;
	String line="";
	MongoCollection<Document> collection=null;
	public CrawlBaikeParallel(String line,MongoCollection<Document> collection) {
		this.line=line;
		this.collection=collection;
	}
	@Override
	public void run() {
		process();
	}
	void process(){
		GetBaikeInfo temp;
		try {
			temp = new GetBaikeInfo(line);
			synchronized (collection) {
				temp.writeMongo(collection);
				System.out.println(new Date()+"\tinsert " + line+"\t"+(i++));
			}
		} catch (UnsupportedEncodingException e) {
			System.err.println("processing error "+ line);
		}
	}
}