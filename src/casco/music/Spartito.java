package casco.music;

import java.util.ArrayList;

import casco.core.*;
import casco.music.Nota;
import casco.music.Strumento;

public class Spartito {
	private int reference = 0;
	private ArrayList<ArrayList<Nota>> spartito= new ArrayList<ArrayList<Nota>>();
	private Strumento strumento;
	private ArrayList<ArrayList<int[]>> flow;

	public Spartito(Strumento s){
		this.strumento = s;
	}

	public void estrazione(GofBoard gofBoard, GcgBoard gcgBoard) {	//input: coppia di automi Gof e Gcg associati allo stesso strumento
		ArrayList<Nota> step = new ArrayList<Nota>();	//creo ArrayList<Nota> step che conterra le note estratte da una singola schermata del Gof

		GofCell[][] gofGrid = gofBoard.getGrid();
		GcgCell[][] gcgGrid = gcgBoard.getGrid();

		boolean a, b, c, d, m, n, o, p;
		int width = gofGrid[0].length;
		String tm[] = new String[2];
		Nota newNota;
		int middle;
		for(int j=0; j < width; j++) {
			for(int i=0; i < width; i++) {
				if (gofGrid[i][j].getState()) {				//scorro tutte le celle ATTIVE del Gof
					
					middle = reference + (width-i) + j+1;	//ottengo middle sommando le righe e le colonne della cella
															//come se fosse in un piano cartesiano
					int ottava = strumento.getOttava();
					
					if(strumento.getScala().length == 1) {	//se la scala dello strumento è composta da singole note:
						
						middle = middle % strumento.getScala()[0].length; //utilizzo middle come indice per scegliere una nota tra quelle della scala
						int bottom;
						int upper;
						
						bottom = (middle + (width-i)) % strumento.getScala()[0].length;	//ottengo bottom sommando middle e righe della cella e lo utilizzo per scegliere un'altra nota dalla scala
						upper = (middle + j+1) % strumento.getScala()[0].length;		//ottengo upper sommando middle e colonne della cella e lo utilizzo per scegliere un'altra nota dalla scala
						
						//creo nuova nota in base alle tre note estratte
						newNota = new Nota(strumento, strumento.getScala()[0][middle] + ottava, strumento.getScala()[0][bottom] + ottava, strumento.getScala()[0][upper] + ottava);
						
					}else {									//se la scala dello strumento è composta da triple di note:
						middle = middle % strumento.getScala().length; //utilizzo middle come indice per scegliere una tripla di note tra quelle della scala
						//creo nuova nota in base alla tripla scelta
						newNota = new Nota(strumento, strumento.getScala()[middle][0] + ottava, strumento.getScala()[middle][1] + ottava, strumento.getScala()[middle][2] + ottava);
					}
					
					//osservo il "vicinato" della cella per ottenere i parametri necessari a definire la Time Morphology
					a = false;
					if (j != 0){
						if(gofGrid[i][j-1].getState()){
							a = true;
						}
					}

					b = false;
					if (j != width-1){
						if(gofGrid[i][j+1].getState()){
							b = true;
						}
					}

					c = false;
					if (i != width-1){
						if(gofGrid[i+1][j].getState()){
							c = true;
						}
					}

					d = false;
					if (i != 0){
						if(gofGrid[i-1][j].getState()){
							d = true;
						}
					}

					m = false;
					if (i != 0 && j != 0){
						if(gofGrid[i-1][j-1].getState()){
							m = true;
						}
					}

					n = false;
					if (i != width-1 && j != width-1){
						if(gofGrid[i+1][j+1].getState()){
							n = true;
						}
					}

					o = false;
					if (i != width-1 && j != 0){
						if(gofGrid[i+1][j-1].getState()){
							o = true;
						}
					}

					p = false;
					if (i != 0 && j != width - 1){
						if(gofGrid[i-1][j+1].getState()){
							p = true;
						}
					}

					tm = calculateTimeMorphology(a, b, c, d, m, n, o, p);	//calcolo Time Morphology
					newNota.setStatoGcg(gcgGrid[i][j].getState());			//leggo il valore della cella del Gcg nella stessa 
																			//posizione di quella che si sta analizzando in questa iterazione
					newNota.setTimeMorfology(tm);
					step.add(newNota);										//aggiungo nota ad ArrayList<Nota> step
				}
			}
		}
		spartito.add(step);			//aggiungo step ad ArrayList<ArrayList<Nota>> spartito
	}
	
