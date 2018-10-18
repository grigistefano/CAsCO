package casco.core;

public class GcgBoard {
    private GcgCell[][] grid;
    private int height=3;
    private int width=3;
    private int possibleState = 5;
    
    public GcgBoard(GcgCell[][] grid) {		//costruttore Griffeath's Crystalline Growths data una griglia
        this.grid = grid;
        height = width = grid.length;
    }

    public GcgBoard(int height, int width, int p) {		//costruttore Griffeath's Crystalline Growths date
        this.height=height;								//le dimensioni e il numero dei possibili stati che
        this.width = width;								//una cella può assumere
        this.possibleState = p;
        grid = new GcgCell[height][width];
        
        for (int h = 0; h < grid.length; h++){				//crea una nuova griglia in base agli input
            for (int w = 0; w < grid[h].length; w++){
                grid[h][w] = new GcgCell();
                int r = (int)(Math.random() * possibleState);	//ad ogni cella viene assegnato uno stato
                grid[h][w].setNewState(r);						//casuale tra 0 e p - 1
                grid[h][w].updateState();
            }
        }
    }
    
    public GcgBoard(GcgBoard cgcBoard){		//costruttore per copiare Gcg
    	this.height = cgcBoard.getSize();
    	this.width = cgcBoard.getSize();
    	int size = cgcBoard.getGrid().length;
    	this.grid = new GcgCell[size][size];
    	for(int i = 0; i < size; i++){
    		for(int j = 0; j < size; j++){
    			this.grid[i][j] = new GcgCell(cgcBoard.getGrid()[i][j].getState());
    		}
    	}
    }
    
    public GcgCell[][] getGrid() {
        return grid;
    }
    
    public int getSize() {
        return width;
    }
    
    public int getPossibleState(){
    	return this.possibleState;
    }
    
    //aggiusta parametro 'row' per far rispettare lo spazio toroidale
    private int aggiustaRow(int row){
    	if(row == -1)
    		return height - 1;
    	else
    		if (row == height)
    			return 0;
    		else
    			return row;
    }
    
    //aggiusta parametro 'col' per far rispettare lo spazio toroidale
    private int aggiustaCol(int col){
    	if(col == -1)
    		return width - 1;
    	else
    		if(col == width)
    			return 0;
    		else
    			return col;
    }
    
    public void dominateNeighbours(int row, int col) {	//data la cella in posizione row, col
    	int myState = getCellState(row, col);			//leggo il suo stato (myState)
        int stateDominated = myState;					//calcolo lo stato che dovra' essere 'dominato' (myState - 1)
    	if(stateDominated != 0)							//e controllo nel vicinato di moore (r=1) quale cella ha
    		stateDominated--;							//quello stato e dovra' cambiare stato in myState
    	else
    		stateDominated = possibleState - 1;
    	
        if(getCellState(aggiustaRow(row-1), aggiustaCol(col-1)) == stateDominated){
        	grid[ aggiustaRow(row-1) ][ aggiustaCol(col-1) ].setNewState(myState);
        }
    
        if(getCellState(aggiustaRow(row-1), aggiustaCol(col)) == stateDominated){
        	grid[ aggiustaRow(row-1) ][ aggiustaCol(col) ].setNewState(myState);
        }
    
        if(getCellState(aggiustaRow(row-1), aggiustaCol(col+1)) == stateDominated){
        	grid[ aggiustaRow(row-1) ][ aggiustaCol(col+1) ].setNewState(myState);
        }
    
        if(getCellState(aggiustaRow(row), aggiustaCol(col-1)) == stateDominated){
        	grid[ aggiustaRow(row) ][ aggiustaCol(col-1) ].setNewState(myState);
        }
    
        if(getCellState(aggiustaRow(row), aggiustaCol(col+1)) == stateDominated){
        	grid[ aggiustaRow(row) ][ aggiustaCol(col+1) ].setNewState(myState);
        }

        if(getCellState(aggiustaRow(row+1), aggiustaCol(col-1)) == stateDominated){
        	grid[ aggiustaRow(row+1) ][ aggiustaCol(col-1) ].setNewState(myState);
        }

        if(getCellState(aggiustaRow(row+1), aggiustaCol(col)) == stateDominated){
        	grid[ aggiustaRow(row+1) ][ aggiustaCol(col) ].setNewState(myState);
        }

        if(getCellState(aggiustaRow(row+1), aggiustaCol(col+1)) == stateDominated){
        	grid[ aggiustaRow(row+1) ][ aggiustaCol(col+1) ].setNewState(myState);
        }
        
        return;
    }

    public int getCellState(int row, int col) {
        return grid[row][col].getState();
    }

    public void update() {
        prepare();	//calcola i nuovi stati
        commit();	//e li rende effettivi
    }
    
    private void prepare() {
        for (int h=0; h<grid.length; h++){
            for (int w=0; w<grid[h].length; w++){	//scorre tutte le celle del Griffeath's Crystalline Growths
            	dominateNeighbours(h,w);			//e definisce i nuovi stati che dovranno assumere le celle
            }
        }
    }

    private void commit() {			//rende attivi in contemporanea i nuovi stati delle celle
        for (int h=0; h<grid.length; h++){
            for (int w=0; w<grid[h].length; w++){
                grid[h][w].updateState();
            }
        }
    }
}
