package casco.music;

import java.util.Arrays;

public class Nota {
	private int b;
	private int m;
	private int u;
	
	private int statoGcg;
	private String[] timeMorfology;
	private Strumento strumento;
	private int bStart;
	private int bEnd;
	private int mStart;
	private int mEnd;
	private int uStart;
	private int uEnd;
	
	public int[] crome = {16,16,32,32,64,64};

	public Nota(Strumento s, int reference, int b, int u) {		//cosrtuttore Nota
		super();
		int[] sorted = {reference, b, u};
		Arrays.sort(sorted);			//le tre note in input vengono ordinate per far sì che b m u siano crescenti
		this.b = sorted[0];
		this.u = sorted[2];
		this.m = sorted[1];
		
		this.strumento = s;				//assegno lo strumento alla nota
		int quart = s.getQuartina();	
		int[] array = new int[6];		//calcole le crome in base alla quartina dello strumento
		array[0] = quart / 4;
		array[1] = quart / 4;
		array[2] = quart / 2;
		array[3] = quart / 2;
		array[4] = quart / 1;
		array[5] = quart / 1;
		this.crome = array;
	}

	public Strumento getStrumento() {
		return strumento;
	}

	public void setStrumento(Strumento strumento) {
		this.strumento = strumento;
	}

	public void setTimeMorfology(String[] timeMorfology){	//in base ai parametri dello strumento, al valore del Gcg
		this.timeMorfology = timeMorfology;					//e alla Time Morphology vengono calcolati i tempi di Star e End delle tre
		if(strumento.isSicronizzazione())					//note associate alla classe Nota
			setTimesSincrona();
		else
			setTimesAsincrona();
	}
	
	public void setTimesAsincrona(){	//se lo strumento è asincrono i tempi vengono ricavati dal Gcg
		int duration[] = new int[3];
		duration[0] = crome[(int)(statoGcg % crome.length)];
		duration[1] = duration[0] + crome[(int)(statoGcg % crome.length)];
		duration[2] = duration[1] + crome[(int)(statoGcg % crome.length)];

		switch(timeMorfology[0]) {
		case "B[UM]":
			bStart = 0;
			bEnd = duration[0];
			mStart = duration[0];
			mEnd = duration[1];
			uStart = mStart;
			uEnd = mEnd;
			//u = u +2;
			break;
		case "[UMB]":
			bStart = 0;
			bEnd = duration[0];
			mStart = bStart;
			mEnd = bEnd;
			uStart = mStart;
			uEnd = mEnd;
			//u = u + 2;
			//b = b - 2;
			break;
		case "BUM":
			bStart = 0;
			bEnd = duration[0];
			mStart = duration[1];
			mEnd = duration[2];
			uStart = duration[0];
			uEnd = duration[1];
			break;
		case "UMB":
			bStart = duration[1];
			bEnd = duration[2];
			mStart = duration[0];
			mEnd = duration[1];
			uStart = 0;
			uEnd = duration[0];
			break;
		case "BMU":
			bStart = 0;
			bEnd = duration[0];
			mStart = duration[0];
			mEnd = duration[1];
			uStart = duration[1];
			uEnd = duration[2];
			break;
		case "UBM":
			bStart = duration[0];
			bEnd = duration[1];
			mStart = duration[1];
			mEnd = duration[2];
			uStart = 0;
			uEnd = duration[0];
			break;
		case "MBU":
			bStart = duration[0];
			bEnd = duration[1];
			mStart = 0;
			mEnd = duration[0];
			uStart = duration[1];
			uEnd = duration[2];
			break;
		case "U[MB]":
			bStart = duration[0];
			bEnd = duration[1];
			mStart = bStart;
			mEnd = bEnd;
			uStart = 0;
			uEnd = duration[0];
			//b = b - 2;
			break;
		case "MUB":
			bStart = duration[1];
			bEnd = duration[2];
			mStart = 0;
			mEnd = duration[0];
			uStart = duration[0];
			uEnd = duration[1];
			break;
		case "M[UB]":
			bStart = duration[0];
			bEnd = duration[1];
			mStart = 0;
			mEnd = duration[0];
			uStart = duration[0];
			uEnd = duration[1];
			break;
		default:
			bStart = duration[0];
			bEnd = duration[1];
			mStart = bStart;
			mEnd = bEnd;
			uStart = mStart;
			uEnd = mEnd;
			break;
		}

	}
	
	public void setTimesSincrona(){		//se lo strumento è sincrono i tempi vengono calcolati in base al Gcg e ai parametri dello strumento
		int start = (int)(crome[(int)(statoGcg % crome.length)] * strumento.getDelay());
		int end = start + (int)((strumento.getQuartina() * strumento.getLunghezzaNota()) + (int)(crome[(int)(statoGcg % crome.length)] * strumento.getLunghezzaGcg()));
		
		bStart = start;
		mStart = start;
		uStart = start;
		bEnd = end;
		mEnd = end;
		uEnd = end;
	}

	public int getB() {
		return b;
	}


	public int getM() {
		return m;
	}


	public int getU() {
		return u;
	}

	public String[] getTimeMorfology(){
		return timeMorfology;
	}

	public int getbStart() {
		return bStart;
	}

	public void setbStart(int bStart) {
		this.bStart = bStart;
	}

	public int getbEnd() {
		return bEnd;
	}

	public void setbEnd(int bEnd) {
		this.bEnd = bEnd;
	}

	public int getmStart() {
		return mStart;
	}

	public void setmStart(int mStart) {
		this.mStart = mStart;
	}

	public int getmEnd() {
		return mEnd;
	}

	public void setmEnd(int mEnd) {
		this.mEnd = mEnd;
	}

	public int getuStart() {
		return uStart;
	}

	public void setuStart(int uStart) {
		this.uStart = uStart;
	}

	public int getuEnd() {
		return uEnd;
	}

	public void setuEnd(int uEnd) {
		this.uEnd = uEnd;
	}

	public int getStatoGcg() {
		return statoGcg;
	}

	public void setStatoGcg(int lunghezzaGcg) {
		this.statoGcg = lunghezzaGcg;
	}

	public void setB(int b) {
		this.b = b;
	}

	public void setM(int m) {
		this.m = m;
	}

	public void setU(int u) {
		this.u = u;
	}
}
