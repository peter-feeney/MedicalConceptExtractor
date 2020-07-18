package metamap_experiments;

import java.util.*;
import java.util.Map;

import org.apache.poi.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import gov.nih.nlm.nls.metamap.*;

/*
 * This class creates a Spreadsheet object that simplifies the IO
 * (accessing the captions and writing the annotations from MetaMaps)
 * to the spreadsheet down to a few lines of code.
 */

public class Spreadsheet {
	
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;
	private int rows;
	private int columns;
	private StringBuilder mm_results;
	
	
	/**
	 * Constructor for spreadsheet class.
	 * Location of read-in spreadsheet is left as a 
	 * parameter for now, in case I want to deal with a separate 
	 * spreadsheet later. But should only be used for 
	 * annotations_and_QAPairs_Training
	 */
	
	public Spreadsheet() {
		String location = "/slurm_storage/feeneypa/annotations_and_QAPairs_Training_Peter_July2.xlsx";
		try {
			this.workbook = readInFile(location);
		} catch(IOException e) {
			System.out.println("Error with IOStream.");
		}
		
		this.sheet = workbook.getSheetAt(0);
		this.rows = 12896;
		this.columns = 11;
		this.mm_results = new StringBuilder();
	}
	
	public Spreadsheet(String location) {
		try {
			this.workbook = readInFile(location);
		} catch(IOException e) {
			System.out.println("Error with IOStream.");
		}
		
		this.sheet = workbook.getSheetAt(0);
		this.rows = sheet.getLastRowNum() - sheet.getFirstRowNum();
		this.columns = sheet.getRow(0).getLastCellNum() - 1; //stored as last index + 1 without adjustment
		this.mm_results = new StringBuilder();
	}
	
	/**
	 * Should take in the location of an excel sheet and
	 * return an XSSF with holding the data in that
	 * excel sheet, with an extra column created to store 
	 * MetaMap annotations.
	 * @param location of excel sheet
	 * @return XSSFWorkbook with data from excel sheet loaded in. 
	 */
	
	
	private static XSSFWorkbook readInFile(String location) throws IOException {
		//Create object of file class to open xlsx file
		File xlsx = new File(location); 
		//Create object of FileInputStream class to read xlsx file
		FileInputStream inputStream = new FileInputStream(xlsx);
		//Create new XSSFWorkbook from inputStream and return:
		XSSFWorkbook toReturn =  new XSSFWorkbook(inputStream);
		XSSFSheet sheet = toReturn.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		//Iterate over rows and create extra column to store MM annotations
		while(iterator.hasNext()) {
			Row currRow = iterator.next();
			Cell newCell = currRow.createCell(
					currRow.getLastCellNum(), CellType.STRING
			);
			if(currRow.getRowNum() == 0) {
				newCell.setCellValue("MetaMap Annotations");
			}
		}
		return toReturn;
	}
	
	
	
	
	
	/**
	 * Should access caption of given row index for annotations and
	 * QA Pairs default spreadsheet.
	 * @param annotations
	 * @return String copy of medical caption.
	 */
	
	public String readCap(int rowNum, int capLocation) {
		Row currRow = sheet.getRow(rowNum);
		if(currRow.getCell(capLocation) == null) {
			return "NULL VALUE REACHED";
		}
		try {
			return replace_UTF8.ReplaceLooklike(currRow.getCell(capLocation).getStringCellValue());
		} catch(IllegalStateException e) {
			return "NUMERIC OR OTHER VALUE REACHED";
		} catch(IOException e) {
			return "MAJOR ERROR OCCURED: BAD INPUT OR OUTPUT";
		}
		
	}
	
	/**
	 * Gets ID of image at the indicated row.
	 * @param index
	 * @return
	 */
	
	public String getID(int rowNum) {
		Row currRow = sheet.getRow(rowNum);
		return currRow.getCell(1).getStringCellValue();
	}
	
	
	/**
	 * Gets ID of image at the indicated row and cell #.
	 * @param index
	 * @return
	 */
	
	public String getID(int rowNum, int cellNum) {
		Row currRow = sheet.getRow(rowNum);
		return currRow.getCell(cellNum).getStringCellValue();
	}
	
	
	/**
	 * Should write annotations from MetaMap to StringBuilder 
	 * that will later be dumped into a csv file.
	 * csv file.
	 * @param annotations
	 * @return list of annotations for all captions so far
	 */
	public StringBuilder write(String annotations) throws Exception {
		mm_results.append(annotations);
		return mm_results;
	}
	
	
	/**
	 * Gets contents of list of annotations 
	 * for all captions so far.
	 * @param None.
	 * @return list of annotations for all captions so far.
	 */
	
	public StringBuilder getAnnotations() {
		return mm_results;
	}
	
	/**
	 * Should write contents of StringBuilder into
	 * a text file. Text file then can be placed into Excel spreadsheet
	 * and manipulated from there. 
	 */
	
	public boolean writeTXT(String destination) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(destination));
			System.out.println("Here's what should be written to the text file: \n" + mm_results);
		    writer.write(mm_results.toString());
		    writer.close();
		    return true;
		} catch(IOException e) {
			System.out.println("Failed to write text file");
			return false;
		}
	}
	
	
	/**
	 * Should dump contents of StringBuilder into
	 * csv file. CSV file will then be combined with captions
	 * using pandas.
	 * @return whether or not operation was successful.
	 */
	public boolean makeCSV() {
		try (PrintWriter writer = new PrintWriter(new File("output.csv"))) {
			writer.write(mm_results.toString());
			return true;
		} catch(FileNotFoundException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	
	/**
	 * Quick test to see if reading in file correctly.
	 * Should return first caption/question.
	 * @return sample caption
	 */
	
	public String test() {
		return readCap(1, 7);
	}
	
	/**
	 * Gets number of rows in spreadsheet.
	 * @return # of rows
	 */
	
	public int getRows() {
		return rows;
	}
	
	/**
	 * Gets number of columns in spreadsheet
	 * @return # of columns
	 */
	
	public int getColumns() {
		return columns;
	}
	
}	
	
	
	
	
	
	
	
	
	
	

