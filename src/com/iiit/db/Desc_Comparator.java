package com.iiit.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Desc_Comparator implements Comparator<List>{

	@Override
	public int compare(List l1, List l2) {
		// TODO Auto-generated method stub
		for(int i=0;i<Driver.meta.sortIndex.size();i++)
	    {
			int index = Driver.meta.sortIndex.get(i);
		//	System.out.println("sorying on " + index);
			
			String type = Driver.mapIndexType.get(index);
			
			if(type==Constants.DATE)
			{
				//System.out.println("Type is date");
				String date1 = l1.get(index).toString();
				String date2 = l2.get(index).toString();

				SimpleDateFormat f = new SimpleDateFormat("dd/mm/yyyy");
				Date d1 = new Date();
				Date d2 = new Date();
				try {
					d1 = f.parse(date1);
					d2 = f.parse(date2);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Driver.myExit("Invalid Date");
				}
				if(d1.getTime()<d2.getTime())
				{
					return 1;
				}else if ( d1.getTime() > d2.getTime())
				{
					return -1;
				}
				
			}else if(type==Constants.INTG)
			{
				Integer int1 = Integer.parseInt(l1.get(index).toString());
				Integer int2 = Integer.parseInt(l2.get(index).toString());
				
				if(int1<int2)
				{
					return 1;
				}else if ( int1> int2)
				{
					return -1;
				}
			}else
			{
			//	System.out.println("Type is other");
				if(l1.get(index).toString().compareTo(l2.get(index).toString())!= 0)
				{
					return (-1) * l1.get(index).toString().compareTo(l2.get(index).toString());
				}
			}
			
	    }
		
	    return 0;
	}

}
