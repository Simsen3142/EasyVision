package arduino.csv;

import java.util.ArrayList;
import java.util.List;

public class CsvConverter {
	
	private String separator=";";
	private String comma=",";
	
//	public CsvConverter(String separator, String comma) {
//		this.separator=separator;
//		this.comma=comma;
//	}
	
	public static double[] convertCsvs(String csvRow, String separator, String comma) {
		csvRow.replaceAll(comma, ".");
		String[] strings=csvRow.split(separator);
		double[] ret=new double[strings.length];

		for(int i=0;i<strings.length;i++) {
			String s=strings[i];
			try {
				double d=Double.parseDouble(s);
				ret[i]=d;
			}catch (Exception e) {
				ret[i]=0;
			}
		}
		
		return ret;
	}
	
	/**
	 * @return the separator
	 */
	public String getSeparator() {
		return separator;
	}

	/**
	 * @param separator the separator to set
	 */
	public void setSeparator(String separator) {
		this.separator = separator;
	}
	
	public static double getDoubleValueAtPos(double[] nrs, int position) {
		double nothing=0d;

		if(position>nrs.length-1) {
			return nothing;
		}
		return nrs[position];
	}
	
	public static double[] getDoubleValuesAtPositions(double[] nrs, int... positions) {
		double[] ds=new double[positions.length];
		
		for(int i=0;i<positions.length;i++) {
			ds[i]=getDoubleValueAtPos(nrs, positions[i]);
		}
		
		return ds;
	}

	/**
	 * @return the comma
	 */
	public String getComma() {
		return comma;
	}

	/**
	 * @param comma the comma to set
	 */
	public void setComma(String comma) {
		this.comma = comma;
	}
	
	public static String toCsv(Object...objects) {
		String s="";
		
		for(Object o:objects) {
			s+=o+";";
		}
		s=s.substring(0, s.length()-2)+"\n";
		
		return s;
	}
	
}
