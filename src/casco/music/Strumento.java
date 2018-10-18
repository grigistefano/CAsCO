package casco.music;

public class Strumento implements Comparable<Strumento> {
	private String name;
	private int strumentIndex;
	private boolean percussione;
	
	private double lunghezzaNota;
	private double lunghezzaGcg;
	private int gapQuartina;
	private double delay;
	private boolean sicronizzazione;
	private int ottava;
	private int orchestraIndex;
	private String nomeScala;
	private int[][] scala;
	private int forzaOn;
	private int quartina;
	private int[] repeat;
	private String nomeRepeat;
	private int inizio;
	private int continuaPer;
	private int pausa;
	private int ripetiPer;
	
	public Strumento(){
		this("Strumento", 0, 1, 0, true);
	}
	
	public Strumento(String name,int strumentIndex, double lunghezzaNota, int gapQuartina){
		this(name, strumentIndex, lunghezzaNota, gapQuartina, true);
	}
	
	public Strumento(String name, int strumentIndex, double lunghezzaNota, int gapQuartina, boolean sincr) {
		//parametri di Default
		this.name = name;
		this.percussione = false;
		this.strumentIndex = strumentIndex;
		this.lunghezzaNota = lunghezzaNota;
		this.gapQuartina = gapQuartina;
		this.delay = 0;
		this.sicronizzazione = sincr;
		this.ottava = 0;
		this.orchestraIndex = 0;
		int[][] s = {{28, 31, 33, 35, 38, 40, 43, 45, 47, 50, 52, 55, 57, 59, 62, 64, 67, 69, 71, 74, 79, 81, 83, 86, 88, 91}};
		this.scala = s;
		this.forzaOn = 1000;
		this.lunghezzaGcg = 1;
		this.quartina = 64;
		this.repeat = null;
		this.nomeRepeat = "[Null]";
		this.inizio = 0;
		this.continuaPer = 0;
		this.pausa = 0;
		this.ripetiPer = 1;
		this.nomeScala = "";
	}
	
