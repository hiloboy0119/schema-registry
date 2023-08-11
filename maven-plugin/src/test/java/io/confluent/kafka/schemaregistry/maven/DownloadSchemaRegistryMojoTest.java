/*
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.confluent.kafka.schemaregistry.maven;

import io.confluent.kafka.schemaregistry.avro.AvroSchema;
import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import org.apache.avro.Schema;
import org.junit.Before;
import org.junit.Test;

import javax.security.auth.Subject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;

public class DownloadSchemaRegistryMojoTest extends SchemaRegistryTest {
  DownloadSchemaRegistryMojo mojo;

  @Before
  public void createMojo() {
    this.mojo = new DownloadSchemaRegistryMojo();
    this.mojo.client(new MockSchemaRegistryClient());
  }

  @Test
  public void specificSubjects() throws IOException, RestClientException {
    int version = 1;

    List<File> files = new ArrayList<>();
    this.mojo.subjectPatterns.clear();

    for (int i = 0; i < 100; i++) {
      String keySubject = String.format("TestSubject%03d-key", i);
      String valueSubject = String.format("TestSubject%03d-value", i);
      Schema keySchema = Schema.create(Schema.Type.STRING);
      Schema valueSchema = Schema.createUnion(Arrays.asList(Schema.create(Schema.Type.STRING), Schema.create(Schema.Type.NULL)));
      this.mojo.client().register(keySubject, new AvroSchema(keySchema));
      this.mojo.client().register(valueSubject, new AvroSchema(valueSchema));
      File keySchemaFile = new File(this.tempDirectory, keySubject + ".avsc");
      File valueSchemaFile = new File(this.tempDirectory, valueSubject + ".avsc");

      if (i % 10 == 0) {
        this.mojo.client().getSchemaMetadata(keySubject,1);
        this.mojo.client().getLatestSchemaMetadata(keySubject);
        this.mojo.client().getSchemaMetadata(valueSubject,1);
        this.mojo.client().getLatestSchemaMetadata(valueSubject);
      }
    }
  }
}
