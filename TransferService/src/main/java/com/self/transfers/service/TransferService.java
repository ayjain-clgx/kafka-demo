package com.self.transfers.service;

import com.self.transfers.model.TransferRestModel;

public interface TransferService {
    boolean transfer(TransferRestModel productPaymentRestModel);
    boolean transferWithDatabaseOperation(TransferRestModel productPaymentRestModel);
}
