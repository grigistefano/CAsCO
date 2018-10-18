package casco.core;

public class GcgCell {
    private int state = 0;	//stato attuale
    private int newState;	//prossimo stato

    public GcgCell() {

    }

    public GcgCell(int state) {
        this.state = state;
    }

    public void setNewState(int state) {
        newState = state;	//definisce il prossimo stato
    }

    public void updateState() {
        state = newState;	//rende il prossimo stato attuale
    }

    public int getState() {
        return state;
    }
}