	public Strumento(Strumento s){		//costruttore per creare copia di una strumento
		this.name = s.getName();
		this.strumentIndex = s.getStrumentIndex();
		this.lunghezzaNota = s.getLunghezzaNota();
		this.gapQuartina = s.getGapQuartina();
		this.delay = s.getDelay();
		this.sicronizzazione = s.isSicronizzazione();
		this.ottava = s.getOttava();
		this.orchestraIndex = s.getOrchestraIndex();
		int[][] scalaInput = s.getScala();
		int[][] newScala = new int[scalaInput.length][scalaInput[0].length];
		for(int i = 0; i < scalaInput.length; i++){
			for(int j = 0; j < scalaInput[0].length; j++){
				newScala[i][j] = scalaInput[i][j];
			}
		}
		this.scala = newScala;
		this.forzaOn = s.getForzaOn();
		this.lunghezzaGcg = s.getLunghezzaGcg();
		this.quartina = s.getQuartina();
		if(s.getRepeat() != null) {
			int[] repeatInput = s.getRepeat();
			int[] newRepeat = new int[repeatInput.length];
			for(int i = 0; i < repeatInput.length; i++){
				newRepeat[i] = repeatInput[i];
			}
			this.repeat = newRepeat;
		}else
			this.repeat = null;
		this.nomeRepeat = s.getNomeRepeat();
		this.inizio = s.getInizio();
		this.continuaPer = s.getContinuaPer();
		this.pausa = s.getPausa();
		this.ripetiPer = s.getRipetiPer();
		this.nomeScala = s.getNomeScala();
		this.percussione = s.isPercussione();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStrumentIndex() {
		return strumentIndex;
	}

	public void setStrumentIndex(int strumentIndex) {
		this.strumentIndex = strumentIndex;
	}

	public double getLunghezzaNota() {
		return lunghezzaNota;
	}

	public void setLunghezzaNota(double lunghezzaNota) {
		this.lunghezzaNota = lunghezzaNota;
	}

	public int getGapQuartina() {
		return gapQuartina;
	}

	public void setGapQuartina(int gapQartina) {
		this.gapQuartina = gapQartina;
	}

	public double getDelay() {
		return delay;
	}

	public void setDelay(double delay) {
		this.delay = delay;
	}

	public boolean isSicronizzazione() {
		return sicronizzazione;
	}

	public void setSicronizzazione(boolean sicronizzazione) {
		this.sicronizzazione = sicronizzazione;
	}

	public int getOttava() {
		return ottava;
	}

	public void setOttava(int ottava) {
		this.ottava = ottava;
	}
	
	public int getOrchestraIndex() {
		return orchestraIndex;
	}

	public void setOrchestraIndex(int orchestraIndex) {
		this.orchestraIndex = orchestraIndex;
	}
	
	public int[][] getScala() {
		return scala;
	}
	
	public String getNomeScala() {
		return nomeScala;
	}
	
	public void setScala(int[][] scala, String nomeScala) {
		this.scala = scala;
		this.nomeScala = nomeScala;
	}
	
	public double getLunghezzaGcg() {
		return lunghezzaGcg;
	}

	public void setLunghezzaGcg(double lunghezzaGcg) {
		this.lunghezzaGcg = lunghezzaGcg;
	}

	public int getForzaOn() {
		return forzaOn;
	}

	public void setForzaOn(int forzaOn) {
		this.forzaOn = forzaOn;
	}
	
	public int getQuartina() {
		return quartina;
	}

	public void setQuartina(int quartina) {
		this.quartina = quartina;
	}
	
	public int[] getRepeat() {
		return repeat;
	}
	
	public String getNomeRepeat() {
		return nomeRepeat;
	}
	
	public void setRepeat(int[] repeat, String nomeRepeat) {
		this.repeat = repeat;
		this.nomeRepeat = nomeRepeat;
	}
	
	public int getInizio() {
		return inizio;
	}

	public void setInizio(int inizio) {
		this.inizio = inizio;
	}

	public int getContinuaPer() {
		return continuaPer;
	}

	public void setContinuaPer(int continuaPer) {
		this.continuaPer = continuaPer;
	}
	
	public int getPausa() {
		return pausa;
	}

	public void setPausa(int pausa) {
		this.pausa = pausa;
	}

	public int getRipetiPer() {
		return ripetiPer;
	}

	public void setRipetiPer(int ripetiPer) {
		this.ripetiPer = ripetiPer;
	}
	
	public boolean isPercussione() {
		return percussione;
	}

	public void setPercussione(boolean percussione) {
		this.percussione = percussione;
	}

	@Override
	public int compareTo(Strumento s1) {
		if( orchestraIndex < s1.getOrchestraIndex())
			return -1;
		else
			if(orchestraIndex == s1.getOrchestraIndex())
				return 0;
			else
				return 1;
	}
	
	@Override
	public String toString(){
		String res = "";
		res = res + "name: " + name;
		res = res + "\nstrumentIndex: " + strumentIndex;
		res = res + "\npercussione: " + percussione;
		res = res + "\nslunghezzaNota: " + lunghezzaNota;
		res = res + "\nlunghezzaGcg: " + lunghezzaGcg;
		res = res + "\ngapQuartina: " + gapQuartina;
		res = res + "\ndelay: " + delay;
		res = res + "\nsicronizzazione: " + sicronizzazione;
		res = res + "\nottava: " + ottava;
		res = res + "\norchestraIndex: " + orchestraIndex;
		res = res + "\nnomeScala: " + nomeScala;
		if(scala != null)
			res = res + "\nscala: " + scala.toString();
		else
			res = res + "\nscala: NULL";
		res = res + "\nforzaOn: " + forzaOn;
		res = res + "\nquartina: " + quartina;
		if(repeat != null)
			res = res + "\nrepeat: " + repeat.toString();
		else
			res = res + "\nrepeat: NULL";
		res = res + "\nnomeRepeatx: " + nomeRepeat;
		res = res + "\ninizio: " + inizio;
		res = res + "\ncontinuaPer: " + continuaPer;
		res = res + "\npausa: " + pausa;
		res = res + "\nripetiPer: " + ripetiPer;
		return res;
	}
}
