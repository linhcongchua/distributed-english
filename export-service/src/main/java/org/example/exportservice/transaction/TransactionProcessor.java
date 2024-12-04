package org.example.exportservice.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class TransactionProcessor implements ItemProcessor<Transaction, Transaction> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionProcessor.class);

    @Override
    public Transaction process(Transaction transaction) throws Exception {
        LOGGER.debug("Processing transaction {}", transaction);
        return transaction;
    }
}
