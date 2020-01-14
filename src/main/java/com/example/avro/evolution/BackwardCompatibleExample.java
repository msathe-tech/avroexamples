package com.example.avro.evolution;

import java.io.File;
import java.io.IOException;

import com.example.Customer;
import com.example.CustomerV1;
import com.example.CustomerV2;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

public class BackwardCompatibleExample {
	public static void main(String[] args) {
		CustomerV1.Builder customerBuilder = CustomerV1.newBuilder();
		customerBuilder.setAge(26);
		customerBuilder.setFirstName("Bunty");
		customerBuilder.setLastName("Bubbly");
		customerBuilder.setHeight(175f);
		customerBuilder.setWeight(90f);
		customerBuilder.setAutomatedEmail(false);
		CustomerV1 customerV1 = customerBuilder.build();
		System.out.println(customerV1);

		System.out.println("---");
		System.out.println("Backward compatibility: Consumer of V2 can read Producer of V1");
		System.out.println("1. Dropping a default field is both backward and forward compatible");
		System.out.println("2. Adding a default field is both backward and forward compatible");
		System.out.println("3. Dropping a mandatory field is backward compatible");
		System.out.println("4. Adding a mandatory field is NOT backward compatible");
		System.out.println("---");



		// Step2: write specific record to a file using V1 schema
		final DatumWriter<CustomerV1> datumWriter = new SpecificDatumWriter<>(CustomerV1.class);
		File file = new File("customer-specific-v1.avro");
		try (DataFileWriter<CustomerV1> dataFileWriter = new DataFileWriter<>(datumWriter)) {
			dataFileWriter.create(customerV1.getSchema(), file);
			dataFileWriter.append(customerV1);
			System.out.println("successfully wrote customer-specific-v1.avro");
		} catch (IOException e){
			e.printStackTrace();
		}


		// Step2: read it from a file using V2 schema
		final DatumReader<CustomerV2> datumReader = new SpecificDatumReader<>(CustomerV2.class);
		final DataFileReader<CustomerV2> dataFileReader;
		try {
			System.out.println("Reading our specific record using V2 schema");
			dataFileReader = new DataFileReader<>(file, datumReader);
			while (dataFileReader.hasNext()) {
				CustomerV2 readCustomer = dataFileReader.next();
				System.out.println(readCustomer.toString());
				System.out.println("First name: " + readCustomer.getFirstName());
			}
			System.out.println("Backword compatibility example complete");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
