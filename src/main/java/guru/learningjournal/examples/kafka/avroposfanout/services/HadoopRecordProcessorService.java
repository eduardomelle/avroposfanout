/**
 * 
 */
package guru.learningjournal.examples.kafka.avroposfanout.services;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import guru.learningjournal.examples.kafka.avroposfanout.bindings.PosListenerBinding;
import guru.learningjournal.examples.kafka.avroposfanout.model.HadoopRecord;
import guru.learningjournal.examples.kafka.model.PosInvoice;
import lombok.extern.log4j.Log4j2;

/**
 * 
 */
@Service
@Log4j2
@EnableBinding(PosListenerBinding.class)
public class HadoopRecordProcessorService {

	@Autowired
	private RecordBuilder recordBuilder;

	@StreamListener("hadoop-input-channel")
	@SendTo("hadoop-output-channel")
	public KStream<String, HadoopRecord> process(KStream<String, PosInvoice> input) {
		KStream<String, HadoopRecord> hadoopRecordKStream = input.mapValues(v -> this.recordBuilder.getMaskedInvoice(v))
				.flatMapValues(v -> this.recordBuilder.getHadoopRecords(v));
		hadoopRecordKStream.foreach((k, v) -> log.info(String.format("Hadoop Record:- Key: %s, Value: %s", k, v)));

		return hadoopRecordKStream;
	}

}
