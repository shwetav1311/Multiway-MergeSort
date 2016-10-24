/*
 * @author Shweta Verma
 */

package com.iiit.db;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

public class Driver {

	
	public static HashMap<String, Attribute> mapColumns;
	public static HashMap<Integer,String> mapIndexType;
	public static Metadata meta;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String metaFileName = args[1];
		meta = new Metadata();
	
		createColumns(metaFileName);
		
//		myPrint(mapColumns);
//		myPrint(mapIndexType);
		
		parseArguements(args);
	
		
//		myPrint(meta.inputFile);
//		myPrint(meta.outputFile);
//		myPrint(meta.memorySize);
//		myPrint(meta.order);
//		myPrint(meta.outCols);
//		myPrint("Sort columbs");
//		myPrint(meta.sortIndex);
//		myPrint(meta.recordSize);
		
		int count=1;
		try {
			count = countLines(meta.inputFile);
//			myPrint(count);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		meta.totalRecords = count;
		
		meta.memoryRecords = (int) ((meta.memorySize)/(meta.recordSize));
		meta.numSubList = (int) Math.ceil(meta.totalRecords / (meta.memoryRecords*1.0)) ;
		
		
//		System.out.println("No.of memeory records" + meta.memoryRecords);
		System.out.println("No.of sublists" + meta.numSubList);
		meta.blockSize  = (int) ((meta.memorySize)/((meta.numSubList+1) * meta.recordSize * 2));
//		System.out.println("No.of block size" + meta.blockSize + "dfsdf " + meta.totalRecords);
		
		
		
		Sort sort = new Sort(mapColumns,mapIndexType,meta);
		
		long beginTime = new Date().getTime();
		try {
			myPrint("Creating Sublists...");
			sort.createSublist();
			myPrint("Sorting Sublists...");
			sort.merge();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long endTime = new Date().getTime();
		
		
		System.out.println("Time taken :"+((endTime-beginTime)/60000.0));
		
//		myPrint("DONE!!!!!!!!!!!!!!");
	}
	
	
	private static void parseArguements(String[] args) {
		// TODO Auto-generated method stub
			
		
		
		meta.inputFile=args[3];
		meta.outputFile = args[5];
		meta.memorySize = (long) (Integer.parseInt(args[11])*1024*1024);
		
		if(args[13].equals("asc"))
		{
			meta.order = true;
		}else
			meta.order = false;
		
		String cols[] = args[7].split(",");
		
		meta.outCols = new Vector<>();
		
		for(int i=0;i<cols.length;i++)
		{
			if(mapColumns.containsKey(cols[i]))
			{
				int index = mapColumns.get(cols[i]).index;
				meta.outCols.add(index);
			}else
			{
				myExit("Invalid Column");
			}
		}
		
		
		meta.sortIndex = new Vector<>();
		
		cols = args[9].split(",");
		
		for(int i=0;i<cols.length;i++)
		{
			if(mapColumns.containsKey(cols[i]))
			{
				int index = mapColumns.get(cols[i]).index;
				meta.sortIndex.add(index);
			}else
			{
				myExit("Invalid Column");
			}
		}
	
	}


	public static void createColumns(String fileName)
	{
	
		String line="";
		mapColumns = new HashMap<>();
		mapIndexType =  new HashMap<>();
		
		try {
			BufferedReader buff = new BufferedReader(new FileReader(fileName));
			int cnt=0;
			int size=0;
			while((line = buff.readLine())!=null)
			{
				String tokens[] = line.split(",");
				Attribute atb = new Attribute();
				atb.index=cnt;
				atb.name = tokens[0];
				atb.type = getType(tokens[1]);
				size = size + getSize(tokens[1]);
				
				mapColumns.put(atb.name,atb);
				mapIndexType.put(cnt,atb.type);
				cnt++;
			}
			meta.recordSize = size;
//			myPrint(size);
			buff.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			myExit("Error Opening File");
		}     
		
	}
	
	
	public static String getType(String type)
	{
		if(type.contains(Constants.DATE))
			return Constants.DATE;
		else if(type.contains(Constants.INTG))
			return Constants.INTG;
		else
			return Constants.STR;
	}
	
	
	public static int getSize(String type)
	{
		if(type.contains(Constants.DATE))
			return 10+1;
		else if(type.contains(Constants.INTG))
			return 6;
		else
		{
			return Integer.parseInt(type.substring(5,7)) + 1; 
		}
	}
	
	public static void myPrint(Object obj)
	{
		System.out.println(obj);
	}
	
	public static void myExit(String errMsg)
	{
		System.out.println(errMsg);
		System.exit(0);
	}
	
	
	public static int countLines(String filename) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(filename));
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0 && !empty) ? 1 : count;
	    } finally {
	        is.close();
	    }
	}

}


	




/* 
 * String string_date = "12-December-2012";

SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy");
Date d = f.parse(string_date);
long milliseconds = d.getTime();
 * 
 * 
 */