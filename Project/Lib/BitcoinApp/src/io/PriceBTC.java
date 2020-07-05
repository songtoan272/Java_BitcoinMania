package io;
class PriceBTC {
    private String datetime;
    private double value;
    private String currency;

    public PriceBTC() {
        this.currency = "USD";
    }
    // constructor
    public PriceBTC(String datetime, double value, String currency) {
        this.datetime = datetime;
        this.value = value;
        this.currency = currency;
    }
    // getter
    public String getDatetime() { return this.datetime; }
    public double getValue() { return this.value; }
    public double getCurrency() { return this.currency; }

    // setter
    public void setDateTime(String datetime) { this.datetime = datetime; }
    public void setValue(double value) { this.value = value; }
    public void setCurrency(String currency) { this.currency = currency; }


}