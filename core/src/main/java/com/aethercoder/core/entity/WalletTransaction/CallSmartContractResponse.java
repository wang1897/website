package com.aethercoder.core.entity.WalletTransaction;

import java.util.List;

public class CallSmartContractResponse {
    private List<Item> items = null;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
