package com.example.avro.evolution;

import java.io.File;
import java.io.IOException;

import com.example.CustomerV1;
import com.example.CustomerV2;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

public class ForwardCompatibleExample {
	public static void main(String[] args) {
		CustomerV2.Builder customerBuilder = CustomerV2.newBuilder();
		customerBuilder.setAge(26);
		customerBuilder.setFirstName("Bunty");
		customerBuilder.setLastName("Bubbly");
		customerBuilder.setHeight(175f);
		customerBuilder.setWeight(90f);
		customerBuilder.setEmail("bunty.bubbly@example.com");
		customerBuilder.setPhoneNumber("800-500-300");
		CustomerV2 customerV2 = customerBuilder.build();
		System.out.println(customerV2);

		System.out.println("---");
		System.out.println("Foward compatibility: Consumer of V1 can read Producer of V2");
		System.out.println("1. Dropping a default field is both backward and forward compatible");
		System.out.println("2. Adding a default field is both backward and forward compatible");
		System.out.println("3. Dropping a mandatory field is NOT forward compatible");
		System.out.println("4. Adding a mandatory field is forward compatible");
		System.out.println("---");



		// Step2: write specific record to a file using V2 schema
		final DatumWriter<CustomerV2> datumWriter = new SpecificDatumWriter<>(CustomerV2.class);
		File file = new File("customer-specific-v2.avro");
		try (DataFileWriter<CustomerV2> dataFileWriter = new DataFileWriter<>(datumWriter)) {
			dataFileWriter.create(customerV2.getSchema(), file);
			dataFileWriter.append(customerV2);
			System.out.println("successfully wrote customer-specific-v2.avro");
		} catch (IOException e){
			e.printStackTrace();
		}


		// Step3: read it from a file using V1 schema
		final DatumReader<CustomerV1> datumReader = new SpecificDatumReader<>(CustomerV1.class);
		final DataFileReader<CustomerV1> dataFileReader;
		try {
			System.out.println("Reading our specific record using V1 schema");
			dataFileReader = new DataFileReader<>(file, datumReader);
			while (dataFileReader.hasNext()) {
				CustomerV1 readCustomer = dataFileReader.next();
				System.out.println(readCustomer.toString());
				System.out.println("First name: " + readCustomer.getFirstName());
			}
			dataFileReader.close();
			System.out.println("Forward compatibility example complete");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
