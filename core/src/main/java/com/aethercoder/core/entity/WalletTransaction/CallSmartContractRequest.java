package com.aethercoder.core.entity.WalletTransaction;

public class CallSmartContractRequest {
    private String[] hashes;

    public String[] getHashes() {
        return hashes;
    }

    public void setHashes(String[] hashes) {
        this.hashes = hashes;
    }

    public CallSmartContractRequest(String[] hashes){
        this.hashes = hashes;
    }
}
