package casco.core;

public class GofCell {
    private boolean state = false;	//stato attuale
    private boolean newState;		//prossimo stato

    public GofCell() {

    }

    public GofCell(boolean state) {
        this.state = state;
    }

    public void setNewState(boolean state) {
        newState = state;		//definisce il prossimo stato
    }

    public void updateState() {
        state = newState;		//rende il prossimo stato attuale
    }

    public boolean getState() {
        return state;
    }
}
