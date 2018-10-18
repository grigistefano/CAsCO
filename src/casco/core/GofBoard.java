package casco.core;

public class GofBoard {
    private GofCell[][] grid;
    private int height=3;
    private int width=3;

    public GofBoard(GofCell[][] grid) {		//costruttore Game of Life data una griglia
        this.grid = grid;
        height = width = grid.length;
    }

    public GofBoard(int height, int width, double p) {	//costruttore del Game of Life date le dimensioni
    													//e la probabilità che una cella inizialmente sia attiva
        this.height=height;
        this.width = width;
        grid = new GofCell[height][width];
        
        for (int h = 0; h < grid.length; h++){			//crea una nuova griglia in base agli input
            for (int w = 0; w < grid[h].length; w++){
                grid[h][w] = new GofCell();
                if (Math.random() <= p){
                    grid[h][w].setNewState(true);
                    grid[h][w].updateState();
                }
            }
        }
    }
    
    public GofBoard(GofBoard gofBoard){		//costruttore per copiare Gof
    	this.height = gofBoard.getSize();
        this.width = gofBoard.getSize();
        int size = gofBoard.getGrid().length;
    	this.grid = new GofCell[size][size];
    	for(int i = 0; i < size; i++){
    		for(int j = 0; j < size; j++){
    			this.grid[i][j] = new GofCell(gofBoard.getGrid()[i][j].getState());
    		}
    	}
    }
    
    public GofCell[][] getGrid() {
        return grid;
    }
    
    public int getSize() {
        return width;
    }
    
    //aggiusta row per far rispettare lo spazio toroidale
    private int aggiustaRow(int row){
    	if(row == -1)
    		return height - 1;
    	else
    		if (row == height)
    			return 0;
    		else
    			return row;
    }
    
    //aggiusta col per far rispettare lo spazio toroidale
    private int aggiustaCol(int col){
    	if(col == -1)
    		return width - 1;
    	else
    		if(col == width)
    			return 0;
    		else
    			return col;
    }
    
    //conta quante sono le celle attive nel vicinato di moore (r=1) della cella in posizione row, col
    public int neighboursCountAt(int row, int col) {
        int sum=0;
        
        if(isAlive(aggiustaRow(row-1), aggiustaCol(col-1))){ //1
            sum++;
        }
        if(isAlive(aggiustaRow(row-1), aggiustaCol(col))){ //2
        	sum++;
        }
        if(isAlive(aggiustaRow(row-1), aggiustaCol(col+1))){ //3
            sum++;
        }
        if(isAlive(aggiustaRow(row), aggiustaCol(col-1))){ //4
        	sum++;
        }
        if(isAlive(aggiustaRow(row), aggiustaCol(col+1))){ //6
            sum++;
        }
        if(isAlive(aggiustaRow(row+1), aggiustaCol(col-1))){ //7
            sum++;
        }
        if(isAlive(aggiustaRow(row+1), aggiustaCol(col))){ //8
        	sum++;
        }
        if(isAlive(aggiustaRow(row+1), aggiustaCol(col+1))){ //9
            sum++;
        }

        return sum;
    }
    
    public boolean isAlive(int row, int col) {
        return grid[row][col].getState();
    }

    public void update() {
        prepare();	//calcola i nuovi stati
        commit();	//e li rende effettivi
    }

    private void prepare() {
        for (int h = 0; h < grid.length; h++){
            for (int w = 0; w < grid[h].length; w++){	//scorre tutte le celle del Game of Life
            	
                int nr = neighboursCountAt(h,w);        //conta le cella attive nel vicinato della cella analizzata
                
                if (nr < 2){							//se sono meno di 2 la cella muore (loneliness)
                	grid[h][w].setNewState(false);
                }else 
                	if (nr > 3){						//se sono più di 3 la cella muore (crowding)
                		grid[h][w].setNewState(false);
                	}else
                		if (nr == 3){					//se sono 3 la cella nasce (born)
                			grid[h][w].setNewState(true);
                		}else
                			if (nr == 2){				//se sono 2 la cella rimane viva o rimane morta (unchanged)
                				grid[h][w].setNewState(grid[h][w].getState());
                			} 
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
