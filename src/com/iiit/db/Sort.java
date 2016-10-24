package com.iiit.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;


public class Sort {
	
	
	public  HashMap<String, Attribute> mapColumns;
	public  HashMap<Integer,String> mapIndexType;
	public  Metadata meta;
	
	public Sort(HashMap<String, Attribute> mapColumns, HashMap<Integer, String> mapIndexType, Metadata meta) {
		super();
		this.mapColumns = mapColumns;
		this.mapIndexType = mapIndexType;
		this.meta = meta;
	}

	public Sort()
	{
		
	}

	
	public void merge() throws IOException
	{
			Vector<ReadFile> readF = new  Vector<>();
			int i=0;
			
			createFile(meta.outputFile);
			
			if(meta.numSubList==1) //only single output file
			{
				File oldName = new File(Constants.OUTPUT+"0.txt");
			    File newName = new File(meta.outputFile);
			    oldName.renameTo(newName);
			    return;
			}
			
			//open buffered readers
			for(i=0;i<meta.numSubList;i++)
			{
				try {
					ReadFile rd = new ReadFile();
					BufferedReader buff = new BufferedReader(new FileReader(Constants.OUTPUT+i+".txt"));
					rd.buff = buff;
					rd.tab = readFileToList(buff);	
					
					readF.add(rd);
					if(rd.tab==null)
					{
						rd.buff.close();
						rd.buff=null;
					}
						
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			int minmax=0;
			List<List> output = new ArrayList<>(); 
			while(true)
			{
				List<List> tempTab = new ArrayList<>(); 
				int cnt = 0;
				for(i=0;i<meta.numSubList;i++)
				{
					if(readF.get(i)==null || readF.get(i).tab==null)
					{
						tempTab.add(null);
						cnt++;
						continue;
					}
					
					if(readF.get(i).tab.size()==0)
					{
						readF.get(i).tab = readFileToList(readF.get(i).buff);	
						
						if(readF.get(i).tab==null) //file read is completed
						{
							readF.get(i).buff.close();
							readF.get(i).buff=null;
							tempTab.add(null);
							cnt++;
							
						}else
						{
							tempTab.add(readF.get(i).tab.get(0));
						}
					}
					else
					{
						tempTab.add(readF.get(i).tab.get(0));
					}
				}
				
	
				if(cnt == meta.numSubList) // all buffers have become null
				{
					writeToOutFile(meta.outputFile, output);
					break;
				}
					
				if(meta.order)
					minmax = getMin(tempTab);
				else
					minmax = getMax(tempTab);
				
				
				output.add(readF.get(minmax).tab.get(0));
				readF.get(minmax).tab.remove(0);
				
				if(output.size()==meta.blockSize)
				{
					writeToOutFile(meta.outputFile, output);
					output = new ArrayList<>();
				}
				
				
				
			}
			
			deleteAllOutputFiles();
			
			
	}
	
	
	public int getMin(List<List> tab)
	{
		
		int i=0;
		int min=0;
		while(i<meta.numSubList && tab.get(i)==null)
		{
			i++;
		}
		min=i;
		
		for(i=min+1;i<meta.numSubList;i++)
		{
			if(tab.get(i)!=null)
			{
				int cmp = new Asc_Comparator().compare(tab.get(min),tab.get(i));
				if(cmp<0)
				{
					min = i;
				}
			}
			
		}
		
		return min;
	}
	
	
	public int getMax(List<List> tab)
	{
		
		int i=0;
		int max=0;
		
		while(i<meta.numSubList && tab.get(i)==null)
		{
			
			i++;
		}
		
		
		max=i;
		
		for(i=max+1;i<meta.numSubList;i++)
		{
			if(tab.get(i)!=null)
			{
				int cmp = new Desc_Comparator().compare(tab.get(max),tab.get(i));
				if(cmp>0)
				{
					max = i;
				}
			}
			
		}
		
		return max;
		
	}
	
	public void deleteAllOutputFiles()
	{
		for(int i=0;i<meta.numSubList;i++)
		{
			boolean success = (new File(Constants.OUTPUT+i+".txt")).delete();
		     if (success) {
		        System.out.println("The file has been successfully deleted"+i); 
		     }
		}
		
		
	}
	
	public List<List> readFileToList(BufferedReader buff) throws IOException
	{
		int cnt=0;
		String line="";
		List<List> tab = new ArrayList<>();
		while(cnt!=meta.blockSize && (line = buff.readLine())!=null)
		{
			String tokens[] = line.split(",");
			
			List tuple = new ArrayList<>();
			
			 for (int i = 0; i <tokens.length; i++) {
			        tuple.add(tokens[i]);
			    } 
			 
			 tab.add(tuple);
			 cnt++;
		}
	
	
	
		if(tab.size()==0)
			return null;
		else
			return tab;
	

	}
	
	public void createSublist()
	{
		String line="";
		try {
			BufferedReader buff = new BufferedReader(new FileReader(meta.inputFile));
			int cnt=0;
			int size=0;
			int subListNum=0;
			
			
			while(size!=meta.totalRecords)
			{
				List<List> tab = new ArrayList<>();
				while(cnt!=meta.memoryRecords && (line = buff.readLine())!=null)
				{
					String tokens[] = line.split(",");
					
					List tuple = new ArrayList<>();
					
					 for (int i = 0; i <tokens.length; i++) {
					        tuple.add(tokens[i]);
					    } 
					 
					 tab.add(tuple);
					 cnt++;
				}
				
				if(meta.order)
					Collections.sort(tab,new Asc_Comparator());
				else
					Collections.sort(tab,new Desc_Comparator());
				

				
				createFile(Constants.OUTPUT+subListNum+".txt");
				writeToFile(Constants.OUTPUT+subListNum+".txt", tab);
				size = size+cnt;
				cnt=0;
				subListNum++;
				
				
				
				if(line==null)
				{
					break;
				}
			}
						
			buff.close();
						
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Driver.myExit("Error Opening File");
		}     
	}
	
	
	
	public void writeToFile(String fileName, List<List> tab )
	{
		PrintWriter pw = null;
		
		for(int i = 0;i<tab.size();i++)
		{
			StringBuilder sb = new StringBuilder();
			List tuple = tab.get(i);
	        sb.append(tuple.get(0).toString());
		
			for(int j=1;j<tuple.size();j++)
			{
				sb.append(",");
				sb.append(tuple.get(j).toString());
			}
			
			//if(i!=tab.size()-1)
				sb.append("\n");
				
			
			try {
				
				pw = new PrintWriter(new FileWriter(fileName,true));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Driver.myExit("File not found");
			}

	        pw.write(sb.toString());
	        pw.close();
			
		}
		
		
	
			
	}
	
	public void writeToOutFile(String fileName, List<List> tab )
	{
		
		
		Vector <Integer>outCols = meta.outCols;
		Integer index = 0;
		PrintWriter pw = null;
		for(int i = 0;i<tab.size();i++)
		{
			StringBuilder sb = new StringBuilder();
			List tuple = tab.get(i);
	       
			index = outCols.get(0);
			sb.append(tuple.get(index).toString());
			
			
			for(int j=1;j<outCols.size();j++)
			{
				index = outCols.get(j);
				sb.append(",");
				sb.append(tuple.get(index).toString());
			}
			
			
			//if(i!=tab.size()-1)
				sb.append("\n");
				
				try {
					
					pw = new PrintWriter(new FileWriter(fileName,true));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Driver.myExit("File not found");
				}

		        pw.write(sb.toString());
		        pw.close();
			
		}
		
		
		
		
			
	}
	
	
	public void createFile(String fileName)
	{
		PrintWriter pw = null;
		
		try {
			
			pw = new PrintWriter(new FileWriter(fileName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Driver.myExit("File not found");
		}

        pw.close();
	}
	
	
	
}
