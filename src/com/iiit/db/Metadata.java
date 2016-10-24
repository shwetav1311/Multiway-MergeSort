/*
 * @author Shweta Verma
 */
package com.iiit.db;

import java.util.Vector;

public class Metadata {

	public Integer blockSize;  //blocksize
	public Integer recordSize;  //Record size in bytes
	public Integer memoryRecords;  //No.of records that can be sorted at time in main memory
	public Integer totalRecords;   //total number of records;
	public Integer numSubList;   //No.of sublists createds
	public Vector<Integer> sortIndex ;  //List of index on which sort needs to be performed;
	public Long memorySize;  // size of memory;
	public boolean order;    // true: asc false:desc
	public String inputFile;
	public String outputFile;
	public Vector<Integer> outCols;  // column indexes that need to be present in output;
	
}