	public String[] calculateTimeMorphology(boolean a, boolean b, boolean c, boolean d, boolean m, boolean n, boolean o, boolean p) {
		
		String timeMorph[] = new String[2];
		String s1 = (a | d) ? "1" : "0";
		String s2 = (b | c) ? "1" : "0";
		String s3 = (c | b) ? "1" : "0";
		String s4 = (d | a) ? "1" : "0";

		String tgg = s1 + s2 + s3 + s4;

		s1 = (m | p) ? "1" : "0";
		s2 = (n | o) ? "1" : "0";
		s3 = (o | n) ? "1" : "0";
		s4 = (p | m) ? "1" : "0";

		String dur = s1 + s2 + s3 + s4;

		switch(tgg) {
		case "0000":
			//timeMorph[0] = "B[UM]";
			timeMorph[0] = "UMB";
			break;
		case "0001":
			//timeMorph[0] = "[UMB]";
			timeMorph[0] = "BUM";
			break;
		case "0010":
			timeMorph[0] = "BUM";
			break;
		case "0011":
			timeMorph[0] = "UMB";
			break;
		case "0101":
			timeMorph[0] = "BMU";
			break;
		case "0110":
			timeMorph[0] = "UBM";
			break;
		case "0111":
			timeMorph[0] = "MBU";
			break;
		case "1001":
			//timeMorph[0] = "U[MB]";
			timeMorph[0] = "MBU";
			break;
		case "1011":
			timeMorph[0] = "MUB";
			break;
		case "1111":
			//timeMorph[0] = "M[UB]";
			timeMorph[0] = "MUB";
			break;

		}
		//non più utilizzati, deprecati!
		switch(dur) {
		case "0000":
			timeMorph[1] = "B[UM]";
			break;
		case "0001":
			timeMorph[1] = "[UMB]";
			break;
		case "0010":
			timeMorph[1] = "BUM";
			break;
		case "0011":
			timeMorph[1] = "UMB";
			break;
		case "0101":
			timeMorph[1] = "BMU";
			break;
		case "0110":
			timeMorph[1] = "UBM";
			break;
		case "0111":
			timeMorph[1] = "MBU";
			break;
		case "1001":
			timeMorph[1] = "U[MB]";
			break;
		case "1011":
			timeMorph[1] = "MUB";
			break;
		case "1111":
			timeMorph[1] = "M[UB]";
			break;
		}

		return timeMorph;
	}

	public int getSpartitoSize(){
		int sum = 0;
		for(int i = 0; i < spartito.size(); i++){
			sum += spartito.get(i).size();
		}
		return sum;
	}

	public void translate(){	//traduce le note estratte in una struttura dati adatta per essere suonata
		if(strumento.isSicronizzazione())
			translateSincrona();
		else
			translateAsincrona();
	}
	
