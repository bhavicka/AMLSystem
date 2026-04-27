package com.tss.AmlSystem.utils;

import java.util.List;

public class FileHeaders {
    public static final List<String> CUSTOMER_HEADER =List.of(
            "clientNumber",
            "firstName",
            "lastName",
            "middleName",
            "aadharNumber",
            "pan",
            "occupation",
            "occupationType",
            "isPep",
            "riskRate",
            "monthlyIncome",
            "dob",
            "professionMultiplier",
            "familyCode"
    );

    public static final List<String> ACCOUNT_HEADER =List.of(
            "clientNumber",
            "accountNumber",
            "accountType",
            "accountStatus"
    );
    public static final List<String> TRANSACTION_HEADER =List.of(
            "accountNumber",
            "counterPartyAccountNumber",
            "transactionDate",
            "transactionType",
            "transactionMode",
            "amount",
            "transactionReferenceNumber"
    );
}
