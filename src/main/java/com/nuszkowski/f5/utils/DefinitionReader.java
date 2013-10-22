package com.nuszkowski.f5.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.log4j.Logger;

import com.googlecode.jcsv.CSVStrategy;
import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;

public class DefinitionReader {
	static Logger log = Logger.getLogger("DefinitionReader");
	public List buildNodeInfo(String input_file) throws IOException
	{
		try {
			Reader reader = new FileReader(input_file);
			CSVStrategy myStrategy = new CSVStrategy(',', '"', '#', false, true);
			CSVReader<ImportedNodeDef> ImportItemReader = new CSVReaderBuilder<ImportedNodeDef>(reader).entryParser(new ImportedNodeDefParser()).strategy(myStrategy).build();
			return ImportItemReader.readAll();
		} catch (FileNotFoundException e) {
			log.error(e);
			return null;
		}
	}
	
	public List buildVSInfo(String input_file) throws IOException
	{
		try {
			Reader reader = new FileReader(input_file);
			CSVStrategy myStrategy = new CSVStrategy(',', '"', '#', false, true);
			CSVReader<ImportedVirtualServerDef> ImportItemReader = new CSVReaderBuilder<ImportedVirtualServerDef>(reader).entryParser(new ImportedVirtualServerDefParser()).strategy(myStrategy).build();
			return ImportItemReader.readAll();
		} catch (FileNotFoundException e) {
			log.error(e);
			return null;
		}
	}
}
