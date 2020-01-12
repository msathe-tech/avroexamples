package com.example.avro.generic;

import java.io.File;
import java.io.IOException;

import com.sun.tools.javah.Gen;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;

public class GenericRecordExample {

	public static void main(String[] args) {

		// Step0: define schme
		Schema.Parser parser = new Schema.Parser();
		Schema schema = parser.parse("{\n"
				+ "     \"type\": \"record\",\n"
				+ "     \"namespace\": \"com.example\",\n"
				+ "     \"name\": \"Customer\",\n"
				+ "     \"doc\": \"Avro Schema for our Customer\",     \n"
				+ "     \"fields\": [\n"
				+ "       { \"name\": \"first_name\", \"type\": \"string\", \"doc\": \"First Name of Customer\" },\n"
				+ "       { \"name\": \"last_name\", \"type\": \"string\", \"doc\": \"Last Name of Customer\" },\n"
				+ "       { \"name\": \"age\", \"type\": \"int\", \"doc\": \"Age at the time of registration\" },\n"
				+ "       { \"name\": \"height\", \"type\": \"float\", \"doc\": \"Height at the time of registration in cm\" },\n"
				+ "       { \"name\": \"weight\", \"type\": \"float\", \"doc\": \"Weight at the time of registration in kg\" },\n"
				+ "       { \"name\": \"automated_email\", \"type\": \"boolean\", \"default\": true, \"doc\": \"Field indicating if the user is enrolled in marketing emails\" }\n"
				+ "     ]\n"
				+ "}");

		// Step1: create a generic record
		GenericRecordBuilder customerBuilder = new GenericRecordBuilder(schema);
		customerBuilder.set("first_name", "Chin");
		customerBuilder.set("last_name", "Chu");
		customerBuilder.set("age", 26);
		customerBuilder.set("height", 175f);
		customerBuilder.set("weight", 85f);
		customerBuilder.set("automated_email", false);
		GenericData.Record customer = customerBuilder.build();
		System.out.println(customer);

		GenericRecordBuilder customerBuilderWithDefault = new GenericRecordBuilder(schema);
		customerBuilderWithDefault.set("first_name", "Chin");
		customerBuilderWithDefault.set("last_name", "Chu");
		customerBuilderWithDefault.set("age", 26);
		customerBuilderWithDefault.set("height", 175f);
		customerBuilderWithDefault.set("weight", 85f);
		GenericData.Record customerWithDefault = customerBuilderWithDefault.build();
		System.out.println(customerWithDefault);

		// Step2: write generic record to a file
		final DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
		DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter);
		try {
			dataFileWriter.create(customer.getSchema(), new File("customer-generic.avro"));
			dataFileWriter.append(customer);
			System.out.println("Written customer to customer-generic.avro");
			dataFileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Step3: read a generic record from a file
		final File file = new File("customer-generic.avro");
		final DatumReader<GenericRecord> datumReader = new GenericDatumReader<>();
		GenericRecord customerRead;
		try (DataFileReader<GenericRecord> dataFileReader = new DataFileReader<>(file, datumReader)){
			customerRead = dataFileReader.next();
			System.out.println("Successfully read avro file");
			System.out.println(customerRead.toString());

			// get the data from the generic record
			System.out.println("First name: " + customerRead.get("first_name"));

			// read a non existent field
			System.out.println("Non existent field: " + customerRead.get("not_here"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
