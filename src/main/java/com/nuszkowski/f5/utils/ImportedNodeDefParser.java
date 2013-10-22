package com.nuszkowski.f5.utils;

import com.googlecode.jcsv.reader.CSVEntryParser;

public class ImportedNodeDefParser implements CSVEntryParser<ImportedNodeDef> {
	public ImportedNodeDef parseEntry(String... data) {
		if (data.length != 4) {
			throw new IllegalArgumentException("Node input data is not in valid format!");
		}

		String ip_address = data[0];
		String server_name = data[1];
		String pool_name = data[2];
		int pool_port = Integer.parseInt(data[3]);

		return new ImportedNodeDef(ip_address, server_name, pool_name, pool_port);

	}
}