package edu.sdse.csvprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class CityCSVProcessor {
	
	List<CityRecord> cityRecords;
	Map<String, List<CityRecord>> recordsByCityName;
	
	public void readAndProcess(File file) {
		cityRecords = new ArrayList<CityRecord>();
		recordsByCityName = new HashMap<String, List<CityRecord>>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {

			br.readLine();
			String line;
			while ((line = br.readLine()) != null) {
				// Parse each line
				String[] rawValues = line.split(",");
				
				int id = convertToInt(rawValues[0]);
				int year = convertToInt(rawValues[1]);
				String city = convertToString(rawValues[2]);
				int population = convertToInt(rawValues[3]);
				
				CityRecord cityRecord = new CityRecord(id, year, city, population);
				cityRecords.add(cityRecord);
				addCityRecordToMap(cityRecord);
			}
		} catch (Exception e) {
			System.err.println("An error occurred:");
			e.printStackTrace();
		}
		printProcessedData();
	}
	
	private String cleanRawValue(String rawValue) {
		return rawValue.trim();
	}
	
	private int convertToInt(String rawValue) {
		rawValue = cleanRawValue(rawValue);
		return Integer.parseInt(rawValue);
	}
	
	private String convertToString(String rawValue) {
		rawValue = cleanRawValue(rawValue);
		
		if (rawValue.startsWith("\"") && rawValue.endsWith("\"")) {
			return rawValue.substring(1, rawValue.length() - 1);
		}
		
		return rawValue;
	}
	
	private void addCityRecordToMap(CityRecord cityRecord) {
		// if city is already in map => update records list
		if (recordsByCityName.containsKey(cityRecord.getCity())) {
			List<CityRecord> updatedCityRecords = recordsByCityName.get(cityRecord.getCity());
			updatedCityRecords.add(cityRecord);
			recordsByCityName.replace(cityRecord.getCity(), updatedCityRecords);
		} 
		// else => create empty records list
		else {
			List<CityRecord> newList = new ArrayList<CityRecord>();
			newList.add(cityRecord);
			recordsByCityName.put(cityRecord.getCity(), newList);
		}
	}
	
	private void printProcessedData() {
				
		// iterate recordsByCityName cities
		for (Entry<String, List<CityRecord>> entry : recordsByCityName.entrySet()) {
			
			int entries = entry.getValue().size();
			int minYear = entry.getValue().get(0).getYear();
			int maxYear = entry.getValue().get(0).getYear();
			double avePopulation = 0;
			
			// iterate records list
			for (CityRecord record : entry.getValue()) {
				if (record.getYear() < minYear) {
					minYear = record.getYear();
				}
				if (record.getYear() > maxYear) {
					maxYear = record.getYear();
				}
				avePopulation += record.getPoulation();
			}
			avePopulation /= entries;
			
			System.out.println(entry.getKey() + " (" + minYear + "," + maxYear + "; " + entries + " entries): " + avePopulation);
		}
	}
	
	
	public static final void main(String[] args) {
		CityCSVProcessor reader = new CityCSVProcessor();
		
		File dataDirectory = new File("data/");
		File csvFile = new File(dataDirectory, "Cities.csv");
		
		reader.readAndProcess(csvFile);
	}
}
