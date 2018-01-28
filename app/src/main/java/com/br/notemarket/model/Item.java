package com.br.notemarket.model;

/**
 * Created by Thaísa on 14/01/2018.
 */

public class Item {

    private String descItem;
    private Boolean checkedItem;

    public String getDescItem() {
        return descItem;
    }

    public void setDescItem(String descItem) {
        this.descItem = descItem;
    }

    public Boolean getCheckedItem() {
        return checkedItem;
    }

    public void setCheckedItem(Boolean checkedItem) {
        this.checkedItem = checkedItem;
    }

    @Override
    public String toString() {
        return descItem;
    }
}
