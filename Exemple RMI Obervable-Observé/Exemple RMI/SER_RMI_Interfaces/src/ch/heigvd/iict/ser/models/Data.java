package ch.heigvd.iict.ser.models;

import java.io.Serializable;
import java.util.Date;

public class Data implements Serializable, Comparable<Data> {

	private static final long serialVersionUID = -6784978119922626197L;
	
	private Date 	date 	= null;
	private int 	value 	= -1;
		
	public Date getDate() { return date; }
	public void setDate(Date date) { this.date = date; }
	
	public int getValue() { return value; }
	public void setValue(int value) { this.value = value; }
	
	@Override
	public int compareTo(Data that) {
		try {
			return that.date.compareTo(this.date);
		} catch(Exception e) {
			return 0;
		}
	}
	
	@Override
	public String toString() {
		return "" + this.value;
	}
	
}