	public void translateSincrona(){
		int flowSize = getSpartitoSize() * (strumento.getQuartina() * 10);
		flow = new ArrayList<ArrayList<int[]>>();	//crea flow
		int index = strumento.getInizio();
		int fine;
		if(strumento.getContinuaPer() == 0 && strumento.getRipetiPer() > 0)
			fine = flowSize - 1;
		else
			fine = Math.min(index + strumento.getRipetiPer()*(strumento.getContinuaPer() + strumento.getPausa()), flowSize - 1);
		
		for(int k = 0; k < flowSize; k++)
			flow.add(null);

		int array[];
		int repeat = 1;
		int max = 0;
		int prossimoCambio = index + strumento.getContinuaPer();
		boolean suona = true;
		boolean inserisci;
		int pos;
		index = index + (int)(strumento.getGapQuartina());
		int length1 = spartito.size();
		for(int i = 0; i < length1 && index < fine; i++){     //scorre le schermate del Game of Life di questo strumento
			int length2 = spartito.get(i).size();
			for(int j = 0; j < length2 && index <= fine; j++){		//scorre le Note ricavate dalle celle attive di tale schermata
																	//per ognuna di esse, in base ai parametri dello strumento e ai valori 
				Nota nota = spartito.get(i).get(j);					//Start e End della nota, vengono posizionati nella corretta posizione di flow
																	//degli array di int[] valorizzati con valori che indicano se si tratta di ON
				repeat = 1;											//o OFF, quale strumento e quale nota suonare.
				if(strumento.getRepeat() != null)
					repeat = strumento.getRepeat()[ nota.getStatoGcg() % strumento.getRepeat().length ] * 4;
				
				for(int k = 0; k < repeat && index <= fine; k++){
					inserisci = true;
					if(k % 2 != 0)
						inserisci = false;
					
					if(inserisci && suona){
						array = new int[2];
						array[0] = nota.getB();
						array[1] = strumento.getOrchestraIndex();
						int startB = nota.getbStart();
						pos = index + startB;
						if(flow.get(pos) == null)
							flow.set(pos, new ArrayList<int[]>());
						flow.get(pos).add(array);
		
						array = new int[2];
						array[0] = nota.getM();
						array[1] = strumento.getOrchestraIndex();
						int startM = nota.getmStart();
						pos = index + startM;
						if(flow.get(pos) == null)
							flow.set(pos, new ArrayList<int[]>());
						flow.get(pos).add(array);
		
						array = new int[2];
						array[0] = nota.getU();
						array[1] = strumento.getOrchestraIndex();
						int startU = nota.getuStart();
						pos = index + startU;
						if(flow.get(pos) == null)
							flow.set(pos, new ArrayList<int[]>());
						flow.get(pos).add(array);
		
						array = new int[2];
						array[0] = - nota.getB();
						array[1] = strumento.getOrchestraIndex();
						int endB = nota.getbEnd();
						pos = index + endB;
						if(flow.get(pos) == null)
							flow.set(pos, new ArrayList<int[]>());
						flow.get(pos).add(array);
						max = Math.max(max, pos);
		
						array = new int[2];
						array[0] = - nota.getM();
						array[1] = strumento.getOrchestraIndex();
						int endM = nota.getmEnd();
						pos = index + endM;
						if(flow.get(pos) == null)
							flow.set(pos, new ArrayList<int[]>());
						flow.get(pos).add(array);
						max = Math.max(max, pos);
		
						array = new int[2];
						array[0] = - nota.getU();
						array[1] = strumento.getOrchestraIndex();
						int endU = nota.getuEnd();
						pos = index + endU;
						if(flow.get(pos) == null)
							flow.set(pos, new ArrayList<int[]>());
						flow.get(pos).add(array);
						max = Math.max(max, pos);
					}else{
						pos = index + nota.getbEnd();
						max = Math.max(max, pos);
						pos = index + nota.getmEnd();
						max = Math.max(max, pos);
						pos = index + nota.getuEnd();
						max = Math.max(max, pos);
					}
					
					//posiziona lo start della nota successiva in contemporanea al prossimo Beat
					int resto = (max - (int)strumento.getGapQuartina()) % (strumento.getQuartina());
					if(resto == 0)
						index = max;
					else
						index = max + (strumento.getQuartina() - resto);
					
					if(index >= prossimoCambio)		//alterna i periodi in cui lo strumento deve suonare o è in pausa
						if(suona){
							if(index >= prossimoCambio + strumento.getPausa())
								prossimoCambio = prossimoCambio + strumento.getPausa() + strumento.getContinuaPer();
							else{
								suona = false;
								prossimoCambio = prossimoCambio + strumento.getPausa();
							}
						}else{
							if(index >= prossimoCambio + strumento.getContinuaPer())
								prossimoCambio = prossimoCambio + strumento.getContinuaPer() + strumento.getPausa();
							else{
								suona = true;
								prossimoCambio = prossimoCambio + strumento.getContinuaPer();
							}
						}
				}
			}
		}
	}

