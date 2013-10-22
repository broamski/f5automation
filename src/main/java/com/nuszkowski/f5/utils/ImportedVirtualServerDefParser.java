package com.nuszkowski.f5.utils;

import com.googlecode.jcsv.reader.CSVEntryParser;

public class ImportedVirtualServerDefParser implements CSVEntryParser<ImportedVirtualServerDef> {
	public ImportedVirtualServerDef parseEntry(String... data) {
		if (data.length < 4) {
			throw new IllegalArgumentException("Virtual Server input data is not in valid format!");
		}

		String vs_name = data[0];
		String ip_address = data[1];
		int port = Integer.parseInt(data[2]);
		String default_pool = data[3];

		return new ImportedVirtualServerDef(vs_name, ip_address, port, default_pool);

	}
}