	public void translateAsincrona(){
		int flowSize = getSpartitoSize() * (strumento.getQuartina() * 10);
		flow = new ArrayList<ArrayList<int[]>>();	//crea flow
		int index = strumento.getInizio();
		int fine;
		if(strumento.getContinuaPer() == 0 && strumento.getRipetiPer() > 0)
			fine = flowSize - 1;
		else
			fine = Math.min(index + strumento.getRipetiPer()*(strumento.getContinuaPer() + strumento.getPausa()), flowSize - 1);
		
		for(int k = 0; k < flowSize; k++)
			flow.add(null);

		int array[];
		int max = 0;
		int prossimoCambio = index + strumento.getContinuaPer();
		boolean suona = true;
		int pos;
		int length1 = spartito.size();
		for(int i = 0; i < length1 && index < fine; i++){		//scorre le schermate del Game of Life di questo strumento
			int length2 = spartito.get(i).size();
			for(int j = 0; j < length2 && index <= fine; j++){	//scorre le Note ricavate dalle celle attive di tale schermata
																//per ognuna di esse, in base ai parametri dello strumento e ai valori 
				Nota nota = spartito.get(i).get(j);				//Start e End della nota, vengono posizionati nella corretta posizione di flow
																//degli array di int[] valorizzati con valori che indicano se si tratta di ON
																//o OFF, quale strumento e quale nota suonare.
				if(suona){
					array = new int[2];
					array[0] = nota.getB();
					array[1] = strumento.getOrchestraIndex();
					int startB = nota.getbStart();
					pos = index + startB;
					if(flow.get(pos) == null)
						flow.set(pos, new ArrayList<int[]>());
					flow.get(pos).add(array);
	
					array = new int[2];
					array[0] = nota.getM();
					array[1] = strumento.getOrchestraIndex();
					int startM = nota.getmStart();
					pos = index + startM;
					if(flow.get(pos) == null)
						flow.set(pos, new ArrayList<int[]>());
					flow.get(pos).add(array);
	
					array = new int[2];
					array[0] = nota.getU();
					array[1] = strumento.getOrchestraIndex();
					int startU = nota.getuStart();
					pos = index + startU;
					if(flow.get(pos) == null)
						flow.set(pos, new ArrayList<int[]>());
					flow.get(pos).add(array);
	
	
					array = new int[2];
					array[0] = - nota.getB();
					array[1] = strumento.getOrchestraIndex();
					int endB = nota.getbEnd();
					pos = index + endB;
					if(flow.get(pos) == null)
						flow.set(pos, new ArrayList<int[]>());
					flow.get(pos).add(array);
					max = Math.max(max, pos);
	
					array = new int[2];
					array[0] = - nota.getM();
					array[1] = strumento.getOrchestraIndex();
					int endM = nota.getmEnd();
					pos = index + endM;
					if(flow.get(pos) == null)
						flow.set(pos, new ArrayList<int[]>());
					flow.get(pos).add(array);
					max = Math.max(max, pos);
	
					array = new int[2];
					array[0] = - nota.getU();
					array[1] = strumento.getOrchestraIndex();
					int endU = nota.getuEnd();
					pos = index + endU;
					if(flow.get(pos) == null)
						flow.set(pos, new ArrayList<int[]>());
					flow.get(pos).add(array);
					max = Math.max(max, pos);
				}else{
					pos = index + nota.getbEnd();
					max = Math.max(max, pos);
					pos = index + nota.getmEnd();
					max = Math.max(max, pos);
					pos = index + nota.getuEnd();
					max = Math.max(max, pos);
				}
				
				//lo Start della nota successiva si posiziona in contemporane all'End dell'ultima nota suonata
				index = max;
				int resto = index % (strumento.getQuartina());	//se la chiusura di una nota si avvicina alla quartina
				if(resto >= (strumento.getQuartina()) - 8)		//la nota successiva comincia sulla quartina per rispettare il tempo
					index = max + (strumento.getQuartina() - resto);
				
				//alterna i periodi in cui lo strumento deve suonare o è in pausa
				if(index >= prossimoCambio)
					if(suona){
						if(index >= prossimoCambio + strumento.getPausa())
							prossimoCambio = prossimoCambio + strumento.getPausa() + strumento.getContinuaPer();
						else{
							suona = false;
							prossimoCambio = prossimoCambio + strumento.getPausa();
						}
					}else{
						if(index >= prossimoCambio + strumento.getContinuaPer())
							prossimoCambio = prossimoCambio + strumento.getContinuaPer() + strumento.getPausa();
						else{
							suona = true;
							prossimoCambio = prossimoCambio + strumento.getContinuaPer();
						}
					}
			}
		}
	}
	
	public void printList() {		
		int length = spartito.size();
		for (int i = 0; i < length; i++){
			int length2 = spartito.get(i).size();
			for (int j = 0; j < length2; j++){
				Nota nota = spartito.get(i).get(j);
				System.out.println(i + ": " + j + " ->  |" + nota.getM() + " " + nota.getB() + " " + nota.getU() + "|  \ttimeMorfology: " + nota.getTimeMorfology()[0] + "; " + nota.getTimeMorfology()[1] + "  \tstrumento: " + nota.getStrumento());
				System.out.println("\t M:" + nota.getmStart() + " : " + nota.getmEnd() + "\t B:" + nota.getbStart() + " : " + nota.getbEnd() + "\t U:" + nota.getuStart() + " : " + nota.getuEnd());
			}
		}
	}

	public void printFlow(int stop){
		if (flow.size() < stop)
			stop = flow.size();
		for(int i = 0; i<stop;i++) {
			ArrayList<int[]> beat = flow.get(i);

			if(i % strumento.getQuartina() == 0)
				System.out.print(i +" Q -> ");
			else
				System.out.print(i +" -> ");
			if(beat != null) {
				for(int j = 0; j< beat.size();j++) {
					int array[] = beat.get(j);

					System.out.print(j + ": [" + array[0] + ", " + array[1] + "] ");
				}
				System.out.println();
			}else
				System.out.println("X");
		}
	}

	public ArrayList<ArrayList<Nota>> getSpartito() {
		return spartito;
	}

	public void setSpartito(ArrayList<ArrayList<Nota>> spartito) {
		this.spartito = spartito;
	}

	public ArrayList<ArrayList<int[]>> getFlow() {
		return flow;
	}

	public void setFlow(ArrayList<ArrayList<int[]>> flow) {
		this.flow = flow;
	}

	public Strumento getStrumento() {
		return strumento;
	}

	public void setStrumento(Strumento strumento) {
		this.strumento = strumento;
	}
	
}


